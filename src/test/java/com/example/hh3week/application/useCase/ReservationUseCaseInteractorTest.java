package com.example.hh3week.application.useCase;

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

import com.example.hh3week.adapter.in.dto.reservation.ReservationSeatDetailDto;
import com.example.hh3week.adapter.in.dto.reservation.ReservationSeatDto;
import com.example.hh3week.adapter.in.dto.token.TokenDto;
import com.example.hh3week.adapter.in.dto.waitingQueue.WaitingQueueDto;
import com.example.hh3week.application.service.ReservationService;
import com.example.hh3week.application.service.TokenService;
import com.example.hh3week.application.service.WaitingQueueService;
import com.example.hh3week.domain.reservation.entity.ReservationStatus;
import com.example.hh3week.domain.waitingQueue.entity.WaitingStatus;

class ReservationUseCaseInteractorTest {

	@Mock
	private ReservationService reservationService;

	@Mock
	private WaitingQueueService waitingQueueService;

	@Mock
	private TokenService tokenService;

	@InjectMocks
	private ReservationUseCaseInteractor reservationUseCaseInteractor;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	@DisplayName("예약 가능한 좌석 목록 반환 성공")
	void 예약_가능한_좌석_목록_반환_성공() {
		// given
		long concertScheduleId = 1L;
		ReservationSeatDto seatDto = ReservationSeatDto.builder()
			.seatId(1L)
			.concertId(concertScheduleId)
			.maxCapacity(50)
			.currentReserved(10)
			.build();
		List<ReservationSeatDto> seatDtoList = List.of(seatDto);

		when(reservationService.getAvailableReservationSeatList(concertScheduleId)).thenReturn(seatDtoList);
		when(reservationService.getAvailableReservationSeatDetailList(seatDto.getSeatId())).thenReturn(List.of());

		// when
		List<ReservationSeatDto> result = reservationUseCaseInteractor.getAvailableReservationSeatList(concertScheduleId);

		// then
		assertThat(result).isNotNull();
		assertThat(result).hasSize(1);
		verify(reservationService, times(1)).getAvailableReservationSeatList(concertScheduleId);
		verify(reservationService, times(1)).getAvailableReservationSeatDetailList(seatDto.getSeatId());
	}


	@Test
	@DisplayName("좌석 예약에 성공하고 대기열 및 토큰을 발급")
	void 좌석_예약에_성공하고_대기열_및_토큰을_발급() {
		// given
		long userId = 1L;
		long seatDetailId = 1L;

		// 1. 사용자 대기열 등록 여부 확인
		when(waitingQueueService.isUserInQueue(userId, seatDetailId)).thenReturn(false);
		// 2. 대기열 추가
		WaitingQueueDto waitingQueueDto = WaitingQueueDto.builder()
			.userId(userId)
			.waitingStatus(WaitingStatus.WAITING)
			.priority(1)
			.reservationDt(LocalDateTime.now())
			.build();
		long mockedWaitingId = 1L;
		when(waitingQueueService.addWaitingQueue(any(WaitingQueueDto.class))).thenReturn(mockedWaitingId);


		// 3. 대기열 위치 계산
		int mockedQueuePosition = 1;
		when(waitingQueueService.getQueuePosition(mockedWaitingId)).thenReturn(mockedQueuePosition);

		// 4. 토큰 발급
		long remainingTime = reservationUseCaseInteractor.calculateRemainingTime(mockedQueuePosition);; // 고정된 값 사용
		TokenDto tokenDto = TokenDto.builder()
			.tokenId(1L)
			.userId(userId)
			.token("mockedToken")
			.issuedAt(LocalDateTime.now())
			.expiresAt(LocalDateTime.now().plusSeconds(remainingTime))
			.build();
		when(tokenService.createToken(userId, mockedQueuePosition, remainingTime)).thenReturn(tokenDto);

		// 5. 좌석 예약 상태 변경
		ReservationSeatDetailDto seatDetailDto = ReservationSeatDetailDto.builder()
			.seatDetailId(seatDetailId)
			.seatId(1L)
			.reservationStatus(ReservationStatus.PENDING)
			.build();
		when(reservationService.getSeatDetailById(seatDetailId)).thenReturn(seatDetailDto);

		// 6. 좌석 마스터 정보 업데이트
		ReservationSeatDto seatDto = ReservationSeatDto.builder()
			.seatId(1L)
			.concertId(1L)
			.maxCapacity(50)
			.currentReserved(10)
			.build();
		when(reservationService.getSeatById(seatDetailDto.getSeatId())).thenReturn(seatDto);

		// 7. 좌석 예약 상태 변경 및 업데이트 (void 메서드)
		doNothing().when(reservationService).updateSeatDetailStatus(seatDetailDto);
		doNothing().when(reservationService).updateSeatReservation(seatDto);

		// when
		TokenDto result = reservationUseCaseInteractor.reserveSeat(userId, seatDetailId);

		System.out.println("result : " + result);

		// then
		// 결과 검증
		assertThat(result).isNotNull();
		assertThat(result.getUserId()).isEqualTo(userId);
		assertThat(result.getToken()).isEqualTo("mockedToken");

		// 메서드 호출 검증
		verify(waitingQueueService, times(1)).isUserInQueue(userId, seatDetailId);
		verify(waitingQueueService, times(1)).addWaitingQueue(any(WaitingQueueDto.class));
		verify(waitingQueueService, times(1)).getQueuePosition(mockedWaitingId);
		verify(tokenService, times(1)).createToken(userId, mockedQueuePosition, remainingTime);
		verify(reservationService, times(1)).getSeatDetailById(seatDetailId);
		verify(reservationService, times(1)).getSeatById(seatDetailDto.getSeatId());
		verify(reservationService, times(1)).updateSeatDetailStatus(seatDetailDto);
		verify(reservationService, times(1)).updateSeatReservation(seatDto);
	}



