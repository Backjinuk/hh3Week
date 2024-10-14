package com.example.hh3week.application.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.hh3week.adapter.in.dto.concert.ConcertDto;
import com.example.hh3week.adapter.in.dto.concert.ConcertScheduleDto;
import com.example.hh3week.application.port.out.ConcertRepositoryPort;

@Service
public class ConcertService {

	private final ConcertRepositoryPort concertRepositoryPort;

	public ConcertService(ConcertRepositoryPort concertRepositoryPort) {
		this.concertRepositoryPort = concertRepositoryPort;
	}

	public List<ConcertDto> getAvilbleConcertList() {
		return concertRepositoryPort.getAvilbleConcertList().stream().map(ConcertDto::ToDto).toList();
	}

	public ConcertDto getConcertFindById(long concertId) {
		return ConcertDto.ToDto(concertRepositoryPort.getConcertFindById(concertId));
	}

	public List<ConcertScheduleDto> getAvilbleConcertScheduletList() {
		return concertRepositoryPort.getAvilbleConcertSchedueList().stream().map(ConcertScheduleDto::ToDto).toList();
	}

	public ConcertScheduleDto getConcertScheduleFindById(long concertId) {
		return ConcertScheduleDto.ToDto(concertRepositoryPort.getConcertScheduleFindById(concertId));
	}
}
