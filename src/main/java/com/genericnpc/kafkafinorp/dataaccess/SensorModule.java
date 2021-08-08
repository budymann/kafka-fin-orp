package com.genericnpc.kafkafinorp.dataaccess;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "SensorModule")
@Getter
@Setter
public class SensorModule {
    @Id
    @Column(name = "SENSOR_MODULE_ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="SENSOR_MODULE_SEQUENCE_GENERATOR")
    @SequenceGenerator(name="SENSOR_MODULE_SEQUENCE_GENERATOR", sequenceName="SENSOR_MODULE_SEQUENCE", initialValue=1, allocationSize=10)
    private int SensorModuleId;
    private String name;

    @ManyToOne
    @JoinColumn(name = "SENSOR_ATTACHMENT_ID", referencedColumnName = "SENSOR_ATTACHMENT_ID", updatable = true, insertable = true)
    @JsonBackReference
    private SensorAttachmentDab sensorAttachmentDab;
}
