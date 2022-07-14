package com.kafka;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
public class Producer {

	@Value("${spring.kafka.consumer.group.id}")
	String groupId;

	@KafkaListener(topics = "miotopic", groupId = "${spring.kafka.consumer.group.id}")
	public void consume(String message) {
		//System.out.println("Consumer "+groupId+":\t"+message);
		System.out.println(message);
	}
}
