package com.kafka.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;


@Service
public class Consumer {
	
	@Value("${topic1}")
	String topic1;
	
	@Value("${topic2}")
	String topic2;

	@Value("${spring.kafka.consumer.group.id}")
	String groupId;
	 

	@KafkaListener(topics = "${topic1}", groupId = "${spring.kafka.consumer.group.id}", autoStartup = "${autostartup.topic1}" )
	public void consumeTopic1(String message) {
		//System.out.println("Consumer "+groupId+":\t"+message);
		System.out.println(message);
	}
	
	@KafkaListener(topics = "${topic2}", groupId = "${spring.kafka.consumer.group.id}", autoStartup = "${autostartup.topic2}" )
	public void consumeTopic2(String message) {
		//System.out.println("Consumer "+groupId+":\t"+message);
		System.out.println(message);
	}
}