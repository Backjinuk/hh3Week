package com.example.hh3week.application.service;

import org.springframework.stereotype.Service;

import com.example.hh3week.application.port.out.ConcertRepositoryPort;

@Service
public class ConcertService {

	private final ConcertRepositoryPort concertRepository;

	public ConcertService(ConcertRepositoryPort concertRepository) {
		this.concertRepository = concertRepository;
	}
}
