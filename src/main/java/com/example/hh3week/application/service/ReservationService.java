package com.example.hh3week.application.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.hh3week.adapter.in.dto.reservation.ReservationSeatDetailDto;
import com.example.hh3week.adapter.in.dto.reservation.ReservationSeatDto;
import com.example.hh3week.application.port.out.ReservationSeatRepositoryPort;
import com.example.hh3week.domain.reservation.entity.ReservationSeat;

@Service
public class ReservationService {

	private final ReservationSeatRepositoryPort reservationSeatRepositoryPort;

	public ReservationService(ReservationSeatRepositoryPort reservationSeatRepositoryPort) {
		this.reservationSeatRepositoryPort = reservationSeatRepositoryPort;
	}

	/*
	 * 특적 콘서트의 좌석 마스터정보 가지고 오기
	 * */
	public List<ReservationSeatDto> getAvailableReservationSeatList(long concertScheduleId){
		return reservationSeatRepositoryPort.getAvailableReservationSeatList(concertScheduleId).stream().map(ReservationSeatDto::ToDto).toList();
	}


	/*
	* 특정 좌석마스터 의 예약가능한 좌석 정보 가지고 오기
	*
	* */
	public List<ReservationSeatDetailDto> getAvailableReservationSeatDetailList(long seatId){
		return reservationSeatRepositoryPort.getAvailableReservationSeatDetailList(seatId).stream().map(ReservationSeatDetailDto::ToDto).toList();
	}



	/**
	 * 좌석 예약 상태를 업데이트하는 메서드
	 * @param seat 예약할 좌석 엔티티
	 */
	@Transactional
	public void updateSeatReservation(ReservationSeatDto seat) {
		seat.setCurrentReserved(seat.getCurrentReserved() + 1);
		reservationSeatRepositoryPort.updateReservationCurrentReserved(ReservationSeat.ToEntity(seat));
	}
}
