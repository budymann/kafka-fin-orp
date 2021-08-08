package com.genericnpc.kafkafinorp.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SensorCategoryDto {
    private int sensorCategoryId;
    private SensorCategoryDto parent;
    private String name;
}
