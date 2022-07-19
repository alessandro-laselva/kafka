package com.kafka;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import java.io.File;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javax.annotation.PostConstruct;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.kafka.services.Producer;

@EnableScheduling
@SpringBootApplication
/**
 * 
 * @author alaselva
 * <p>Add argument with --<name_properties=..> to change applications.properties files on cli.
 * <p>Ex. mvn spring-boot:run -Dspring-boot.run.arguments="--spring.kafka.consumer.group.id=testcli2 --server.port=8898 --schedule.period=5000"
 * <p>
 */
public class KafkaApplication {
	@Autowired
	Producer producer;
	
	@Value("${schedule.period}")
	int schedulePeriod;
	
	
	private ScheduledExecutorService executor;

	public static void main(String[] args) {
		SpringApplication.run(KafkaApplication.class, args);
	}

	@PostConstruct
	public void schedule() {
		System.out.println("Creating newScheduledThreadPool\nSchedule period "+schedulePeriod);
		executor = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
		
		Runnable task = () -> producer.sendMessage(createMessage());
		executor.scheduleAtFixedRate(task, 0, schedulePeriod, TimeUnit.MILLISECONDS);
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
		String returnValue=  LocalDateTime.now()+"\nlatitude: "+latitude+"\nlongitude: "+longitude+"\n";
		System.out.println(returnValue);
		return returnValue;
	}
}
