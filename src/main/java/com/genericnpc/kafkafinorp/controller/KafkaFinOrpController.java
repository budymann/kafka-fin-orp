package com.genericnpc.kafkafinorp.controller;

import com.example.Sensor;
import com.genericnpc.kafkafinorp.dataaccess.SensorAttachmentDab;
import com.genericnpc.kafkafinorp.dataaccess.SensorCategory;
import com.genericnpc.kafkafinorp.dataaccess.SensorDab;
import com.genericnpc.kafkafinorp.dataaccess.SensorModule;
import com.genericnpc.kafkafinorp.model.SensorCategoryDto;
import com.genericnpc.kafkafinorp.repository.SensorCategoryRepository;
import com.genericnpc.kafkafinorp.repository.SensorRepository;
import com.genericnpc.kafkafinorp.service.PersistenceService;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationRunner;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.BatchListenerFailedException;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RestController
public class KafkaFinOrpController {
    BlockingQueue<Message<Sensor>> unbounded = new LinkedBlockingQueue<>();
    private final EmitterProcessor<Message<Sensor>> processor = EmitterProcessor.create();

    private int index = 0;

    @Autowired
    private StreamBridge streamBridge;

    @Autowired
    private PersistenceService persistenceService;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    SensorRepository sensorRepository;

    @Autowired
    SensorCategoryRepository sensorCategoryRepository;

    private TransactionTemplate transactionTemplate;

    @PersistenceContext
    private EntityManager em;


    @PostConstruct
    public void fun(){
        transactionTemplate = new TransactionTemplate(transactionManager);
    }

    private Sensor randomSensor() {
        Random random = new Random();
        Sensor sensor = new Sensor();
        sensor.setAcceleration(random.nextFloat() * 10);
        sensor.setVelocity(random.nextFloat() * 100);
        sensor.setTemperature(random.nextFloat() * 50);
        return sensor;
    }


    @RequestMapping(value={"/message"})
    @ResponseBody
    public String getMessage(){
        for(int i = 0; i < 5; i++) {
            var a = MessageBuilder.withPayload(randomSensor()).build();
            processor.onNext(a);
            //unbounded.offer(a);
        }
        return "Sent mock event";
    }

    @RequestMapping(value={"/dbtest"})
    @ResponseBody
    public String dbtest(){

        var sensor = randomSensor();
        var sensorDab = new SensorDab();
        BeanUtils.copyProperties(sensor, sensorDab);
        var sensorAttachmentDab = new SensorAttachmentDab();
        sensorAttachmentDab.setModuleSlot(5);
        sensorAttachmentDab.setSensorDab(sensorDab);
        var sensorModuleOne = new SensorModule();
        sensorModuleOne.setName("ModuleOne");
        var sensorModuleTwo = new SensorModule();
        sensorModuleTwo.setName("ModuleTwo");
        sensorAttachmentDab.addSensorModule(sensorModuleOne);
        sensorAttachmentDab.addSensorModule(sensorModuleTwo);
        sensorDab.setSensorAttachmentDab(sensorAttachmentDab);



        var sensorTwo = randomSensor();
        var sensorDabTwo = new SensorDab();
        BeanUtils.copyProperties(sensorTwo, sensorDabTwo);

        var sensorAttachmentDabTwo = new SensorAttachmentDab();
        sensorAttachmentDabTwo.setModuleSlot(3);

        persistenceService.save(sensorDab);


        return "Hello";
    }

    @RequestMapping(value={"/dbtest2"})
    @ResponseBody
    public String dbtest2(){
        em.getEntityManagerFactory().getCache().evictAll();
        em.clear();
        var z = persistenceService.findById(1);

        return "COK";
    }

