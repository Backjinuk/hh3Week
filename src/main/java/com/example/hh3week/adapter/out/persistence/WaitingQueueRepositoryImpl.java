package com.example.hh3week.adapter.out.persistence;

import java.util.Optional;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.hh3week.application.port.out.WaitingQueueRepositoryPort;
import com.example.hh3week.domain.waitingQueue.entity.WaitingQueue;
import com.example.hh3week.domain.waitingQueue.entity.WaitingStatus;
import com.example.hh3week.domain.waitingQueue.entity.QWaitingQueue;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Repository
public class WaitingQueueRepositoryImpl implements WaitingQueueRepositoryPort {

	private final JPAQueryFactory queryFactory;

	private final QWaitingQueue qWaitingQueue = QWaitingQueue.waitingQueue;

	@PersistenceContext
	private EntityManager entityManager;

	public WaitingQueueRepositoryImpl(JPAQueryFactory queryFactory) {
		this.queryFactory = queryFactory;
	}

	/**
	 * 대기열에 새로운 항목을 추가합니다.
	 *
	 * @param waitingQueue 추가할 WaitingQueue 객체
	 * @return 저장된 WaitingQueue 객체
	 */
	@Override
	@Transactional
	public WaitingQueue addToQueue(WaitingQueue waitingQueue) {
		entityManager.persist(waitingQueue);
		return waitingQueue;
	}

	/**
	 * 특정 콘서트 스케줄에 대한 다음 대기열 항목을 반환합니다.
	 *
	 * @param concertScheduleId 콘서트 스케줄 ID
	 * @return 다음 WaitingQueue 객체
	 * @throws IllegalArgumentException 대기열에 항목이 없을 경우
	 */
	@Override
	@Transactional(readOnly = true)
	public WaitingQueue getNextInQueue(long concertScheduleId) {
		Optional<WaitingQueue> nextInQueue = Optional.ofNullable(
			queryFactory.selectFrom(qWaitingQueue)
				.where(qWaitingQueue.concertScheduleId.eq(concertScheduleId)
					.and(qWaitingQueue.waitingStatus.eq(WaitingStatus.WAITING)))
				.orderBy(qWaitingQueue.priority.desc(), qWaitingQueue.reservationDt.asc())
				.fetchFirst()
		);

		return nextInQueue.orElseThrow(() ->
			new IllegalArgumentException("No waiting queue found for concertScheduleId: " + concertScheduleId)
		);
	}

	/**
	 * 대기열 항목의 상태를 업데이트합니다.
	 *
	 * @param waitingId 대기열 항목 ID
	 * @param status    업데이트할 상태
	 * @throws IllegalArgumentException 해당 대기열 항목을 찾을 수 없을 경우
	 */
	@Override
	@Transactional
	public void updateStatus(long waitingId, WaitingStatus status) {
		long updatedCount = queryFactory.update(qWaitingQueue)
			.set(qWaitingQueue.waitingStatus, status)
			.where(qWaitingQueue.waitingId.eq(waitingId))
			.execute();

		if (updatedCount == 0) {
			throw new IllegalArgumentException("Waiting Queue not found with waitingId: " + waitingId);
		}
	}

	/**
	 * 특정 사용자와 좌석 상세 ID에 대한 대기열 상태를 조회합니다.
	 *
	 * @param userId        사용자 ID
	 * @param concertScheduleId  좌석 상세 ID
	 * @return 해당 WaitingQueue 객체
	 * @throws IllegalArgumentException 해당 대기열 상태를 찾을 수 없을 경우
	 */
	@Override
	@Transactional(readOnly = true)
	public WaitingQueue getQueueStatus(long userId, long concertScheduleId) {
		return 	queryFactory.selectFrom(qWaitingQueue)
			.where(qWaitingQueue.userId.eq(userId)
				.and(qWaitingQueue.concertScheduleId.eq(concertScheduleId)))
			.fetchOne();
	}

	/**
	 * 대기열 항목의 위치를 조회합니다.
	 *
	 * @param waitingId 대기열 항목 ID
	 * @return 대기열에서의 위치 (1부터 시작)
	 * @throws IllegalArgumentException 해당 대기열 항목을 찾을 수 없을 경우
	 */
	@Override
	@Transactional(readOnly = true)
	public int getQueuePosition(long waitingId) {
		// waitingId로 WaitingQueue 조회
		WaitingQueue waitingQueue = queryFactory.selectFrom(qWaitingQueue)
			.where(qWaitingQueue.waitingId.eq(waitingId))
			.fetchOne();

		// 조회된 대기열 항목이 없을 경우 예외 발생
		if (waitingQueue == null) {
			throw new IllegalArgumentException("Waiting Queue not found with waitingId: " + waitingId);
		}

		// 대기열 위치 계산을 위한 카운트 조회
		Long positionCount = queryFactory.select(qWaitingQueue.count())
			.from(qWaitingQueue)
			.where(qWaitingQueue.concertScheduleId.eq(waitingQueue.getConcertScheduleId())
				.and(qWaitingQueue.waitingStatus.eq(WaitingStatus.WAITING))
				.and(qWaitingQueue.priority.goe(waitingQueue.getPriority()))
				.and(qWaitingQueue.reservationDt.lt(waitingQueue.getReservationDt())))
			.fetchOne();

		// positionCount가 null일 경우 0으로 처리하여 NullPointerException 방지
		long position = (positionCount != null ? positionCount : 0L) + 1;

		return (int) position;
	}
}
