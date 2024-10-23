package com.example.hh3week.integration.application.port.in;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import com.example.hh3week.adapter.in.dto.reservation.ReservationSeatDetailDto;
import com.example.hh3week.adapter.in.dto.reservation.ReservationSeatDto;
import com.example.hh3week.adapter.in.dto.token.TokenDto;
import com.example.hh3week.application.port.in.ReservationUseCase;
import com.example.hh3week.domain.reservation.entity.ReservationStatus;

@SpringBootTest
@Transactional
@Sql({"classpath:schema.sql", "classpath:data.sql"})
class ReservationUseCaseIntegrationTest {
	@Autowired
	private ReservationUseCase reservationUseCase;

	@Test
	@DisplayName("예약 가능한 모든 좌석 조회 - 특정 콘서트 스케줄에 대해")
	void 예약가능한모든좌석조회_특정콘서트스케줄에대해() {
		// Given
		long concertScheduleId = 1L;

		// When
		List<ReservationSeatDto> availableSeats = reservationUseCase.getAvailableReservationSeatList(concertScheduleId);

		// Then
		assertThat(availableSeats).hasSize(1); // A1, A2
		for (ReservationSeatDto seatDto : availableSeats) {
			assertThat(seatDto.getReservationSeatDetailDtoList()).hasSize(50); // Each seat has 2 details
			for (ReservationSeatDetailDto detailDto : seatDto.getReservationSeatDetailDtoList()) {
				assertThat(detailDto.getReservationStatus()).isEqualTo(ReservationStatus.AVAILABLE);
			}
		}
	}

	@Test
	@DisplayName("좌석 예약 - 정상적으로 대기열에 추가되고 토큰이 발급됨")
	void 좌석예약_정상적으로대기열에추가되고토큰이발급됨() {
		// Given
		long userId = 2L;
		long seatDetailId = 100L; // SeatDetail for seatId 2 (B1)

		// When
		TokenDto tokenDto = reservationUseCase.reserveSeat(userId, seatDetailId);

		System.out.println(tokenDto.getToken());

		// Then
		assertThat(tokenDto).isNotNull();
		assertThat(tokenDto.getUserId()).isEqualTo(userId);

		// 추가적으로, 대기열에 등록되었는지, 토큰이 올바르게 생성되었는지 확인
		// 이는 서비스 및 리포지토리 레이어에 대한 더 상세한 검증이 필요할 수 있습니다.
	}

	@Test
	@DisplayName("좌석 예약 - 사용자가 이미 대기열에 등록된 경우 예외 발생")
	void 좌석예약_사용자가이미대기열에등록된경우예외발생() {
		// Given
		long userId = 101L; // 이미 대기열에 등록된 사용자
		long seatDetailId = 1L; // SeatDetail for seatId 1 (A1)

		// When & Then
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
			reservationUseCase.reserveSeat(userId, seatDetailId);
		});

		assertThat(exception.getMessage()).isEqualTo("사용자가 이미 대기열에 등록되어 있습니다.");
	}

	@Test
	@DisplayName("좌석 예약 - 잘못된 seatDetailId로 예약 시 예외 발생")
	void 좌석예약_잘못된seatDetailId로예약시예외발생() {
		// Given
		long userId = 103L;
		long invalidSeatDetailId = 999L; // 존재하지 않는 SeatDetail ID

		// When & Then
		// ReservationService.getSeatDetailById가 존재하지 않을 경우 예외가 발생하도록 구현되어 있어야 함
		// 여기서는 IllegalArgumentException을 가정
		assertThrows(NullPointerException.class, () -> {
			reservationUseCase.reserveSeat(userId, invalidSeatDetailId);
		}, "SeatDetail not found with id: " + invalidSeatDetailId);
	}

	@Test
	@DisplayName("예약 가능한 좌석 조회 - 특정 날짜 범위 내 조회")
	void 예약가능한좌석조회_특정날짜범위내조회() {
		// Given
		long concertScheduleId = 2L;

		// When
		List<ReservationSeatDto> availableSeats = reservationUseCase.getAvailableReservationSeatList(concertScheduleId);

		// Then
		assertThat(availableSeats).hasSize(1); // B1, B2
		for (ReservationSeatDto seatDto : availableSeats) {
			assertThat(seatDto.getReservationSeatDetailDtoList()).hasSize(50); // Each seat has 1 detail
			for (ReservationSeatDetailDto detailDto : seatDto.getReservationSeatDetailDtoList()) {
				assertThat(detailDto.getReservationStatus()).isEqualTo(ReservationStatus.AVAILABLE);
			}
		}
	}
}
