package com.example.hh3week.application.port.in;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.hh3week.adapter.in.dto.concert.ConcertScheduleDto;
import com.example.hh3week.application.service.ConcertService;
import com.example.hh3week.application.useCase.ConcertUseCaseInteractor;
import com.example.hh3week.domain.concert.entity.ConcertScheduleStatus;

class ConcertUseCaseTest {

	@Mock
	private ConcertService concertService;

	@InjectMocks
	private ConcertUseCaseInteractor useCaseInteractor;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	/*
	 * 예약 가능한 전체 콘서트 조회 테스트
	 */
	@Test
	@DisplayName("예약 가능한 전체 콘서트 조회")
	void 예약_가능한_전체_콘서트_조회() {
		// given
		ConcertScheduleDto concertScheduleDto1 = ConcertScheduleDto.builder()
			.concertScheduleId(1)
			.concertId(1)
			.concertScheduleStatus(ConcertScheduleStatus.AVAILABLE)
			.startDt(LocalDateTime.of(2024, 1, 1, 0, 0))
			.endDt(LocalDateTime.of(2024, 1, 2, 23, 59))
			.build();

		ConcertScheduleDto concertScheduleDto2 = ConcertScheduleDto.builder()
			.concertScheduleId(2)
			.concertId(2)
			.concertScheduleStatus(ConcertScheduleStatus.AVAILABLE)
			.startDt(LocalDateTime.of(2024, 2, 1, 0, 0))
			.endDt(LocalDateTime.of(2024, 2, 2, 23, 59))
			.build();

		List<ConcertScheduleDto> availableConcerts = List.of(concertScheduleDto1, concertScheduleDto2);

		// when
		when(concertService.getAvilbleConcertScheduletList()).thenReturn(availableConcerts);
		List<ConcertScheduleDto> concertDates = useCaseInteractor.getAvailableConcertDates(null, null);

		// then
		assertThat(concertDates).hasSize(2);
		assertThat(concertDates.get(0).getConcertScheduleId()).isEqualTo(1);
		assertThat(concertDates.get(1).getConcertScheduleId()).isEqualTo(2);
	}

	/*
	 * 특정 날짜 범위의 예약 가능한 콘서트 조회 테스트
	 */
	@Test
	@DisplayName("특정 날짜 범위의 예약 가능한 콘서트 조회")
	void 특정_날짜_범위의_예약_가능한_콘서트_조회() {
		// given
		LocalDateTime startDate = LocalDateTime.of(2024, 1, 1, 0, 0);
		LocalDateTime endDate = LocalDateTime.of(2024, 12, 31, 23, 59);

		ConcertScheduleDto concertScheduleDto1 = ConcertScheduleDto.builder()
			.concertScheduleId(1)
			.concertId(1)
			.concertScheduleStatus(ConcertScheduleStatus.AVAILABLE)
			.startDt(startDate)
			.endDt(endDate)
			.build();

		ConcertScheduleDto concertScheduleDto2 = ConcertScheduleDto.builder()
			.concertScheduleId(2)
			.concertId(2)
			.concertScheduleStatus(ConcertScheduleStatus.AVAILABLE)
			.startDt(startDate)
			.endDt(endDate)
			.build();

		List<ConcertScheduleDto> filteredConcerts = List.of(concertScheduleDto1, concertScheduleDto2);

		// when
		when(concertService.getConcertsByDate(startDate, endDate)).thenReturn(filteredConcerts);
		List<ConcertScheduleDto> concertDates = useCaseInteractor.getAvailableConcertDates(startDate, endDate);

		// then
		assertThat(concertDates).hasSize(2);
		assertThat(concertDates.get(0).getConcertScheduleId()).isEqualTo(1);
		assertThat(concertDates.get(1).getConcertScheduleId()).isEqualTo(2);
	}

	@Test
	@DisplayName("특정 콘서트의 예약가능한 콘서트 조회")
	void 특정_콘서트의_예약_가능한_콘서트_조회() {
		// given
		LocalDateTime startDate = LocalDateTime.of(2024, 1, 1, 0, 0);
		LocalDateTime endDate = LocalDateTime.of(2024, 12, 31, 23, 59);

		ConcertScheduleDto concertScheduleDto1 = ConcertScheduleDto.builder()
			.concertScheduleId(1)
			.concertId(1)
			.concertScheduleStatus(ConcertScheduleStatus.AVAILABLE)
			.startDt(startDate)
			.endDt(endDate)
			.build();

		// when
		when(concertService.getConcertScheduleFindById(1)).thenReturn(concertScheduleDto1);
		ConcertScheduleDto concertScheduleDto = useCaseInteractor.getConcertScheduleFindById(
			concertScheduleDto1.getConcertId());

		// then
		assertThat(concertScheduleDto.getConcertId()).isEqualTo(1);
		assertThat(concertScheduleDto.getConcertScheduleId()).isEqualTo(1);
		assertThat(concertScheduleDto.getConcertScheduleStatus()).isEqualTo(ConcertScheduleStatus.AVAILABLE);
	}

}
