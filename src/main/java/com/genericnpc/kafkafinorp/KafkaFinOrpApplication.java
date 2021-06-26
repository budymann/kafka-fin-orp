package com.genericnpc.kafkafinorp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class KafkaFinOrpApplication {

	public static void main(String[] args) {
		SpringApplication.run(KafkaFinOrpApplication.class, args);
	}

}
