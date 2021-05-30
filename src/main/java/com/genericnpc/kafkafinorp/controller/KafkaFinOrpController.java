package com.genericnpc.kafkafinorp.controller;

import com.example.Sensor;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Supplier;

@RestController
public class KafkaFinOrpController {
    private Random random = new Random();
    BlockingQueue<Sensor> unbounded = new LinkedBlockingQueue<>();


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
        return "HelloWorld";
    }

    @Bean
    public Supplier<Sensor> supplier() {
        return () -> unbounded.poll();
    }
}
