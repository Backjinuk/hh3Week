package com.example.hh3week.adapter.out.persistence;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.example.hh3week.domain.concert.entity.Concert;
import com.example.hh3week.domain.concert.entity.ConcertSchedule;
import com.example.hh3week.domain.concert.entity.ConcertScheduleStatus;

@SpringBootTest
@Transactional
public class ConcertRepositoryImplTest {

	@Autowired
	private ConcertRepositoryImpl concertRepositoryImpl;

	@Test
	@DisplayName("사용 가능한 콘서트 목록 조회 - 정상적으로 콘서트 목록 반환")
	void 사용가능한콘서트목록조회_정상적으로콘서트목록반환() {
		// When
		List<Concert> availableConcerts = concertRepositoryImpl.getAvilbleConcertList();

		// Then
		assertThat(availableConcerts).isNotEmpty();
		// 추가 검증: 각 콘서트에 적어도 하나의 'AVAILABLE' 스케줄이 있는지 확인
		for (Concert concert : availableConcerts) {
			List<ConcertSchedule> schedules = concertRepositoryImpl.getConcertsByDate(
					LocalDateTime.of(2020, 1, 1, 0, 0), LocalDateTime.of(2030, 12, 31, 23, 59))
				.stream()
				.filter(schedule -> schedule.getConcertId() == concert.getConcertId())
				.filter(schedule -> schedule.getConcertScheduleStatus().equals(ConcertScheduleStatus.AVAILABLE))
				.toList();
			assertThat(schedules).isNotEmpty();
		}
	}

	@Test
	@DisplayName("콘서트 ID로 콘서트 조회 - 존재하는 콘서트 반환")
	void 콘서트ID로콘서트조회_존재하는콘서트반환() {
		// Given
		long concertId = 1L;

		// When
		Concert concert = concertRepositoryImpl.getConcertFindById(concertId);

		// Then
		assertThat(concert).isNotNull();
		assertThat(concert.getConcertId()).isEqualTo(concertId);
		// Concert에 status 필드가 없으므로 관련 검증 제거
	}

	@Test
	@DisplayName("콘서트 ID로 콘서트 조회 - 존재하지 않는 콘서트 시 예외 발생")
	void 콘서트ID로콘서트조회_존재하지않는콘서트시예외발생() {
		// Given
		long nonExistentConcertId = 999L;

		Concert concertFindById = concertRepositoryImpl.getConcertFindById(nonExistentConcertId);
		// When & Then

		assertThat(concertFindById).isNull();
	}

	@Test
	@DisplayName("사용 가능한 콘서트 스케줄 목록 조회 - 정상적으로 스케줄 목록 반환")
	void 사용가능한콘서트스케줄목록조회_정상적으로스케줄목록반환() {
		// When
		List<ConcertSchedule> availableSchedules = concertRepositoryImpl.getAvilbleConcertSchedueList();

		// Then
		assertThat(availableSchedules).isNotEmpty();
		for (ConcertSchedule schedule : availableSchedules) {
			assertThat(schedule.getConcertScheduleStatus()).isEqualTo(ConcertScheduleStatus.AVAILABLE);
		}
	}

	@Test
	@DisplayName("콘서트 스케줄 ID로 스케줄 조회 - 존재하는 스케줄 반환")
	void 콘서트스케줄ID로스케줄조회_존재하는스케줄반환() {
		// Given
		long scheduleId = 1L;

		// When
		ConcertSchedule schedule = concertRepositoryImpl.getConcertScheduleFindById(scheduleId);

		// Then
		assertThat(schedule).isNotNull();
		assertThat(schedule.getConcertScheduleId()).isEqualTo(scheduleId);
		assertThat(schedule.getConcertScheduleStatus()).isEqualTo(ConcertScheduleStatus.AVAILABLE);
	}

	@Test
	@DisplayName("콘서트 스케줄 ID로 스케줄 조회 - 존재하지 않는 스케줄 시 예외 발생")
	void 콘서트스케줄ID로스케줄조회_존재하지않는스케줄시예외발생() {
		// Given
		long nonExistentScheduleId = 999L;

		ConcertSchedule concertScheduleFindById = concertRepositoryImpl.getConcertScheduleFindById(
			nonExistentScheduleId);
		// When & Then

		assertThat(concertScheduleFindById).isNull();
	}

	@Test
	@DisplayName("특정 기간 내 콘서트 스케줄 조회 - 정상적으로 스케줄 목록 반환")
	void 특정기간내콘서트스케줄조회_정상적으로스케줄목록반환() {
		// Given
		LocalDateTime startDate = LocalDateTime.of(2024, 1, 1, 0, 0);
		LocalDateTime endDate = LocalDateTime.of(2024, 2, 28, 23, 59);

		// When
		List<ConcertSchedule> schedules = concertRepositoryImpl.getConcertsByDate(startDate, endDate);

		// Then
		assertThat(schedules).isNotEmpty();
		for (ConcertSchedule schedule : schedules) {
			assertThat(schedule.getStartDt()).isAfterOrEqualTo(startDate);
			assertThat(schedule.getStartDt()).isBeforeOrEqualTo(endDate);
		}
	}

	@Test
	@DisplayName("특정 기간 내 콘서트 스케줄 조회 - 해당 기간에 스케줄이 없을 경우 빈 목록 반환")
	void 특정기간내콘서트스케줄조회_해당기간에스케줄이없을경우빈목록반환() {
		// Given
		LocalDateTime startDate = LocalDateTime.of(2025, 1, 1, 0, 0);
		LocalDateTime endDate = LocalDateTime.of(2025, 12, 31, 23, 59);

		// When
		List<ConcertSchedule> schedules = concertRepositoryImpl.getConcertsByDate(startDate, endDate);

		// Then
		assertThat(schedules).isEmpty();
	}
}
