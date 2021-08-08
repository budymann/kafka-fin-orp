package com.genericnpc.kafkafinorp.dataaccess;

import com.example.Sensor;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Table(name = "SensorCategory")
@Getter
@Setter
@Entity
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class SensorCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="SENSOR_SEQUENCE_GENERATOR")
    @SequenceGenerator(name="SENSOR_SEQUENCE_GENERATOR", sequenceName="SENSOR_SEQUENCE", initialValue=1, allocationSize=10)
    @Column(name="SENSOR_CATEGORY_ID")
    private int sensorCategoryId;

    private String name;

//    @Column(name = "SENSOR_CATEGORY_PARENT_ID", insertable = false, updatable = false)
//    private int sensorCategoryParentId;

    @ManyToOne(cascade =  CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "SENSOR_CATEGORY_PARENT_ID")
    private SensorCategory sensorCategoryParent;

    @OneToMany(mappedBy = "sensorCategoryParent", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<SensorCategory> sensorCategoryChildrens;

    public void setSensorCategoryParent(SensorCategory sensorCategory){
        sensorCategoryParent = sensorCategory;
        if(sensorCategory.getSensorCategoryChildrens() == null){
            sensorCategory.setSensorCategoryChildrens(new ArrayList<SensorCategory>());
        }
        sensorCategory.getSensorCategoryChildrens().add(this);
    }

}
