package com.example.hh3week.adapter.in.useCase;

import org.springframework.stereotype.Service;

import com.example.hh3week.application.domain.reservation.service.ReservationService;

@Service
public class ReservationUseCase {

	private final ReservationService reservationService;

	public ReservationUseCase(ReservationService reservationService) {
		this.reservationService = reservationService;
	}
}
