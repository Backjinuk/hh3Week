package com.example.hh3week.application.service;

import org.springframework.stereotype.Service;

import com.example.hh3week.application.port.out.ReservationSeatDetailRepositoryPort;
import com.example.hh3week.application.port.out.ReservationSeatRepositoryPort;

@Service
public class ReservationService {

	private final ReservationSeatRepositoryPort reservationSeatRepositoryPort;
	private final ReservationSeatDetailRepositoryPort reservationSeatDetailRepositoryPort;

	public ReservationService(
		ReservationSeatRepositoryPort reservationSeatRepositoryPort, ReservationSeatDetailRepositoryPort reservationSeatDetailRepositoryPort) {
		this.reservationSeatRepositoryPort = reservationSeatRepositoryPort;
		this.reservationSeatDetailRepositoryPort = reservationSeatDetailRepositoryPort;
	}
}
