package com.kafka.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;


@Service
public class Consumer {
	
	@Value("${topic}")
	String topic;

	@Value("${spring.kafka.consumer.group.id}")
	String groupId;

	@KafkaListener(topics = "${topic}", groupId = "${spring.kafka.consumer.group.id}")
	public void consume(String message) {
		//System.out.println("Consumer "+groupId+":\t"+message);
		System.out.println(message);
	}
}