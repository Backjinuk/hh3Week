package com.example.hh3week;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching // 캐시 활성화
public class Hh3weekApplication {

	public static void main(String[] args) {
		SpringApplication.run(Hh3weekApplication.class, args);
	}

}
