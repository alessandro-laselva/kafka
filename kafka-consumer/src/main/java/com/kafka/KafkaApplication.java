package com.kafka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


/**
 * 
 * @author alaselva
 * <p>Add argument with --<name_properties=..> to change applications.properties files on cli.
 * <p>Ex. mvn spring-boot:run -Dspring-boot.run.arguments="--spring.kafka.consumer.group.id=testcli2 --server.port=8898 --schedule.period=5000"
 * <p>
 */

@SpringBootApplication
public class KafkaApplication {

	public static void main(String[] args) {
		SpringApplication.run(KafkaApplication.class, args);
	}
	
}
