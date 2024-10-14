package com.example.hh3week.application.port.out;

import java.util.Collection;
import java.util.List;

import com.example.hh3week.adapter.in.dto.reservation.ReservationSeatDto;
import com.example.hh3week.domain.concert.entity.Concert;
import com.example.hh3week.domain.reservation.entity.ReservationSeat;
import com.example.hh3week.domain.reservation.entity.ReservationSeatDetail;

public interface ReservationSeatRepositoryPort {
	List<ReservationSeatDetail> getAvailableReservationSeatDetailList(long seatId);

	List<ReservationSeat> getAvailableReservationSeatList(long concertScheduleId);

	void updateReservationCurrentReserved(ReservationSeat reservationSeat);
}
