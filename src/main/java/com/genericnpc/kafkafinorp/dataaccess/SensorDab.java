package com.genericnpc.kafkafinorp.dataaccess;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "Sensor")
@Getter
@Setter
public class SensorDab {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="SENSOR_SEQUENCE_GENERATOR")
    @SequenceGenerator(name="SENSOR_SEQUENCE_GENERATOR", sequenceName="SENSOR_SEQUENCE", initialValue=1, allocationSize=10)
    @Column(name="SENSOR_ID")
    private int sensorId;
    private String name;
    private Float acceleration;
    private Float velocity;
    private Float temperature;


    @OneToOne(mappedBy = "sensorDab", cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private SensorAttachmentDab sensorAttachmentDab;
}
