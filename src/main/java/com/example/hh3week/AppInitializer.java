package com.example.hh3week;

import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.example.hh3week.application.service.ReservationService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AppInitializer implements CommandLineRunner {

	private final ReservationService reservationService;

	public AppInitializer(ReservationService reservationService) {
		this.reservationService = reservationService;
	}

	@Override
	public void run(String... args) throws Exception {

		log.info("메모리 캐싱 전");
		reservationService.initializeAndRefreshCache();
		log.info("메모리 캐싱 후");
	}
}
