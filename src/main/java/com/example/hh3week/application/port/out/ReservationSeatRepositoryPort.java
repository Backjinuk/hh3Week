package com.example.hh3week.application.port.out;

import java.util.List;

import com.example.hh3week.adapter.in.dto.reservation.ReservationSeatDetailDto;
import com.example.hh3week.domain.reservation.entity.ReservationSeat;
import com.example.hh3week.domain.reservation.entity.ReservationSeatDetail;

public interface ReservationSeatRepositoryPort {
	List<ReservationSeatDetail> getAvailableReservationSeatDetailList(long seatId);

	List<ReservationSeat> getAvailableReservationSeatList(long concertScheduleId);

	void updateReservationCurrentReserved(ReservationSeat reservationSeat);

	void updateSeatDetailStatus(ReservationSeatDetail seatDetail);

	ReservationSeatDetail getSeatDetailById(long seatDetailId);

	ReservationSeat getSeatById(long seatId);
}
