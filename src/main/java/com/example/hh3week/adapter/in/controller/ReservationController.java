// src/main/java/com/example/hh3week/adapter/in/controller/ReservationController.java
package com.example.hh3week.adapter.in.controller;

import com.example.hh3week.adapter.in.dto.concert.ConcertScheduleDto;
import com.example.hh3week.adapter.in.dto.reservation.ReservationSeatDetailDto;
import com.example.hh3week.domain.concert.entity.ConcertScheduleStatus;
import com.example.hh3week.domain.reservation.entity.ReservationStatus;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/reservations")
public class ReservationController {

	// Mock concert schedules
	private static final Map<Long, List<ConcertScheduleDto>> CONCERT_SCHEDULES = new HashMap<>();

	static {
		// Concert A schedules
		CONCERT_SCHEDULES.put(1L, Arrays.asList(
			new ConcertScheduleDto(101, 1, ConcertScheduleStatus.SCHEDULED, 10000,
				LocalDateTime.of(2024, 10, 10, 19, 0), LocalDateTime.of(2024, 10, 10, 22, 0)),
			new ConcertScheduleDto(102, 1, ConcertScheduleStatus.FINISHED, 10000,
				LocalDateTime.of(2024, 10, 11, 19, 0), LocalDateTime.of(2024, 10, 11, 22, 0))
		));

		// Concert B schedules
		CONCERT_SCHEDULES.put(2L, List.of(
			new ConcertScheduleDto(201, 2, ConcertScheduleStatus.SCHEDULED, 15000,
				LocalDateTime.of(2024, 10, 12, 20, 0), LocalDateTime.of(2024, 10, 12, 23, 0))
		));
	}

	/**
	 * 예약 가능한 날짜 목록을 조회하는 API
	 *
	 * @return List of Maps containing date and availableSeats
	 */
	@GetMapping("/dates")
	public ResponseEntity<List<Map<String, Object>>> getAvailableReservationDates() {
		// Extract dates from ConcertSchedules with status SCHEDULED or AVAILABLE
		Set<LocalDate> availableDates = CONCERT_SCHEDULES.values().stream()
			.flatMap(List::stream)
			.filter(schedule -> schedule.getConcertScheduleStatus() == ConcertScheduleStatus.SCHEDULED
				|| schedule.getConcertScheduleStatus() == ConcertScheduleStatus.AVAILABLE)
			.map(schedule -> schedule.getStartDt().toLocalDate())
			.collect(Collectors.toSet());

		// For each date, calculate total available seats (assuming 50 per schedule)
		List<Map<String, Object>> availableDatesList = new ArrayList<>();
		for (LocalDate date : availableDates) {
			// Count schedules on this date
			long schedulesCount = CONCERT_SCHEDULES.values().stream()
				.flatMap(List::stream)
				.filter(schedule -> schedule.getStartDt().toLocalDate().equals(date)
					&& (schedule.getConcertScheduleStatus() == ConcertScheduleStatus.SCHEDULED
					|| schedule.getConcertScheduleStatus() == ConcertScheduleStatus.AVAILABLE))
				.count();
			// Assume 50 seats per schedule
			int availableSeats = (int)(schedulesCount * 50);
			Map<String, Object> map = new HashMap<>();
			map.put("date", date.toString());
			map.put("availableSeats", availableSeats);
			availableDatesList.add(map);
		}

		return new ResponseEntity<>(availableDatesList, HttpStatus.OK);
	}

	/**
	 * 특정 날짜의 예약 가능한 좌석 상세 정보를 조회하는 API
	 *
	 * @param date 예약 날짜 (예: "2024-10-10")
	 * @return List of ReservationSeatDetailDto
	 */
	@GetMapping("/dates/{date}/seats")
	public ResponseEntity<List<ReservationSeatDetailDto>> getAvailableSeatsByDate(@PathVariable String date) {
		// Parse the date
		LocalDate queryDate;
		try {
			queryDate = LocalDate.parse(date);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		// Find all ConcertSchedules on the given date with status SCHEDULED or AVAILABLE
		List<ConcertScheduleDto> schedules = CONCERT_SCHEDULES.values().stream()
			.flatMap(List::stream)
			.filter(schedule -> (schedule.getConcertScheduleStatus() == ConcertScheduleStatus.SCHEDULED)
				&& schedule.getStartDt().toLocalDate().equals(queryDate))
			.collect(Collectors.toList());

		if (schedules.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		// For each schedule, assume 50 seats available (mock)
		// Create ReservationSeatDetailDto for each seat
		List<ReservationSeatDetailDto> availableSeats = new ArrayList<>();
		long seatDetailId = 1;
		for (ConcertScheduleDto schedule : schedules) {
			for (int i = 1; i <= 50; i++) { // 50 seats per schedule
				ReservationSeatDetailDto seatDto = ReservationSeatDetailDto.builder()
					.seatDetailId(seatDetailId++)
					.userId(0)
					.seatId(i)
					.seatCode("A" + i) // Mock seatCode
					.reservationStatus(ReservationStatus.AVAILABLE)
					.seatPrice(schedule.getConcertPrice())
					.build();
				availableSeats.add(seatDto);
			}
		}

		return new ResponseEntity<>(availableSeats, HttpStatus.OK);
	}
}
