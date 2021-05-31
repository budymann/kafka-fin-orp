package com.genericnpc.kafkafinorp.repository;

import com.genericnpc.kafkafinorp.dataaccess.SensorDab;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SensorRepository extends JpaRepository<SensorDab, String> {
}
