package com.genericnpc.kafkafinorp.dataaccess;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "Sensor")
@Getter
@Setter
public class SensorDab {
    @Id
    private String id;
    private Float acceleration;
    private Float velocity;
    private Float temperature;
}
