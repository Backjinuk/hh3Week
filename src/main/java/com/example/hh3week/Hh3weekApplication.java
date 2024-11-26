package com.example.hh3week;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.Schedules;

@SpringBootApplication
@EnableScheduling
public class Hh3weekApplication {

	public static void main(String[] args) {
		SpringApplication.run(Hh3weekApplication.class, args);
	}

}
