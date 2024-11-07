package com.example.hh3week.adapter.out.persistence;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import com.example.hh3week.adapter.in.dto.waitingQueue.WaitingQueueDto;
import com.example.hh3week.application.port.out.WaitingQueueRepositoryPort;
import com.example.hh3week.common.config.exception.CustomException;
import com.example.hh3week.domain.waitingQueue.entity.QWaitingQueue;
import com.example.hh3week.domain.waitingQueue.entity.WaitingQueue;
import com.example.hh3week.domain.waitingQueue.entity.WaitingStatus;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class WaitingQueueRepositoryImpl implements WaitingQueueRepositoryPort {

	private final JPAQueryFactory queryFactory;

	private final QWaitingQueue qWaitingQueue = QWaitingQueue.waitingQueue;

	private final RedisTemplate<String, Object> redisTemplate;

	@PersistenceContext
	private EntityManager entityManager;

	public WaitingQueueRepositoryImpl(JPAQueryFactory queryFactory, RedisTemplate<String, Object> redisTemplate) {
		this.queryFactory = queryFactory;
		this.redisTemplate = redisTemplate;
	}


	@Override
	@Transactional
	public WaitingQueue addToQueue(WaitingQueue waitingQueue) {
		long seatDetailId = waitingQueue.getSeatDetailId();
		String queueKey = "waitingQueue:" + seatDetailId;

		long newPriority = redisTemplate.opsForZSet().zCard(queueKey) + 1;

		waitingQueue.setPriority(newPriority);

		try {
			redisTemplate.opsForZSet().add(queueKey, WaitingQueueDto.ToDto(waitingQueue), newPriority);
		}catch (Exception e){
			throw new IllegalArgumentException("대기열에 추가할수 없습니다.", e);
		}


		return waitingQueue;
	}



	@Override
	public WaitingQueue getNextInQueue(long seatDetailId) {

		// 예외를 던지지 않고, 대기열 항목이 없을 경우 null을 반환
		return queryFactory.selectFrom(qWaitingQueue)
			.where(
				qWaitingQueue.seatDetailId.eq(seatDetailId).and(qWaitingQueue.waitingStatus.eq(WaitingStatus.WAITING)))
			.orderBy(qWaitingQueue.priority.desc(), qWaitingQueue.reservationDt.asc())
			.fetchFirst();
	}

	@Override
	public void updateStatus(long waitingId, WaitingStatus status) {
		long updatedCount = queryFactory.update(qWaitingQueue)
			.set(qWaitingQueue.waitingStatus, status)
			.where(qWaitingQueue.waitingId.eq(waitingId))
			// .setLockMode(LockModeType.OPTIMISTIC)
			.execute();

		if (updatedCount == 0) {
			CustomException.nullPointer("대기열 항목을 찾을 수 없습니다.", this.getClass());
		}
	}

	@Override
	public WaitingQueue getQueueStatus(long userId, long seatDetailId) {
		return queryFactory.selectFrom(qWaitingQueue)
			.where(qWaitingQueue.userId.eq(userId).and(qWaitingQueue.seatDetailId.eq(seatDetailId)))
			// .setLockMode(LockModeType.OPTIMISTIC) // 비관적 잠금 설정
			.fetchOne();
	}

	@Override
	public int getQueuePosition(long waitingId) {
		WaitingQueue waitingQueue = queryFactory.selectFrom(qWaitingQueue)
			.where(qWaitingQueue.waitingId.eq(waitingId))
			// .setLockMode(LockModeType.OPTIMISTIC)
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

		return (int)position;
	}

	@Override
	public void expireQueue(long waitingId) {
		WaitingQueue waitingQueue = queryFactory.selectFrom(qWaitingQueue)
			.where(qWaitingQueue.waitingId.eq(waitingId))
			.fetchOne();

		if (waitingQueue == null) {
			CustomException.nullPointer("대기열 항목을 찾을 수 없습니다.", this.getClass());
		}

		Objects.requireNonNull(waitingQueue).setWaitingStatus(WaitingStatus.EXPIRED);
		entityManager.merge(waitingQueue);
	}

	@Override
	public List<WaitingQueue> getQueueBySeatDetailId(long seatDetailId) {
		return queryFactory.selectFrom(qWaitingQueue)
			.where(qWaitingQueue.seatDetailId.eq(seatDetailId))
			.orderBy(qWaitingQueue.priority.asc(), qWaitingQueue.reservationDt.asc())
			.fetch();
	}

	@Override
	public void clearQueue() {
		queryFactory.delete(qWaitingQueue).execute();
	}

	@Override
	public List<WaitingQueue> findExpiredQueues(LocalDateTime currentTime) {
		return queryFactory.selectFrom(qWaitingQueue)
			.where(qWaitingQueue.waitingStatus.eq(WaitingStatus.WAITING)
				.and(qWaitingQueue.reservationDt.before(currentTime)))
			.fetch();
	}

	@Override
	public Long findMaxPriorityBySeatDetailIdForUpdate(long seatDetailId) {
		return queryFactory.select(qWaitingQueue.priority.max())
			.from(qWaitingQueue)
			.where(qWaitingQueue.seatDetailId.eq(seatDetailId))
			// .setLockMode(LockModeType.OPTIMISTIC) // 비관적 잠금 설정
			.fetchOne();
	}
}
