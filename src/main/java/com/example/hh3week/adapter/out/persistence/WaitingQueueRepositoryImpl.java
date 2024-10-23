package com.example.hh3week.adapter.out.persistence;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.hh3week.application.port.out.WaitingQueueRepositoryPort;
import com.example.hh3week.common.config.CustomException;
import com.example.hh3week.domain.waitingQueue.entity.QWaitingQueue;
import com.example.hh3week.domain.waitingQueue.entity.WaitingQueue;
import com.example.hh3week.domain.waitingQueue.entity.WaitingStatus;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class WaitingQueueRepositoryImpl implements WaitingQueueRepositoryPort {

	private final JPAQueryFactory queryFactory;

	private final QWaitingQueue qWaitingQueue = QWaitingQueue.waitingQueue;

	@PersistenceContext
	private EntityManager entityManager;

	public WaitingQueueRepositoryImpl(JPAQueryFactory queryFactory) {
		this.queryFactory = queryFactory;
	}

	@Override
	@Transactional
	public WaitingQueue addToQueue(WaitingQueue waitingQueue) {
		entityManager.persist(waitingQueue);
		return waitingQueue;
	}

	@Override
	@Transactional(readOnly = true)
	public WaitingQueue getNextInQueue(long seatDetailId) {
		WaitingQueue nextInQueue = queryFactory.selectFrom(qWaitingQueue)
			.where(qWaitingQueue.seatDetailId.eq(seatDetailId)
				.and(qWaitingQueue.waitingStatus.eq(WaitingStatus.WAITING)))
			.orderBy(qWaitingQueue.priority.desc(), qWaitingQueue.reservationDt.asc())
			.fetchFirst();

		if (nextInQueue == null) {
			return WaitingQueue.builder().priority(1).build();
		}

		return nextInQueue;
	}

	@Override
	@Transactional
	public void updateStatus(long waitingId, WaitingStatus status) {
		long updatedCount = queryFactory.update(qWaitingQueue)
			.set(qWaitingQueue.waitingStatus, status)
			.where(qWaitingQueue.waitingId.eq(waitingId))
			.execute();

		if (updatedCount == 0) {
			CustomException.nullPointer("대기열 항목을 찾을 수 없습니다.", this.getClass());
		}
	}

	@Override
	@Transactional(readOnly = true)
	public WaitingQueue getQueueStatus(long userId, long seatDetailId) {

		return queryFactory.selectFrom(qWaitingQueue)
			.where(qWaitingQueue.userId.eq(userId)
				.and(qWaitingQueue.seatDetailId.eq(seatDetailId)))
			.fetchOne();
	}

	@Override
	@Transactional(readOnly = true)
	public int getQueuePosition(long waitingId) {
		WaitingQueue waitingQueue = queryFactory.selectFrom(qWaitingQueue)
			.where(qWaitingQueue.waitingId.eq(waitingId))
			.fetchOne();

		if (waitingQueue == null) {
			CustomException.nullPointer("대기열 항목을 찾을 수 없습니다.", this.getClass());
		}

		Long positionCount = queryFactory.select(qWaitingQueue.priority)
			.from(qWaitingQueue)
			.where(qWaitingQueue.seatDetailId.eq(waitingQueue.getSeatDetailId())
				.and(qWaitingQueue.waitingStatus.eq(WaitingStatus.WAITING))
				.and(qWaitingQueue.priority.goe(waitingQueue.getPriority()))
				.and(qWaitingQueue.reservationDt.lt(waitingQueue.getReservationDt())))
			.fetchOne();

		long position = (positionCount != null ? positionCount : 0L) + 1;

		return (int) position;
	}
}
