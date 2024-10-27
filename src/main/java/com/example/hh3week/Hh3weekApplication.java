package com.example.hh3week;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@EnableRetry
@SpringBootApplication
public class Hh3weekApplication {

	public static void main(String[] args) {
		SpringApplication.run(Hh3weekApplication.class, args);
	}

}
