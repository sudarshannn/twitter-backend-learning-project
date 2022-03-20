package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TwitterProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(TwitterProjectApplication.class, args);
		new Thread(new KafkaTweetProducer()).start();
		new Thread(new KafkaTweetConsumer("grp1")).start();
	}

}
