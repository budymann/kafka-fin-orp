package com.genericnpc.kafkafinorp.controller;

import com.example.Sensor;
import com.genericnpc.kafkafinorp.dataaccess.SensorDab;
import com.genericnpc.kafkafinorp.repository.SensorRepository;
import com.genericnpc.kafkafinorp.service.PersistenceService;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.BatchListenerFailedException;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.IntStream;

@RestController
public class KafkaFinOrpController {
    private Random random = new Random();
    BlockingQueue<Message<Sensor>> unbounded = new LinkedBlockingQueue<>();
    private final EmitterProcessor<Message<Sensor>> processor = EmitterProcessor.create();

    private int index = 0;

    @Autowired
    private StreamBridge streamBridge;

    @Autowired
    private PersistenceService persistenceService;

    @Autowired
    private KafkaTemplate kafkaTemplate;

    private Sensor randomSensor() {
        Sensor sensor = new Sensor();
        sensor.setId(UUID.randomUUID().toString() + "-v1");
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
            var sensors = new ArrayList<Sensor>();

            for(var i = 0; i < input.getPayload().size(); i++){
                var sensor = input.getPayload().get(i);
                sensors.add(sensor);
            }
            persistenceService.save(sensors);
        };
    }
    @Bean
    public Consumer<Message<List<Sensor>>> consensor() {
        return input -> {
            System.out.println("CONSENSOR SIZE:" + input.getPayload().size());
        };
    }

    @Bean
    public Consumer<Message<List<Sensor>>> senconsor() {
        return input -> {
            System.out.println("SENCONSOR SIZE:" + input.getPayload().size());
        };
    }
}
