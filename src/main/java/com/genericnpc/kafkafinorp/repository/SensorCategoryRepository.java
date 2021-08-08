package com.genericnpc.kafkafinorp.repository;

import com.genericnpc.kafkafinorp.dataaccess.SensorCategory;
import com.genericnpc.kafkafinorp.dataaccess.SensorDab;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.QueryHints;

import javax.persistence.QueryHint;

public interface SensorCategoryRepository extends JpaRepository<SensorCategory, Integer> {
    @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") })
    SensorCategory findByName(String name);

//    @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") })
//    SensorCategory findBySensorCategoryParentId(int sensorCategoryParentId);
}
