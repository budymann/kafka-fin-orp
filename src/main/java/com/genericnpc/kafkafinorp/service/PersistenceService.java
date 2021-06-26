package com.genericnpc.kafkafinorp.service;

import com.example.Sensor;
import com.genericnpc.kafkafinorp.dataaccess.SensorDab;
import com.genericnpc.kafkafinorp.repository.SensorRepository;
import org.hibernate.service.spi.InjectService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class PersistenceService {
    @Autowired
    SensorRepository sensorRepository;

    @Autowired
    KafkaTemplate kafkaTemplate;

    @Transactional
    public void save(SensorDab sensorDab, Sensor sensor){
        sensorRepository.save(sensorDab);
        kafkaTemplate.send("sensor-topic-out",sensor);

    }

    @Transactional
    public void save(List<Sensor> sensors){
        for (int i = 0; i < sensors.size(); i++) {
            var sensorDab = new SensorDab();
            BeanUtils.copyProperties(sensors.get(i), sensorDab);
            sensorDab.setId(sensors.get(i).getId().toString());

            sensorRepository.save(sensorDab);

            if(i == 4){
                throw new RuntimeException("SD");
            }

            kafkaTemplate.send("sensor-topic-out",sensors.get(i));
        }
    }
}
