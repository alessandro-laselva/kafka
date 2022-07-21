package com.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kafka.services.Producer;

@RestController
@RequestMapping(value = "/kafka")
public class KafkaController {

	
    @Autowired
    Producer producer;
    
    /**
     * Questo end point puo' essere usato per inviare un messaggio su topic
     * */
    @GetMapping(value = "/publish")
    public void sendMessageToKafkaTopic(@RequestParam("message") String message,@RequestParam String topic) {
    	producer.sendMessage(message, topic);
    }
}