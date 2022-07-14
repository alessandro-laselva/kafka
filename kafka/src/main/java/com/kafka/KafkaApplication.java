package com.kafka;

import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class KafkaApplication {
	@Autowired
	Producer producer;
	private ScheduledExecutorService executor;

	public static void main(String[] args) {
		SpringApplication.run(KafkaApplication.class, args);
	}

	@PostConstruct
	public void schedule() {
		executor = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());

		Runnable task = () -> producer.sendMessage(createMessage());
		executor.scheduleAtFixedRate(task, 0, 3000, TimeUnit.MILLISECONDS);
	}

	private String createMessage() {
		
		double latitude=0,longitude =0;
		double bearing = 0 + (Math.random() - 0.5) * 10.0;
		double brngRad = Math.toRadians(bearing);
		double latRad = Math.toRadians(latitude);
		double lonRad = Math.toRadians(longitude);
		int earthRadiusInMetres = 6371000;
		double distFrac = 500.0 / earthRadiusInMetres;

		double latitudeResult = Math.asin(Math.sin(latRad) * Math.cos(distFrac) + Math.cos(latRad) * Math.sin(distFrac) * Math.cos(brngRad));
		double a = Math.atan2(Math.sin(brngRad) * Math.sin(distFrac) * Math.cos(latRad), Math.cos(distFrac) - Math.sin(latRad) * Math.sin(latitudeResult));
		double longitudeResult = (lonRad + a + 3 * Math.PI) % (2 * Math.PI) - Math.PI;
		latitude = Math.toDegrees(latitudeResult);
		longitude = Math.toDegrees(longitudeResult);
		return LocalDateTime.now()+"\nlatitude: "+latitude+"\nlongitude: "+longitude+"\n";
	}
}