    @RequestMapping(value={"/dbtestget"})
    @ResponseBody
    public String dbtestget(){



        var fromDbSensorDab = sensorRepository.findAll().get(0);
        var fromDbSensorAtachmentDab = fromDbSensorDab.getSensorAttachmentDab();

        var sensor = new SensorDab();
        sensor.setSensorId(fromDbSensorDab.getSensorId());
        sensor.setAcceleration(fromDbSensorDab.getAcceleration());
        sensor.setName(fromDbSensorDab.getName());
        sensor.setVelocity(fromDbSensorDab.getVelocity());

        var sensorAttachmentDab = new SensorAttachmentDab();
        sensorAttachmentDab.setModuleSlot(3);
        sensorAttachmentDab.setSensorAttachmentId(fromDbSensorAtachmentDab.getSensorAttachmentId());
        sensorAttachmentDab.setSensorDab(sensor);

        sensor.setSensorAttachmentDab(sensorAttachmentDab);

        var sensorModuleDab = new SensorModule();
        sensorModuleDab.setName("WANAMEKR");
        sensorModuleDab.setSensorAttachmentDab(sensorAttachmentDab);

        sensorAttachmentDab.addSensorModule(sensorModuleDab);

        sensorRepository.save(sensor);

        return "Hello";
    }

    @RequestMapping(value={"/dbtestrec"})
    @ResponseBody
    @Transactional
    public String dbtestrec(){
        var swag = new SensorCategoryDto();
        swag.setName("Swag");

        var utility = new SensorCategoryDto();
        utility.setName("Utility");
        utility.setParent(swag);

        //Swag -> Utility -> Water
        var water = new SensorCategoryDto();
        water.setName("Water");
        water.setParent(utility);

        //Swag -> Utility - Electricity
        var electricity = new SensorCategoryDto();
        electricity.setName("Electricity");
        electricity.setParent(utility);

        var swig = new SensorCategoryDto();
        swig.setName("SWIG");

        var business = new SensorCategoryDto();
        business.setName("Business");
        business.setParent(swig);

        //swig -> business -> space
        var space = new SensorCategoryDto();
        space.setName("Space");
        space.setParent(business);

        var scs = new ArrayList<SensorCategoryDto>();
        scs.add(water);
        scs.add(electricity);
        scs.add(space);

        for (SensorCategoryDto sc : scs) {
            var sensorCategoryFromDb = sensorCategoryRepository.findByName(sc.getName());
            if(sensorCategoryFromDb != null){
                var a = sensorCategoryFromDb.getSensorCategoryParent().getName();
            }
            if(sensorCategoryFromDb != null){
              continue;
            }

            var temp = generateDab(sc);

            sensorCategoryRepository.saveAndFlush(temp);
        }

        return "Hello";
    }

    @RequestMapping(value={"/dbtestreccache"})
    @ResponseBody
    public String dbtestreccache() {
        var test = sensorCategoryRepository.findByName("Water");
        if(test.getSensorCategoryParent() != null) {
            var p = test.getSensorCategoryParent().getName();
        }

//        if(test.getSensorCategoryChildrens() != null) {
//            var c = test.getSensorCategoryChildrens().size();
//        }

        return "CACHE";
    }

    @RequestMapping(value={"/dbtestrec2"})
    @ResponseBody
    public String dbtestrec2(){

        var utility = new SensorCategoryDto();
        utility.setName("Utility");

        //Swag -> Utility -> Water
        var power = new SensorCategoryDto();
        power.setName("Power");
        power.setParent(utility);


        var scs = new ArrayList<SensorCategoryDto>();
        scs.add(power);

        for (SensorCategoryDto sc : scs) {
            var sensorCategoryFromDb = sensorCategoryRepository.findByName(sc.getName());
            if(sensorCategoryFromDb != null){
                continue;
            }
            var temp = generateDab(sc);
            sensorCategoryRepository.save(temp);
        }


        return "Hello";
    }

    private SensorCategory generateDab(SensorCategoryDto sensorCategoryDto){
        var sensorCategoryDab = new SensorCategory();
        //mapping first here
        sensorCategoryDab.setName(sensorCategoryDto.getName());


        traverseSensorCategoryDto(sensorCategoryDab, sensorCategoryDto);

        //return traverseSensorCategoryDto(sensorCategoryDto);
        return sensorCategoryDab;
    }

