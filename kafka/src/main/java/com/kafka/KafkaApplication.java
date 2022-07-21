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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import java.sql.Timestamp;
import javax.annotation.PostConstruct;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kafka.services.Producer;

@EnableScheduling
@SpringBootApplication
/**
 * 
 * @author alaselva
 *         <p>
 *         Add argument with --<name_properties=..> to change
 *         applications.properties files on cli.
 *         <p>
 *         Ex. mvn spring-boot:run
 *         -Dspring-boot.run.arguments="--spring.kafka.consumer.group.id=testcli2
 *         --server.port=8898 --schedule.period=5000"
 *         <p>
 */
public class KafkaApplication {
	@Autowired
	Producer producer;

	@Value("${schedule.period}")
	int schedulePeriod;
	@Value("${topic1}")
	String topic1;
	@Value("${topic2}")
	String topic2;

	private ScheduledExecutorService executor;

	public static void main(String[] args) {
		SpringApplication.run(KafkaApplication.class, args);
	}

	@PostConstruct
	public void schedule() {
		System.out.println("Creating newScheduledThreadPool\nSchedule period " + schedulePeriod);
		executor = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());

		Runnable task = () -> producer.sendMessage(createMessageLatLon(), topic1);
		Runnable task2 = () -> producer.sendMessage(createMessageMeasure(), topic2);
		executor.scheduleAtFixedRate(task, 0, schedulePeriod, TimeUnit.MILLISECONDS);
		executor.scheduleAtFixedRate(task2, 0, schedulePeriod, TimeUnit.MILLISECONDS);
	}

	private String createMessageLatLon() {

		double latitude = 0, longitude = 0;
		double bearing = 0 + (Math.random() - 0.5) * 10.0;
		double brngRad = Math.toRadians(bearing);
		double latRad = Math.toRadians(latitude);
		double lonRad = Math.toRadians(longitude);
		int earthRadiusInMetres = 6371000;
		double distFrac = 500.0 / earthRadiusInMetres;

		double latitudeResult = Math.asin(
				Math.sin(latRad) * Math.cos(distFrac) + Math.cos(latRad) * Math.sin(distFrac) * Math.cos(brngRad));
		double a = Math.atan2(Math.sin(brngRad) * Math.sin(distFrac) * Math.cos(latRad),
				Math.cos(distFrac) - Math.sin(latRad) * Math.sin(latitudeResult));
		double longitudeResult = (lonRad + a + 3 * Math.PI) % (2 * Math.PI) - Math.PI;
		latitude = Math.toDegrees(latitudeResult);
		longitude = Math.toDegrees(longitudeResult);
		String returnValue = LocalDateTime.now() + "\nlatitude: " + latitude + "\nlongitude: " + longitude + "\n";
		System.out.println(returnValue);
		return returnValue;
	}

	private String createMessageMeasure() {

		List<MeasureStatus> returnValue = new ArrayList<>();
		MeasureStatus ms = new MeasureStatus();
		MeasureStatus ms1 = new MeasureStatus();
		MeasureStatus ms2 = new MeasureStatus();
		MeasureStatus ms3 = new MeasureStatus();

		ms.setTypology("battery capacity");
		ms.setValue(75.59055 + (new Random().nextDouble() * (24.40945)));
		returnValue.add(ms);

		ms1.setTypology("battery charge");
		ms1.setValue(2533.1687 + (new Random().nextDouble() * (615.3856)));
		returnValue.add(ms1);

		ms2.setTypology("battery voltage");
		ms2.setValue(4.64576 + (new Random().nextDouble() * (0.27816)));
		returnValue.add(ms2);

		ms3.setTypology("temperature");
		ms3.setValue(23.500002 + (new Random().nextDouble() * (7.000002)));
		returnValue.add(ms3);

		MeasureStatusWrapper msw = new MeasureStatusWrapper();
		msw.setMeasures(returnValue);
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.findAndRegisterModules();
		try {
			return objectMapper.writeValueAsString(msw);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;

	}

	class MeasureStatusWrapper {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		String timestamp = LocalDateTime.now().format(formatter);
		List<MeasureStatus> measures = new ArrayList<>();

	 
		public String getTimestamp() {
			return timestamp;
		}

		public void setTimestamp(String timestamp) {
			this.timestamp = timestamp;
		}

		public List<MeasureStatus> getMeasures() {
			return measures;
		}

		public void setMeasures(List<MeasureStatus> measures) {
			this.measures = measures;
		}

		@Override
		public String toString() {
			return "MeasureStatusWrapper [timestamp=" + timestamp + ", measures=" + measures + "]";
		}
		
		
	}

	class MeasureStatus {
		String typology;
		double value;

		public String getTypology() {
			return typology;
		}

		public void setTypology(String typology) {
			this.typology = typology;
		}

		public double getValue() {
			return value;
		}

		public void setValue(double value) {
			this.value = value;
		}

	}

}
