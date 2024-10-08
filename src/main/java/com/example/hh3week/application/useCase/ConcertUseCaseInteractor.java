package com.example.hh3week.application.useCase;

import org.springframework.stereotype.Service;

import com.example.hh3week.application.port.in.ConcertUseCase;
import com.example.hh3week.application.service.ConcertService;

@Service
public class ConcertUseCaseInteractor implements ConcertUseCase {

	private final ConcertService concertService;

	public ConcertUseCaseInteractor(ConcertService concertService) {
		this.concertService = concertService;
	}
}
