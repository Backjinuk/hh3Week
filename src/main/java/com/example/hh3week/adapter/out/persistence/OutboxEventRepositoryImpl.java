package com.example.hh3week.adapter.out.persistence;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.example.hh3week.application.port.out.OutboxEventRepository;
import com.example.hh3week.domain.outBox.QReservationOutBox;
import com.example.hh3week.domain.outBox.ReservationOutBox;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Repository
public class OutboxEventRepositoryImpl implements OutboxEventRepository {

	private final JPAQueryFactory queryFactory;

	@PersistenceContext
	private EntityManager entityManager;

	QReservationOutBox qReservationOutBox = QReservationOutBox.reservationOutBox;

	public OutboxEventRepositoryImpl(JPAQueryFactory queryFactory) {
		this.queryFactory = queryFactory;
	}

	// 1. findByAggregateId: correlationId로 ReservationOutBox 조회
	@Override
	public ReservationOutBox findByAggregateId(String correlationId) {
		return queryFactory.selectFrom(qReservationOutBox)
			.where(qReservationOutBox.aggregateId.eq(correlationId))
			.fetchOne(); // 하나의 결과를 반환
	}

	// 2. findByProcessedFalse: processed가 false인 ReservationOutBox 리스트 조회
	@Override
	public List<ReservationOutBox> findByProcessedFalse() {
		return queryFactory.selectFrom(qReservationOutBox)
			.where(qReservationOutBox.processed.isFalse()) // processed == false
			.fetch(); // 결과 리스트 반환
	}

	// 3. addReservationOutBox: ReservationOutBox 추가
	@Override
	public void addReservationOutBox(ReservationOutBox reservationOutBox) {
		entityManager.persist(reservationOutBox);
	}

	@Override
	public void updateEventOutBox(ReservationOutBox event) {
		queryFactory.update(qReservationOutBox)
			.set(qReservationOutBox.processed ,event.isProcessed())
			.where(qReservationOutBox.aggregateId.eq(event.getAggregateId()));
	}
}
