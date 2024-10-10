// src/main/java/com/example/hh3week/adapter/in/controller/ReservationController.java
package com.example.hh3week.adapter.in.controller;

import com.example.hh3week.adapter.in.dto.reservation.ReservationSeatDetailDto;
import com.example.hh3week.domain.reservation.entity.ReservationStatus;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/api/v1")
public class ReservationController {

	// 예약 가능한 날짜와 좌석 수 (날짜 문자열 -> availableSeats)
	private final ConcurrentHashMap<String, Integer> availableDates = new ConcurrentHashMap<>();

	// 날짜별 좌석 상세 정보 (날짜 문자열 -> List<ReservationSeatDetailDto>)
	private final ConcurrentHashMap<String, List<ReservationSeatDetailDto>> seatsByDate = new ConcurrentHashMap<>();
	private final AtomicLong seatDetailIdGenerator = new AtomicLong(1);

	public ReservationController() {
		// 초기 데이터 설정 (예시)
		availableDates.put("2024-10-10", 50);
		availableDates.put("2024-10-12", 50);

		// 초기 좌석 설정
		for (String date : availableDates.keySet()) {
			List<ReservationSeatDetailDto> seats = new ArrayList<>();
			for (int i = 1; i <= availableDates.get(date); i++) {
				ReservationSeatDetailDto seat = new ReservationSeatDetailDto(
					seatDetailIdGenerator.getAndIncrement(),
					0L, // 사용자 ID 0은 예약되지 않은 상태
					(long) i,
					"A" + i,
					ReservationStatus.AVAILABLE,
					10000
				);
				seats.add(seat);
			}
			seatsByDate.put(date, seats);
		}
	}

	/**
	 * 예약 가능한 날짜 목록 조회 API
	 *
	 * @return 예약 가능한 날짜와 좌석 수 목록
	 */
	@GetMapping("/reservations/dates")
	public ResponseEntity<List<Map<String, Object>>> getAvailableDates() {
		List<Map<String, Object>> response = new ArrayList<>();
		for (Map.Entry<String, Integer> entry : availableDates.entrySet()) {
			Map<String, Object> dateInfo = new HashMap<>();
			dateInfo.put("date", entry.getKey());
			dateInfo.put("availableSeats", entry.getValue());
			response.add(dateInfo);
		}
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	/**
	 * 특정 날짜의 예약 가능한 좌석 조회 API
	 *
	 * @param date 조회할 예약 날짜 (예: "2024-10-10")
	 * @return 해당 날짜의 좌석 상세 정보 목록
	 */
	@GetMapping("/reservations/dates/{date}/seats")
	public ResponseEntity<List<ReservationSeatDetailDto>> getSeatsByDate(@PathVariable String date) {
		List<ReservationSeatDetailDto> seats = seatsByDate.get(date);
		if (seats == null) {
			throw new IllegalArgumentException("예약 가능한 날짜가 아닙니다.");
		}
		return new ResponseEntity<>(seats, HttpStatus.OK);
	}
}
