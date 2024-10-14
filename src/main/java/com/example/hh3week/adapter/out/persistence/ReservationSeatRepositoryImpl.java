package com.example.hh3week.adapter.out.persistence;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.example.hh3week.application.port.out.ReservationSeatRepositoryPort;
import com.example.hh3week.domain.concert.entity.Concert;
import com.example.hh3week.domain.reservation.entity.ReservationSeat;
import com.example.hh3week.domain.reservation.entity.ReservationSeatDetail;

@Repository
public class ReservationSeatRepositoryImpl implements ReservationSeatRepositoryPort {

	@Override
	public List<ReservationSeatDetail> getAvailableReservationSeatDetailList(long seatId) {
		return List.of();
	}

	@Override
	public List<ReservationSeat> getAvailableReservationSeatList(long concertScheduleId) {
		return List.of();
	}

	@Override
	public void updateReservationCurrentReserved(ReservationSeat reservationSeat) {

	}
}