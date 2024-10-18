// src/main/java/com/example/hh3week/adapter/out/persistence/ConcertRepositoryImpl.java

package com.example.hh3week.adapter.out.persistence;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.hh3week.application.port.out.ConcertRepositoryPort;
import com.example.hh3week.domain.concert.entity.Concert;
import com.example.hh3week.domain.concert.entity.ConcertSchedule;
import com.example.hh3week.domain.concert.entity.ConcertScheduleStatus;
import com.example.hh3week.domain.concert.entity.QConcert;
import com.example.hh3week.domain.concert.entity.QConcertSchedule;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.jpa.JPAExpressions;

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
		return queryFactory.selectFrom(qConcert)
			.fetch();
	}


	@Override
	@Transactional(readOnly = true)
	public Concert getConcertFindById(long concertId) {

		return queryFactory.selectFrom(qConcert)
			.where(qConcert.concertId.eq(concertId))
			.fetchOne();
	}

	/**
	 * 사용 가능한 모든 콘서트 스케줄 목록을 반환합니다.
	 *
	 * @return List of available ConcertSchedules
	 */
	@Override
	@Transactional(readOnly = true)
	public List<ConcertSchedule> getAvilbleConcertSchedueList() {
		return queryFactory.selectFrom(qConcertSchedule)
			.where(qConcertSchedule.concertScheduleStatus.eq(ConcertScheduleStatus.AVAILABLE))
			.fetch();
	}


	@Override
	@Transactional(readOnly = true)
	public ConcertSchedule getConcertScheduleFindById(long concertScheduleId) {

		return queryFactory.selectFrom(qConcertSchedule)
			.where(qConcertSchedule.concertScheduleId.eq(concertScheduleId))
			.fetchOne();
	}


	@Override
	@Transactional(readOnly = true)
	public List<ConcertSchedule> getConcertsByDate(LocalDateTime startDate, LocalDateTime endDate) {
		return queryFactory.selectFrom(qConcertSchedule)
			.where(qConcertSchedule.startDt.between(startDate, endDate))
			.fetch();
	}
}
