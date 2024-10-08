package com.example.hh3week.application.useCase;

import org.springframework.stereotype.Service;

import com.example.hh3week.application.port.in.ReservationUseCase;
import com.example.hh3week.application.service.ReservationService;

@Service
public class ReservationUseCaseInteractor implements ReservationUseCase {

	private final ReservationService reservationService;

	public ReservationUseCaseInteractor(ReservationService reservationService) {
		this.reservationService = reservationService;
	}
}
