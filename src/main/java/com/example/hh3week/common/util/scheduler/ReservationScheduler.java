package com.example.hh3week.common.util.scheduler;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.hh3week.application.service.ReservationService;

@Component
public class ReservationScheduler {

	private final ReservationService reservationService;

	public ReservationScheduler(ReservationService reservationService) {
		this.reservationService = reservationService;
	}


	@Scheduled(cron = "0 0 0 * * *") // 매일 자정에 캐시 무효화
	@CacheEvict(value = "RESERVATION_ITEM", allEntries = true) // 모든 캐시 항목 무효화
	public void evictPopularItemsCache() {
		reservationService.getReservationItem();
		System.out.println("Evicted POPULAR_ITEM cache");
	}
}
