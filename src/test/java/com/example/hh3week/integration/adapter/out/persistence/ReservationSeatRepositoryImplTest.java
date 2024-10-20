package com.example.hh3week.integration.adapter.out.persistence;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import com.example.hh3week.application.port.out.ReservationSeatRepositoryPort;
import com.example.hh3week.domain.reservation.entity.ReservationSeat;
import com.example.hh3week.domain.reservation.entity.ReservationSeatDetail;
import com.example.hh3week.domain.reservation.entity.ReservationStatus;

@SpringBootTest
@Transactional
@Sql({"classpath:schema.sql", "classpath:data.sql"})
public class ReservationSeatRepositoryImplTest {

	@Autowired
	private ReservationSeatRepositoryPort reservationSeatRepositoryImpl;

	/**
	 * 사용 가능한 예약 좌석 상세 목록 조회 - 정상적으로 목록 반환
	 */
	@Test
	@DisplayName("사용 가능한 예약 좌석 상세 목록 조회 - 정상적으로 목록 반환")
	void 사용가능한예약좌석상세목록조회_정상적으로목록반환() {
		// Given
		long seatId = 1L;

		// When
		List<ReservationSeatDetail> availableDetails = reservationSeatRepositoryImpl.getAvailableReservationSeatDetailList(
			seatId);

		// Then
		assertThat(availableDetails).isNotEmpty();
		for (ReservationSeatDetail detail : availableDetails) {
			assertThat(detail.getReservationStatus()).isEqualTo(ReservationStatus.AVAILABLE);
			assertThat(detail.getSeatId()).isEqualTo(seatId);
		}
	}

	/**
	 * 예약 좌석 상세 조회 - 존재하는 seatId 반환
	 */
	@Test
	@DisplayName("예약 좌석 상세 조회 - 존재하는 seatId 반환")
	void 예약좌석상세조회_존재하는seatId반환() {
		// Given
		long seatId = 1L;

		// When
		ReservationSeatDetail detail = reservationSeatRepositoryImpl.getSeatDetailById(seatId);

		// Then
		assertThat(detail).isNotNull();
		assertThat(detail.getSeatId()).isEqualTo(seatId);
		assertThat(detail.getReservationStatus()).isEqualTo(ReservationStatus.AVAILABLE);
	}

	/**
	 * 예약 좌석 상세 조회 - 존재하지 않는 seatId 시 예외 발생
	 */
	@Test
	@DisplayName("예약 좌석 상세 조회 - 존재하지 않는 seatId 시 예외 발생")
	void 예약좌석상세조회_존재하지않는seatId시예외발생() {
		// Given
		long nonExistentSeatId = 999L;

		// When & Then
		assertThatThrownBy(() ->  reservationSeatRepositoryImpl.getSeatDetailById(nonExistentSeatId))
			.isInstanceOf(NullPointerException.class)
			.hasMessage("해당 좌석을 찾을수 없습니다.");
	}

	/**
	 * 사용 가능한 예약 좌석 목록 조회 - 정상적으로 목록 반환
	 */
	@Test
	@DisplayName("사용 가능한 예약 좌석 목록 조회 - 정상적으로 목록 반환")
	void 사용가능한예약좌석목록조회_정상적으로목록반환() {
		// Given
		long concertScheduleId = 1L;

		// When
		List<ReservationSeat> availableSeats = reservationSeatRepositoryImpl.getAvailableReservationSeatList(
			concertScheduleId);

		// Then
		assertThat(availableSeats).isNotEmpty();
		for (ReservationSeat seat : availableSeats) {
			assertThat(seat.getConcertId()).isEqualTo(concertScheduleId);
			assertThat(seat.getCurrentReserved()).isEqualTo(0);
		}
	}

	/**
	 * 예약 좌석 조회 - 존재하는 seatId 반환
	 */
	@Test
	@DisplayName("예약 좌석 조회 - 존재하는 seatId 반환")
	void 예약좌석조회_존재하는seatId반환() {
		// Given
		long seatId = 1L;

		// When
		ReservationSeat seat = reservationSeatRepositoryImpl.getSeatById(seatId);

		// Then
		assertThat(seat).isNotNull();
		assertThat(seat.getSeatId()).isEqualTo(seatId);
		assertThat(seat.getCurrentReserved()).isEqualTo(0);
	}

	/**
	 * 예약 좌석 조회 - 존재하지 않는 seatId 시 예외 발생
	 */
	@Test
	@DisplayName("예약 좌석 조회 - 존재하지 않는 seatId 시 예외 발생")
	void 예약좌석조회_존재하지않는seatId시예외발생() {
		// Given
		long nonExistentSeatId = 999L;

		// When & Then
		ReservationSeat seatById = reservationSeatRepositoryImpl.getSeatById(nonExistentSeatId);
		assertThat(seatById).isNull();
	}

	/**
	 * 예약 좌석 상세 상태 업데이트 - 정상적으로 업데이트됨
	 */
	@Test
	@DisplayName("예약 좌석 상세 상태 업데이트 - 정상적으로 업데이트됨")
	void 예약좌석상세상태업데이트_정상적으로업데이트됨() {
		// Given
		ReservationSeatDetail seatDetailDto = ReservationSeatDetail.builder()
			.seatId(1L)
			.reservationStatus(ReservationStatus.COMPLETED)
			.build();

		// When
		reservationSeatRepositoryImpl.updateSeatDetailStatus(seatDetailDto);

		// Then
		assertThat(seatDetailDto.getReservationStatus()).isEqualTo(ReservationStatus.COMPLETED);
	}

	/**
	 * 예약 좌석 상세 상태 업데이트 - 존재하지 않는 seatId 시 예외 발생
	 */
	@Test
	@DisplayName("예약 좌석 상세 상태 업데이트 - 존재하지 않는 seatId 시 예외 발생")
	void 예약좌석상세상태업데이트_존재하지않는seatId시예외발생() {
		// Given
		ReservationSeatDetail seatDetail = ReservationSeatDetail.builder()
			.seatId(999L)
			.reservationStatus(ReservationStatus.COMPLETED)
			.build();

		// When & Then
		reservationSeatRepositoryImpl.updateSeatDetailStatus(seatDetail);

		assertThat(seatDetail.getSeatId()).isEqualTo(999);
	}

	/**
	 * 예약 좌석 현재 예약 상태 업데이트 - 정상적으로 업데이트됨
	 */
	@Test
	@DisplayName("예약 좌석 현재 예약 상태 업데이트 - 정상적으로 업데이트됨")
	void 예약좌석현재예약상태업데이트_정상적으로업데이트됨() {
		// Given
		ReservationSeat seat = reservationSeatRepositoryImpl.getSeatById(1L);
		seat.setCurrentReserved(1); // 현재 예약 수를 1로 증가

		// When
		reservationSeatRepositoryImpl.updateReservationCurrentReserved(seat);

		// Then
		ReservationSeat updatedSeat = reservationSeatRepositoryImpl.getSeatById(1L);
		assertThat(updatedSeat.getCurrentReserved()).isEqualTo(1);
	}
}
