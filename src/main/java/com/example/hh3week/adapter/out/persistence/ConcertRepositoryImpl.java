package com.example.hh3week.adapter.out.persistence;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.example.hh3week.application.port.out.ConcertRepositoryPort;
import com.example.hh3week.domain.concert.entity.Concert;
import com.example.hh3week.domain.concert.entity.ConcertSchedule;

@Repository
public class ConcertRepositoryImpl implements ConcertRepositoryPort {
	/**
	 *
	 * @return
	 */
	@Override
	public List<Concert> getAvilbleConcertList() {
		return List.of();
	}

	/**
	 *
	 * @param concertId
	 * @return
	 */
	@Override
	public Concert getConcertFindById(long concertId) {
		return null;
	}

	/**
	 *
	 * @return
	 */
	@Override
	public List<ConcertSchedule> getAvilbleConcertSchedueList() {
		return List.of();
	}

	/**
	 *
	 * @param concertId
	 * @return
	 */
	@Override
	public ConcertSchedule getConcertScheduleFindById(long concertId) {
		return null;
	}

	@Override
	public List<ConcertSchedule> getConcertsByDate(LocalDateTime startDate, LocalDateTime endDate) {
		return List.of();
	}
}
