package com.genericnpc.kafkafinorp.repository;

import com.genericnpc.kafkafinorp.dataaccess.SensorDab;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface SensorRepository extends JpaRepository<SensorDab, Integer> {
    @Transactional(isolation = Isolation.SERIALIZABLE)
    SensorDab findByVelocity(Float velocity);
}
