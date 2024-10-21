package com.example.hh3week.adapter.out.persistence;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.hh3week.application.port.out.ConcertRepositoryPort;
import com.example.hh3week.common.config.CustomException;
import com.example.hh3week.domain.concert.entity.Concert;
import com.example.hh3week.domain.concert.entity.ConcertSchedule;
import com.example.hh3week.domain.concert.entity.ConcertScheduleStatus;
import com.example.hh3week.domain.concert.entity.QConcert;
import com.example.hh3week.domain.concert.entity.QConcertSchedule;
import com.querydsl.jpa.impl.JPAQueryFactory;

@Repository
public class ConcertRepositoryImpl implements ConcertRepositoryPort {

	private final JPAQueryFactory queryFactory;

	private final QConcert qConcert = QConcert.concert;
	private final QConcertSchedule qConcertSchedule = QConcertSchedule.concertSchedule;

	public ConcertRepositoryImpl(JPAQueryFactory queryFactory) {
		this.queryFactory = queryFactory;
	}

	@Override
	@Transactional(readOnly = true)
	public List<Concert> getAvilbleConcertList() {
		List<Concert> concerts = queryFactory.selectFrom(qConcert)
			.fetch();

		if (concerts.isEmpty()) {
			CustomException.nullPointer("사용 가능한 콘서트를 찾을 수 없습니다.", this.getClass());
		}

		return concerts;
	}


	@Override
	@Transactional(readOnly = true)
	public Concert getConcertFindById(long concertId) {
		Concert concert = queryFactory.selectFrom(qConcert)
			.where(qConcert.concertId.eq(concertId))
			.fetchOne();

		if (concert == null) {
			CustomException.nullPointer("콘서트를 찾을 수 없습니다.", this.getClass());
		}

		return concert;
	}

	@Override
	@Transactional(readOnly = true)
	public List<ConcertSchedule> getAvilbleConcertSchedueList() {
		List<ConcertSchedule> concertSchedules = queryFactory.selectFrom(qConcertSchedule)
			.where(qConcertSchedule.concertScheduleStatus.eq(ConcertScheduleStatus.AVAILABLE))
			.fetch();

		if (concertSchedules.isEmpty()) {
			CustomException.nullPointer("사용 가능한 콘서트 스케줄을 찾을 수 없습니다." , this.getClass());
		}

		return concertSchedules;
	}

	@Override
	@Transactional(readOnly = true)
	public ConcertSchedule getConcertScheduleFindById(long concertScheduleId) {
		ConcertSchedule concertSchedule = queryFactory.selectFrom(qConcertSchedule)
			.where(qConcertSchedule.concertScheduleId.eq(concertScheduleId))
			.fetchOne();

		if (concertSchedule == null) {
			CustomException.nullPointer("콘서트 스케줄을 찾을 수 없습니다.", this.getClass());
		}

		return concertSchedule;
	}

	@Override
	@Transactional(readOnly = true)
	public List<ConcertSchedule> getConcertsByDate(LocalDateTime startDate, LocalDateTime endDate) {

		List<ConcertSchedule> concertSchedules = queryFactory.selectFrom(qConcertSchedule)
			.where(qConcertSchedule.startDt.between(startDate, endDate))
			.fetch();

		if (concertSchedules.isEmpty()) {
			CustomException.nullPointer("콘서트 스케줄을 찾을 수 없습니다.", this.getClass());
		}

		return concertSchedules;
	}
}
