package com.example.hh3week.application.port.in;

import java.time.LocalDateTime;
import java.util.List;

import com.example.hh3week.adapter.in.dto.concert.ConcertScheduleDto;

public interface ConcertUseCase {

	public List<ConcertScheduleDto> getAvailableConcertDates(LocalDateTime startDate, LocalDateTime endDate);

	public ConcertScheduleDto getConcertScheduleFindById(long concertId);
}
