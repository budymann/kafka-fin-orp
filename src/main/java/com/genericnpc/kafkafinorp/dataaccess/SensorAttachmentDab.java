package com.genericnpc.kafkafinorp.dataaccess;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "SensorAttachment")
@Getter
@Setter
public class SensorAttachmentDab {
    @Id
    @Column(name = "SENSOR_ATTACHMENT_ID")
    private int sensorAttachmentId;
    private int moduleSlot;

    @OneToOne
    @MapsId
    @JoinColumn(name = "SENSOR_ATTACHMENT_ID")
    private SensorDab sensorDab;

    @OneToMany(mappedBy = "sensorAttachmentDab", cascade=CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<SensorModule> sensorModules = new ArrayList<>();

    public void addSensorModule(SensorModule sensorModule){
        sensorModule.setSensorAttachmentDab(this);
        sensorModules.add(sensorModule);
    }

    public void removeSensorModule(SensorModule sensorModule){
        sensorModules.remove(sensorModule);
        sensorModule.setSensorAttachmentDab(null);
    }
}
