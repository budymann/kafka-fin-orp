package com.genericnpc.kafkafinorp.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class Transaction {
    private int randomStuffId;
    private int financialTransactionId;
    private String transactionType;
}