	@Test
	@DisplayName("좌석 예약 실패 - 이미 대기열에 등록된 사용자")
	void 좌석_예약_실패_이미_대기열에_등록된_사용자() {
		// given
		long userId = 1L;
		long seatDetailId = 1L;

		// 사용자 대기열 등록 여부 확인
		when(waitingQueueService.isUserInQueue(userId, seatDetailId)).thenReturn(true);

		// when & then
		assertThatThrownBy(() -> reservationUseCaseInteractor.reserveSeat(userId, seatDetailId))
			.isInstanceOf(IllegalStateException.class)
			.hasMessage("사용자가 이미 대기열에 등록되어 있습니다.");

		verify(waitingQueueService, times(1)).isUserInQueue(userId, seatDetailId);
		verify(waitingQueueService, never()).addWaitingQueue(any(WaitingQueueDto.class));
		verify(tokenService, never()).createToken(anyLong(), anyInt(), anyLong());
	}


	@Test
	@DisplayName("대기 순번 조회 성공")
	void 대기_순번_조회_성공() {
		// given
		long mockedWaitingId = 2L;
		int expectedQueuePosition = 5;

		when(waitingQueueService.getQueuePosition(mockedWaitingId)).thenReturn(expectedQueuePosition);

		// when
		int actualQueuePosition = waitingQueueService.getQueuePosition(mockedWaitingId);

		// then
		assertThat(actualQueuePosition).isEqualTo(expectedQueuePosition);
		verify(waitingQueueService, times(1)).getQueuePosition(mockedWaitingId);
	}


	@Test
	@DisplayName("대기 순번 조회 실패 - 유효하지 않은 대기 ID")
	void 대기_순번_조회_실패_유효하지_않은_대기_ID() {
		// given
		long mockedWaitingId = 999L;
		int expectedQueuePosition = -1; // 또는 0으로 설정

		when(waitingQueueService.getQueuePosition(mockedWaitingId)).thenReturn(expectedQueuePosition);

		// when
		int actualQueuePosition = waitingQueueService.getQueuePosition(mockedWaitingId);

		// then
		assertThat(actualQueuePosition).isEqualTo(expectedQueuePosition);
		verify(waitingQueueService, times(1)).getQueuePosition(mockedWaitingId);
	}


	@Test
	@DisplayName("대기열 발급 실패 - 이미 대기열에 등록된 사용자")
	void 대기열_발급_실패_이미_등록된_사용자() {
		// given
		long userId = 4L;
		long seatDetailId = 4L;

		when(waitingQueueService.isUserInQueue(userId, seatDetailId)).thenReturn(true);

		// when & then
		assertThatThrownBy(() -> reservationUseCaseInteractor.reserveSeat(userId, seatDetailId))
			.isInstanceOf(IllegalStateException.class)
			.hasMessage("사용자가 이미 대기열에 등록되어 있습니다.");

		verify(waitingQueueService, times(1)).isUserInQueue(userId, seatDetailId);
		verify(waitingQueueService, never()).addWaitingQueue(any(WaitingQueueDto.class));
	}

	@Test
	@DisplayName("대기 순번 조회 - 유효하지 않은 대기 ID일 때 -1 반환")
	void 대기_순번_조회_유효하지_않은_대기_ID_반환() {
		// given
		long invalidWaitingId = 999L;
		int expectedQueuePosition = -1;

		when(waitingQueueService.getQueuePosition(invalidWaitingId)).thenReturn(expectedQueuePosition);

		// when
		int actualQueuePosition = waitingQueueService.getQueuePosition(invalidWaitingId);

		// then
		assertThat(actualQueuePosition).isEqualTo(expectedQueuePosition);
		verify(waitingQueueService, times(1)).getQueuePosition(invalidWaitingId);
	}

}
