package com.example.hh3week.application.port.in;

import java.time.LocalDateTime;
import java.util.List;

import com.example.hh3week.adapter.in.dto.concert.ConcertScheduleDto;

public interface ConcertUseCase {

	List<ConcertScheduleDto> getAvailableConcertDates(LocalDateTime startDate, LocalDateTime endDate);

	ConcertScheduleDto getConcertScheduleFindById(long concertId);
}
