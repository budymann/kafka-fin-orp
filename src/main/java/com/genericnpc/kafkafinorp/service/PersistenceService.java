package com.genericnpc.kafkafinorp.service;

import com.example.Sensor;
import com.genericnpc.kafkafinorp.dataaccess.SensorDab;
import com.genericnpc.kafkafinorp.repository.SensorRepository;
import org.hibernate.SessionFactory;
import org.hibernate.service.spi.InjectService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@Transactional
public class PersistenceService {
    @Autowired
    SensorRepository sensorRepository;

    @Autowired
    KafkaTemplate kafkaTemplate;

    @Autowired
    SessionFactory sessionFactory;

    public void save(Sensor sensor){
        var sensorDab = new SensorDab();
        BeanUtils.copyProperties(sensor, sensorDab);
        sensorDab.setSensorId(sensor.getId());
        sensorRepository.save(sensorDab);
        //kafkaTemplate.send("sensor-topic-out",sensor);
    }

    public void save(SensorDab sensor){

        sensorRepository.save(sensor);
        var test = TransactionSynchronizationManager.isActualTransactionActive();
        var session = SessionFactory.;

        sensorRepository.save(sensor);
        //kafkaTemplate.send("sensor-topic-out",sensor);
    }

    public SensorDab findById(int id){
        var a = sensorRepository.findById(id).get();
        var test = TransactionSynchronizationManager.isActualTransactionActive();
        var session = sessionFactory.getCurrentSession().getSession();
        return a;
    }

    @Async
    public CompletableFuture<Sensor> saveS(Sensor sensor){
        System.out.println("Execute method asynchronously - "
                + Thread.currentThread().getName());
        var sensorDab = new SensorDab();
        BeanUtils.copyProperties(sensor, sensorDab);
        sensorDab.setSensorId(sensor.getId());
        var id = sensorRepository.save(sensorDab).getSensorId();
        //kafkaTemplate.send("sensor-topic-out",sensor);
        sensor.setId(id);
        return CompletableFuture.completedFuture(sensor);
    }

    @Transactional
    public void save(List<Sensor> sensors){
        for (int i = 0; i < sensors.size(); i++) {
            var sensorDab = new SensorDab();
            BeanUtils.copyProperties(sensors.get(i), sensorDab);
            sensorDab.setSensorId(sensors.get(i).getId());

            sensorRepository.save(sensorDab);

            if(i == 4){
                throw new RuntimeException("SD");
            }

            kafkaTemplate.send("sensor-topic-out",sensors.get(i));
        }
    }
}
