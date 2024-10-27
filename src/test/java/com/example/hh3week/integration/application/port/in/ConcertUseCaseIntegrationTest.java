package com.example.hh3week.integration.application.port.in;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import com.example.hh3week.adapter.in.dto.concert.ConcertScheduleDto;
import com.example.hh3week.application.port.in.ConcertUseCase;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
@Sql({"classpath:schema.sql", "classpath:data.sql"})
class ConcertUseCaseIntegrationTest {
	@Autowired
	private ConcertUseCase concertUseCase;


	@Test
	@DisplayName("예약 가능한 모든 콘서트 스케줄 조회 - 날짜 범위 없이 조회")
	void 예약가능한모든콘서트스케줄조회_날짜범위없이조회() {
		// When
		List<ConcertScheduleDto> availableConcerts = concertUseCase.getAvailableConcertDates(null, null);

		// Then
		assertThat(availableConcerts).hasSize(3);
		assertThat(availableConcerts).extracting("concertId").containsExactlyInAnyOrder(1L, 2L, 3L);
	}

	@Test
	@DisplayName("예약 가능한 콘서트 스케줄 조회 - 특정 날짜 범위 내 조회")
	void 예약가능한콘서트스케줄조회_특정날짜범위내조회() {
		// Given
		LocalDateTime startDate = LocalDateTime.of(2024, 1, 1, 0, 0);
		LocalDateTime endDate = LocalDateTime.of(2025, 5, 31, 23, 59);

		// When
		List<ConcertScheduleDto> availableConcerts = concertUseCase.getAvailableConcertDates(startDate, endDate);

		// Then
		assertThat(availableConcerts).hasSize(3);
		assertThat(availableConcerts).extracting("concertId").containsExactlyInAnyOrder(1L, 2L, 3L);
	}

	@Test
	@DisplayName("예약 가능한 콘서트 스케줄 조회 - 날짜 범위 내에 예약 불가능한 콘서트 제외")
	void 예약가능한콘서트스케줄조회_예약불가능한콘서트제외() {
		// Given
		LocalDateTime startDate = LocalDateTime.of(2024, 6, 1, 0, 0);
		LocalDateTime endDate = LocalDateTime.of(2024, 6, 30, 23, 59);

		// When

		NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
			concertUseCase.getAvailableConcertDates(startDate, endDate);
		});

		// Then
		assertThat(nullPointerException.getMessage()).isEqualTo("콘서트 스케줄을 찾을 수 없습니다.");
	}

	@Test
	@DisplayName("예약 가능한 콘서트 스케줄 조회 - startDate가 endDate보다 이후일 경우 예외 발생")
	void 예약가능한콘서트스케줄조회_startDate가_endDate보다_이후일경우예외발생() {
		// Given
		LocalDateTime startDate = LocalDateTime.of(2024, 6, 1, 0, 0);
		LocalDateTime endDate = LocalDateTime.of(2024, 5, 31, 23, 59);

		// When & Then
		assertThrows(IllegalArgumentException.class, () -> {
			concertUseCase.getAvailableConcertDates(startDate, endDate);
		}, "startDate는 endDate보다 이전이어야 합니다.");
	}

	@Test
	@DisplayName("특정 콘서트 ID로 콘서트 스케줄 조회 - 존재하는 ID일 경우 반환")
	void 특정콘서트ID로콘서트스케줄조회_존재하는ID일경우반환() {
		// Given
		long concertId = 1L;

		// When
		ConcertScheduleDto concert = concertUseCase.getConcertScheduleFindById(concertId);

		// Then
		assertThat(concert).isNotNull();
		assertThat(concert.getConcertId()).isEqualTo(concertId);
		assertThat(concert.getStartDt()).isEqualTo(LocalDateTime.of(2024, 1, 1, 19, 0));
	}

	@Test
	@DisplayName("특정 콘서트 ID로 콘서트 스케줄 조회 - 존재하지 않는 ID일 경우 예외 발생")
	void 특정콘서트ID로콘서트스케줄조회_존재하지않는ID일경우예외발생() {
		// Given
		long nonExistentConcertId = 999L;

		// When & Then
		assertThrows(NullPointerException.class, () -> {
			concertUseCase.getConcertScheduleFindById(nonExistentConcertId);
		}, "Concert not found with id: " + nonExistentConcertId);
	}
}