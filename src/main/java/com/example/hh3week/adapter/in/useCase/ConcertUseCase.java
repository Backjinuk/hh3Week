package com.example.hh3week.adapter.in.useCase;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.hh3week.application.domain.concert.service.ConcertService;

@Service
public class ConcertUseCase {

	private final ConcertService concertService;

	public ConcertUseCase(ConcertService concertService) {
		this.concertService = concertService;
	}
}
