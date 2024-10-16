package com.example.hh3week.application.useCase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.hh3week.adapter.in.dto.concert.ConcertScheduleDto;
import com.example.hh3week.application.service.ConcertService;
import com.example.hh3week.domain.concert.entity.ConcertScheduleStatus;

@ExtendWith(MockitoExtension.class)
class ConcertUseCaseInteractorTest {

	@Mock
	private ConcertService concertService;

	@InjectMocks
	private ConcertUseCaseInteractor concertUseCaseInteractor;

	/**
	 * Test Case 1: 특정 날짜 범위 내의 예약 가능한 콘서트 조회 성공
	 */
	@Test
	@DisplayName("예약 가능한 콘서트 날짜 범위 조회 성공")
	void getAvailableConcertDates_withDateRange_success() {
		// given
		LocalDateTime startDate = LocalDateTime.of(2024, 1, 1, 0, 0);
		LocalDateTime endDate = LocalDateTime.of(2024, 1, 31, 23, 59);

		ConcertScheduleDto concert1 = ConcertScheduleDto.builder()
			.concertScheduleId(1L)
			.concertId(1L)
			.concertScheduleStatus(ConcertScheduleStatus.AVAILABLE)
			.concertPrice(100000L)
			.startDt(LocalDateTime.of(2024, 1, 10, 20, 0))
			.endDt(LocalDateTime.of(2024, 1, 10, 23, 0))
			.build();

		ConcertScheduleDto concert2 = ConcertScheduleDto.builder()
			.concertScheduleId(2L)
			.concertId(2L)
			.concertScheduleStatus(ConcertScheduleStatus.AVAILABLE)
			.concertPrice(150000L)
			.startDt(LocalDateTime.of(2024, 1, 20, 21, 0))
			.endDt(LocalDateTime.of(2024, 1, 20, 23, 30))
			.build();

		List<ConcertScheduleDto> expectedConcerts = Arrays.asList(concert1, concert2);

		when(concertService.getConcertsByDate(startDate, endDate)).thenReturn(expectedConcerts);

		// when
		List<ConcertScheduleDto> result = concertUseCaseInteractor.getAvailableConcertDates(startDate, endDate);

		// then
		assertThat(result).isNotNull();
		assertThat(result).hasSize(2);
		assertThat(result).containsExactly(concert1, concert2);

		verify(concertService, times(1)).getConcertsByDate(startDate, endDate);
		verify(concertService, never()).getAvilbleConcertScheduletList();
	}

	/**
	 * Test Case 2: 날짜 범위 없이 모든 예약 가능한 콘서트 조회 성공
	 */
	@Test
	@DisplayName("모든 예약 가능한 콘서트 날짜 조회 성공")
	void getAvailableConcertDates_withoutDateRange_success() {
		// given
		ConcertScheduleDto concert1 = ConcertScheduleDto.builder()
			.concertScheduleId(3L)
			.concertId(3L)
			.concertScheduleStatus(ConcertScheduleStatus.AVAILABLE)
			.concertPrice(200000L)
			.startDt(LocalDateTime.of(2024, 2, 15, 19, 30))
			.endDt(LocalDateTime.of(2024, 2, 15, 22, 30))
			.build();

		ConcertScheduleDto concert2 = ConcertScheduleDto.builder()
			.concertScheduleId(4L)
			.concertId(4L)
			.concertScheduleStatus(ConcertScheduleStatus.AVAILABLE)
			.concertPrice(250000L)
			.startDt(LocalDateTime.of(2024, 3, 5, 20, 30))
			.endDt(LocalDateTime.of(2024, 3, 5, 23, 30))
			.build();

		List<ConcertScheduleDto> expectedConcerts = Arrays.asList(concert1, concert2);

		when(concertService.getAvilbleConcertScheduletList()).thenReturn(expectedConcerts);

		// when
		List<ConcertScheduleDto> result = concertUseCaseInteractor.getAvailableConcertDates(null, null);

		// then
		assertThat(result).isNotNull();
		assertThat(result).hasSize(2);
		assertThat(result).containsExactly(concert1, concert2);

		verify(concertService, times(1)).getAvilbleConcertScheduletList();
		verify(concertService, never()).getConcertsByDate(any(), any());
	}

