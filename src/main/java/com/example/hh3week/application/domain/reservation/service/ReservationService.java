package com.example.hh3week.application.domain.reservation.service;

import org.springframework.stereotype.Service;

import com.example.hh3week.application.domain.reservation.repository.ReservationSeatDetailRepository;
import com.example.hh3week.application.domain.reservation.repository.ReservationSeatRepository;

@Service
public class ReservationService {

	private final ReservationSeatRepository reservationSeatRepository;
	private final ReservationSeatDetailRepository reservationSeatDetailRepository;

	public ReservationService(ReservationSeatRepository reservationSeatRepository, ReservationSeatDetailRepository reservationSeatDetailRepository) {
		this.reservationSeatRepository = reservationSeatRepository;
		this.reservationSeatDetailRepository = reservationSeatDetailRepository;
	}
}
