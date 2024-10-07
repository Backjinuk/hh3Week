package com.example.hh3week.application.domain.concert.service;

import org.springframework.stereotype.Service;

import com.example.hh3week.application.domain.concert.repository.ConcertRepository;

@Service
public class ConcertService {

	private final ConcertRepository concertRepository;

	public ConcertService(ConcertRepository concertRepository) {
		this.concertRepository = concertRepository;
	}
}
