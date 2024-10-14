package com.example.hh3week.application.port.out;

import java.time.LocalDateTime;
import java.util.List;

import com.example.hh3week.domain.concert.entity.Concert;
import com.example.hh3week.domain.concert.entity.ConcertSchedule;

public interface ConcertRepositoryPort {
	List<Concert> getAvilbleConcertList();

	Concert getConcertFindById(long concertId);

	List<ConcertSchedule> getAvilbleConcertSchedueList();

	ConcertSchedule getConcertScheduleFindById(long concertId);

	List<ConcertSchedule> getConcertsByDate(LocalDateTime startDate, LocalDateTime endDate);
}
