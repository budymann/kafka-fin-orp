package com.genericnpc.kafkafinorp;

import com.genericnpc.kafkafinorp.model.Transaction;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SpringBootApplication
@EnableTransactionManagement
public class KafkaFinOrpApplication {

	public static void main(String[] args) {
		SpringApplication.run(KafkaFinOrpApplication.class, args);

//		List<Transaction> transactions = new ArrayList<Transaction>();
//
//		var a = Transaction.builder().randomStuffId(1).financialTransactionId(9009).transactionType("OLDER TRANSAXION").build();
//		var b = Transaction.builder().randomStuffId(1).financialTransactionId(1111).transactionType("1111").build();
//		var c = Transaction.builder().randomStuffId(99).financialTransactionId(9009).transactionType("NEWER TRANSAXION").build();
//		var d = Transaction.builder().randomStuffId(1).financialTransactionId(2222).transactionType("2222").build();
//
//		overrideAdd(transactions, a);
//		overrideAdd(transactions, b);
//		overrideAdd(transactions, c);
//		overrideAdd(transactions, d);
//
//		for (Transaction transaction : transactions) {
//			System.out.println(transaction.getFinancialTransactionId() + " : " + transaction.getRandomStuffId() + " : " + transaction.getTransactionType());
//		}


	}

	public static void overrideAdd(List<Transaction> transactions, Transaction toBeAdded){
		var existingObjectToBeRemoved = transactions.stream().filter(x -> x.getFinancialTransactionId() == toBeAdded.getFinancialTransactionId()).findFirst();
		if(existingObjectToBeRemoved.isPresent()){
			transactions.remove(existingObjectToBeRemoved.get());
		}
		transactions.add(toBeAdded);
	}




}
