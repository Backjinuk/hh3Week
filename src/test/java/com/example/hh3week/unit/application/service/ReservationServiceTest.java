package com.example.hh3week.unit.application.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.hh3week.adapter.in.dto.reservation.ReservationSeatDetailDto;
import com.example.hh3week.adapter.in.dto.reservation.ReservationSeatDto;
import com.example.hh3week.application.port.out.ReservationSeatRepositoryPort;
import com.example.hh3week.application.service.ReservationService;
import com.example.hh3week.domain.reservation.entity.ReservationSeat;
import com.example.hh3week.domain.reservation.entity.ReservationSeatDetail;
import com.example.hh3week.domain.reservation.entity.ReservationStatus;

@SpringBootTest
class ReservationServiceTest {

	@Mock
	private ReservationSeatRepositoryPort reservationSeatRepositoryPort;

	@InjectMocks
	private ReservationService reservationService;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	@DisplayName("특정 콘서트의 예약 가능한 좌석 목록을 조회 성공")
	void 특정_콘서트의_예약_가능한_좌석_목록을_조회_성공() {
		// given
		long seatId = 1L;
		ReservationSeat reservationSeat = ReservationSeat.builder()
			.seatId(seatId)
			.concertId(101L)
			.maxCapacity(50)
			.currentReserved(10)
			.build();

		when(reservationSeatRepositoryPort.getAvailableReservationSeatList(seatId))
			.thenReturn(List.of(reservationSeat));

		// when
		List<ReservationSeatDto> availableSeats = reservationService.getAvailableReservationSeatList(seatId);

		// then
		assertThat(availableSeats).hasSize(1);
		assertThat(availableSeats.get(0).getSeatId()).isEqualTo(seatId);
		assertThat(availableSeats.get(0).getMaxCapacity()).isEqualTo(50);
		assertThat(availableSeats.get(0).getCurrentReserved()).isEqualTo(10);
	}

	@Test
	@DisplayName("특정 콘서트의 예약 가능한 좌석 목록을 조회 실패 - 좌석 없음")
	void 특정_콘서트의_예약_가능한_좌석_목록을_조회_실패_좌석_없음() {
		// given
		long seatId = 1L;
		when(reservationSeatRepositoryPort.getAvailableReservationSeatList(seatId)).thenReturn(List.of());

		// when
		List<ReservationSeatDto> availableSeats = reservationService.getAvailableReservationSeatList(seatId);

		// then
		assertThat(availableSeats).isEmpty();
	}

	@Test
	@DisplayName("특정 좌석의 예약 가능한 세부 정보 조회 성공")
	void 특정_좌석의_예약_가능한_세부_정보_조회_성공() {
		// given
		long seatId = 1L;
		ReservationSeatDetailDto seatDetail = ReservationSeatDetailDto.builder()
			.seatDetailId(1001L)
			.seatId(seatId)
			.seatCode("A1")
			.reservationStatus(ReservationStatus.AVAILABLE)
			.seatPrice(100)
			.build();

		when(reservationSeatRepositoryPort.getAvailableReservationSeatDetailList(seatId)).thenReturn(List.of(ReservationSeatDetail.ToEntity(seatDetail)));

		// when
		List<ReservationSeatDetailDto> seatDetails = reservationService.getAvailableReservationSeatDetailList(seatId);

		// then
		assertThat(seatDetails).hasSize(1);
		assertThat(seatDetails.get(0).getSeatDetailId()).isEqualTo(1001L);
		assertThat(seatDetails.get(0).getSeatCode()).isEqualTo("A1");
		assertThat(seatDetails.get(0).getReservationStatus()).isEqualTo(ReservationStatus.AVAILABLE);
	}

	@Test
	@DisplayName("특정 좌석의 예약 가능한 세부 정보 조회 실패 - 좌석 없음")
	void 특정_좌석의_예약_가능한_세부_정보_조회_실패_좌석_없음() {
		// given
		long seatId = 1L;
		when(reservationSeatRepositoryPort.getAvailableReservationSeatDetailList(seatId)).thenReturn(List.of());

		// when
		List<ReservationSeatDetailDto> seatDetails = reservationService.getAvailableReservationSeatDetailList(seatId);

		// then
		assertThat(seatDetails).isEmpty();
	}

	@Test
	@DisplayName("좌석 예약 상태 업데이트 성공")
	void 좌석_예약_상태_업데이트_성공() {
		// given
		ReservationSeatDto seatDto = ReservationSeatDto.builder()
			.seatId(1L)
			.concertId(101L)
			.maxCapacity(50)
			.currentReserved(10)
			.build();

		doNothing().when(reservationSeatRepositoryPort).updateReservationCurrentReserved(any(ReservationSeat.class));

		// when
		reservationService.updateSeatReservation(seatDto);

		// then
		verify(reservationSeatRepositoryPort, times(1)).updateReservationCurrentReserved(any(ReservationSeat.class));
		assertThat(seatDto.getCurrentReserved()).isEqualTo(11);
	}

	@Test
	@DisplayName("좌석 예약 상태 업데이트 실패 - 최대 예약 수 초과")
	void 좌석_예약_상태_업데이트_실패_최대_예약_수_초과() {
		// given
		ReservationSeatDto seatDto = ReservationSeatDto.builder()
			.seatId(1L)
			.concertId(101L)
			.maxCapacity(50)
			.currentReserved(50)
			.build();

		// when & then
		assertThatThrownBy(() -> reservationService.updateSeatReservation(seatDto))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("이미 최대 예약 수에 도달했습니다.");
	}

}