	/**
	 * Test Case 3: 특정 콘서트 ID로 콘서트 스케줄 조회 성공
	 */
	@Test
	@DisplayName("특정 콘서트 ID로 콘서트 스케줄 조회 성공")
	void getConcertScheduleFindById_success() {
		// given
		long concertId = 5L;

		ConcertScheduleDto expectedConcert = ConcertScheduleDto.builder()
			.concertScheduleId(5L)
			.concertId(concertId)
			.concertScheduleStatus(ConcertScheduleStatus.AVAILABLE)
			.concertPrice(300000L)
			.startDt(LocalDateTime.of(2024, 4, 10, 18, 0))
			.endDt(LocalDateTime.of(2024, 4, 10, 21, 0))
			.build();

		when(concertService.getConcertScheduleFindById(concertId)).thenReturn(expectedConcert);

		// when
		ConcertScheduleDto result = concertUseCaseInteractor.getConcertScheduleFindById(concertId);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getConcertId()).isEqualTo(concertId);
		assertThat(result.getConcertScheduleStatus()).isEqualTo(ConcertScheduleStatus.AVAILABLE);
		assertThat(result.getConcertPrice()).isEqualTo(300000L);
		assertThat(result.getStartDt()).isEqualTo(LocalDateTime.of(2024, 4, 10, 18, 0));
		assertThat(result.getEndDt()).isEqualTo(LocalDateTime.of(2024, 4, 10, 21, 0));