    private void traverseSensorCategoryDto(SensorCategory sensorCategory, SensorCategoryDto sensorCategoryDto){
        if(sensorCategoryDto.getParent() != null) {
            var sensorCategoryParentFromDb = sensorCategoryRepository.findByName(sensorCategoryDto.getParent().getName());

            //means already exist
            if(sensorCategoryParentFromDb != null){
                sensorCategory.setSensorCategoryParent(sensorCategoryParentFromDb);
                return;
            } else {
                var temp = new SensorCategory();
                //mapping done here
                temp.setName(sensorCategoryDto.getParent().getName());
                sensorCategory.setSensorCategoryParent(temp);
                traverseSensorCategoryDto(sensorCategory.getSensorCategoryParent(), sensorCategoryDto.getParent());
            }
        }
    }


    private SensorCategory traverseSensorCategoryDto(SensorCategoryDto sensorCategoryDto){


        var temp = new SensorCategory();
        temp.setName(sensorCategoryDto.getName());

        if(sensorCategoryDto.getParent() != null) {
            temp.setSensorCategoryParent(traverseSensorCategoryDto(sensorCategoryDto.getParent()));
        }
        return temp;


    }

    @Bean
    public Supplier<Flux<Message<Sensor>>> supplier() {

//        return () -> {
//            System.out.println("Size: " + unbounded.size() + " INDEX: " + index);
//            index++;
//            if((index % 5) == 0){
//                System.out.println("OH SHIT 20 MODULUS");
//                throw new RuntimeException("FFFF");
//            }
//            return processor.;
//        };

        return () -> processor;
    }

    @Bean
    public Consumer<Message<List<Sensor>>> consumer() {
        return input -> {
            System.out.println("CONSUMER 1 SIZE: " + input.getPayload().size());

//            transactionTemplate.execute(new TransactionCallback<List<Sensor>>() {
//                @Override
//                public List<Sensor> doInTransaction(TransactionStatus transactionStatus) {
//
//                    List<CompletableFuture<Sensor>> futures = new ArrayList<CompletableFuture<Sensor>>();
//
//                    for(var i = 0; i < input.getPayload().size(); i++){
//                        if(i == 3){
//                            transactionManager.rollback(transactionStatus);
//                        }
//                        var sensor = input.getPayload().get(i);
//                        futures.add(persistenceService.saveS(sensor));
//                    }
//
//                    CompletableFuture[] cfs = futures.toArray(new CompletableFuture[futures.size()]);
//
//                    var tempSensors =  CompletableFuture.allOf(cfs)
//                            .thenApply(ignored -> futures.stream()
//                                    .map(CompletableFuture::join)
//                                    .collect(Collectors.toList())
//                            );
//                    try {
//                        return tempSensors.get();
//                    } catch (InterruptedException e) {
//                        transactionManager.rollback(transactionStatus);
//                    } catch (ExecutionException e) {
//                        transactionManager.rollback(transactionStatus);
//                    } catch (Exception e){
//                        transactionManager.rollback(transactionStatus);
//                    }
//                    return null;
//                }
//            });



//            var sensors = new ArrayList<Sensor>();
//
//            for(var i = 0; i < input.getPayload().size(); i++){
//                var sensor = input.getPayload().get(i);
//                sensors.add(sensor);
//            }
//            persistenceService.save(sensors);

//            for (int i = 0; i < input.getPayload().size(); i++) {
//                var sensor = input.getPayload().get(i);
//                var acknowledgment = input.getHeaders().get(KafkaHeaders.ACKNOWLEDGMENT, Acknowledgment.class);
//                try {
//                    persistenceService.save(sensor, i);
//                }catch(Exception ex){
//                    acknowledgment.nack(i, 1000);
//                    break;
//                }
//            }
        };
    }

    @Bean
    public Consumer<Message<List<Sensor>>> consensor() {
        return input -> {
            System.out.println("READ-COMMITTED SIZE:" + input.getPayload().size());
        };
    }

    @Bean
    public Consumer<Message<List<Sensor>>> senconsor() {
        return input -> {
            System.out.println("READ-UNCOMMITTED SIZE:" + input.getPayload().size());
        };
    }
}
