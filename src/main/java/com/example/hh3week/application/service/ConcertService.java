package com.example.hh3week.application.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.hh3week.adapter.in.dto.concert.ConcertDto;
import com.example.hh3week.adapter.in.dto.concert.ConcertScheduleDto;
import com.example.hh3week.application.port.out.ConcertRepositoryPort;
import com.example.hh3week.domain.concert.entity.Concert;
import com.example.hh3week.domain.concert.entity.ConcertSchedule;

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

	@Transactional(readOnly = true)
	public List<ConcertScheduleDto> getConcertsByDate(LocalDateTime startDate, LocalDateTime endDate) {
		return concertRepositoryPort.getConcertsByDate(startDate, endDate).stream().map(ConcertScheduleDto::ToDto).toList();
	}

}