		verify(concertService, times(1)).getConcertScheduleFindById(concertId);
	}

	/**
	 * Test Case 4: 특정 콘서트 ID로 콘서트 스케줄 조회 실패 (존재하지 않는 ID)
	 * 예를 들어, null을 반환하거나 예외를 던지는 경우
	 */
	@Test
	@DisplayName("특정 콘서트 ID로 콘서트 스케줄 조회 실패")
	void getConcertScheduleFindById_failure() {
		// given
		long concertId = 999L;

		when(concertService.getConcertScheduleFindById(concertId)).thenReturn(null);

		// when
		ConcertScheduleDto result = concertUseCaseInteractor.getConcertScheduleFindById(concertId);

		// then
		assertThat(result).isNull();

		verify(concertService, times(1)).getConcertScheduleFindById(concertId);
	}

	/**
	 * Test Case 5: 날짜 범위 중 하나만 주어진 경우
	 * 예시로, startDate만 주어졌을 때의 동작을 검증할 수 있습니다.
	 * 현재 구현에서는 startDate와 endDate가 모두 필요하므로, 하나만 주어진 경우 전체 조회를 할 것으로 예상됩니다.
	 */
	@Test
	@DisplayName("날짜 범위 중 하나만 주어진 경우 전체 예약 가능한 콘서트 조회")
	void getAvailableConcertDates_partialDateRange() {
		// given
		LocalDateTime startDate = LocalDateTime.of(2024, 1, 1, 0, 0);
		LocalDateTime endDate = null;

		ConcertScheduleDto concert1 = ConcertScheduleDto.builder()
			.concertScheduleId(6L)
			.concertId(6L)
			.concertScheduleStatus(ConcertScheduleStatus.AVAILABLE)
			.concertPrice(350000L)
			.startDt(LocalDateTime.of(2024, 5, 20, 20, 0))
			.endDt(LocalDateTime.of(2024, 5, 20, 23, 0))
			.build();

		ConcertScheduleDto concert2 = ConcertScheduleDto.builder()
			.concertScheduleId(7L)
			.concertId(7L)
			.concertScheduleStatus(ConcertScheduleStatus.AVAILABLE)
			.concertPrice(400000L)
			.startDt(LocalDateTime.of(2024, 6, 15, 19, 0))
			.endDt(LocalDateTime.of(2024, 6, 15, 22, 0))
			.build();

		List<ConcertScheduleDto> expectedConcerts = Arrays.asList(concert1, concert2);

		when(concertService.getAvilbleConcertScheduletList()).thenReturn(expectedConcerts);

		// when
		List<ConcertScheduleDto> result = concertUseCaseInteractor.getAvailableConcertDates(startDate, endDate);

		// then
		assertThat(result).isNotNull();
		assertThat(result).hasSize(2);
		assertThat(result).containsExactly(concert1, concert2);

		verify(concertService, times(1)).getAvilbleConcertScheduletList();
		verify(concertService, never()).getConcertsByDate(any(), any());
	}

	/**
	 * Test Case 6: 특정 콘서트 ID로 콘서트 스케줄 조회 시 예외 발생 (예외 던지기)
	 * 예를 들어, 서비스에서 예외를 던지는 경우를 검증할 수 있습니다.
	 */
	@Test
	@DisplayName("특정 콘서트 ID로 콘서트 스케줄 조회 시 예외 발생")
	void getConcertScheduleFindById_exception() {
		// given
		long concertId = 6L;

		when(concertService.getConcertScheduleFindById(concertId))
			.thenThrow(new RuntimeException("Database error"));

		// when & then
		assertThatThrownBy(() -> concertUseCaseInteractor.getConcertScheduleFindById(concertId))
			.isInstanceOf(RuntimeException.class)
			.hasMessage("Database error");

		verify(concertService, times(1)).getConcertScheduleFindById(concertId);
	}

	/**
	 * Test Case 7: ArgumentCaptor를 사용하여 전달된 인자 검증
	 */
	@Test
	@DisplayName("ConcertsByDate 호출 시 올바른 인자 전달")
	void getAvailableConcertDates_correctArguments() {
		// given
		LocalDateTime startDate = LocalDateTime.of(2024, 1, 1, 0, 0);
		LocalDateTime endDate = LocalDateTime.of(2024, 1, 31, 23, 59);

		ConcertScheduleDto concert1 = ConcertScheduleDto.builder()
			.concertScheduleId(1L)
			.concertId(1L)
			.concertScheduleStatus(ConcertScheduleStatus.AVAILABLE)
			.concertPrice(100000L)
			.startDt(LocalDateTime.of(2024, 1, 10, 20, 0))
			.endDt(LocalDateTime.of(2024, 1, 10, 23, 0))
			.build();

		List<ConcertScheduleDto> expectedConcerts = Arrays.asList(concert1);

		when(concertService.getConcertsByDate(any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(expectedConcerts);

		// when
		List<ConcertScheduleDto> result = concertUseCaseInteractor.getAvailableConcertDates(startDate, endDate);

		// then
		ArgumentCaptor<LocalDateTime> startDateCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
		ArgumentCaptor<LocalDateTime> endDateCaptor = ArgumentCaptor.forClass(LocalDateTime.class);

		verify(concertService).getConcertsByDate(startDateCaptor.capture(), endDateCaptor.capture());

		assertThat(startDateCaptor.getValue()).isEqualTo(startDate);
		assertThat(endDateCaptor.getValue()).isEqualTo(endDate);
	}

	/**
	 * Test Case 8: InOrder를 사용하여 메서드 호출 순서 검증
	 */
	@Test
	@DisplayName("ConcertService 메서드 호출 순서 검증")
	void methodCallOrder_verification() {
		// given
		LocalDateTime startDate = LocalDateTime.of(2024, 7, 1, 0, 0);
		LocalDateTime endDate = LocalDateTime.of(2024, 7, 31, 23, 59);

		ConcertScheduleDto concert = ConcertScheduleDto.builder()
			.concertScheduleId(8L)
			.concertId(8L)
			.concertScheduleStatus(ConcertScheduleStatus.AVAILABLE)
			.concertPrice(450000L)
			.startDt(LocalDateTime.of(2024, 7, 15, 20, 0))
			.endDt(LocalDateTime.of(2024, 7, 15, 23, 0))
			.build();

		when(concertService.getConcertsByDate(startDate, endDate)).thenReturn(Arrays.asList(concert));

		// when
		List<ConcertScheduleDto> result = concertUseCaseInteractor.getAvailableConcertDates(startDate, endDate);

		// then
		InOrder inOrder = inOrder(concertService);
		inOrder.verify(concertService).getConcertsByDate(startDate, endDate);
		inOrder.verifyNoMoreInteractions();
	}
}
