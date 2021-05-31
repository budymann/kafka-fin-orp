package com.genericnpc.kafkafinorp.controller;

import com.example.Sensor;
import com.genericnpc.kafkafinorp.dataaccess.SensorDab;
import com.genericnpc.kafkafinorp.repository.SensorRepository;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;

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
    BlockingQueue<Sensor> unbounded = new LinkedBlockingQueue<>();
    private final EmitterProcessor<Message<Sensor>> processor = EmitterProcessor.create();

    @Autowired
    private StreamBridge streamBridge;

    @Autowired
    private SensorRepository sensorRepository;

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
        }
        return "Sent mock event";
    }

    @RequestMapping(value={"/sensor"})
    @ResponseBody
    public List<SensorDab> getSensor(){

        System.out.println("Hello WOrld");
        System.out.println(sensorRepository.findAll());
        System.out.println(sensorRepository.findAll().get(0).getId());
        return sensorRepository.findAll();
    }

    @Bean
    public Supplier<Flux<Message<Sensor>>> supplier() {
        return () -> processor;
    }

    @Bean
    public Consumer<Message<List<Sensor>>> consumer() {
        return input -> {
            System.out.println(input.getPayload().size());

            for(var i = 0; i < input.getPayload().size(); i++){
                if(i==3){
                    streamBridge.send("NOWHEREFAILPLSSS", input.getPayload().get(i));

                }else{
                    streamBridge.send("output-out-0", input.getPayload().get(i));
                }

            }
        };
    }

}
