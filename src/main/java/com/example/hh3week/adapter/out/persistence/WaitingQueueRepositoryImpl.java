package com.example.hh3week.adapter.out.persistence;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import com.example.hh3week.application.port.out.WaitingQueueRepositoryPort;
import com.example.hh3week.common.config.exception.CustomException;
import com.example.hh3week.domain.waitingQueue.entity.QWaitingQueue;
import com.example.hh3week.domain.waitingQueue.entity.WaitingQueue;
import com.example.hh3week.domain.waitingQueue.entity.WaitingStatus;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class WaitingQueueRepositoryImpl implements WaitingQueueRepositoryPort {

	private final JPAQueryFactory queryFactory;

	private final QWaitingQueue qWaitingQueue = QWaitingQueue.waitingQueue;

	private final RedisTemplate<String, Object> redisTemplate;


	private  String queueKey = "waitingQueue:";

	@PersistenceContext
	private EntityManager entityManager;

	public WaitingQueueRepositoryImpl(JPAQueryFactory queryFactory, RedisTemplate<String, Object> redisTemplate) {
		this.queryFactory = queryFactory;
		this.redisTemplate = redisTemplate;
	}


	/* Redis를 DB처럼 사용 */
	@Override
	@Transactional
	public WaitingQueue addToQueue(WaitingQueue waitingQueue) {
		//  queueKey += waitingQueue.getSeatDetailId();
		//
		// waitingQueue.setPriority(LocalDateTime.now().getNano());
		//
		// try {
		// 	redisTemplate.opsForZSet().add(queueKey, waitingQueue, waitingQueue.getPriority());
		//
		// } catch (Exception e) {
		// 	throw new IllegalArgumentException("대기열에 추가할수 없습니다.", e);
		// }

		 // 기존 DB 사용 로직
		long seatDetailId = waitingQueue.getSeatDetailId();

		Long maxPriority = queryFactory.select(qWaitingQueue.priority.max())
			.from(qWaitingQueue)
			.where(qWaitingQueue.seatDetailId.eq(seatDetailId))
			.fetchOne();

		if (maxPriority == null) {
			maxPriority = 0L;
		}

		long newPriority = maxPriority + 1;
		waitingQueue.setPriority(newPriority);

		try {
			entityManager.persist(waitingQueue);
			entityManager.flush(); // 즉시 쿼리 실행
		} catch (PersistenceException e) {
			throw new IllegalArgumentException("동일한 우선순위로 대기열에 추가할 수 없습니다.", e);
		}

		return waitingQueue;

		// return waitingQueue;
	}


	/* Redis를 DB처럼 사용 */
	@Override
	public WaitingQueue getQueueStatus(long userId, long seatDetailId) {
		//  queueKey +=  seatDetailId;
		//
		// return Objects.requireNonNull(redisTemplate.opsForZSet().rangeByScore(queueKey, 0, -1))
		// 	.stream()
		// 	.map(Object -> (WaitingQueue)Object)
		// 	.filter(waitingQueue -> waitingQueue.getUserId() == userId)
		// 	.findFirst() // 첫 번째 요소 가져오기
		// 	.orElseThrow(() -> new IllegalArgumentException("대기열에 해당 사용자가 없습니다."));


		//  기존 DB 사용 로직
		return queryFactory.selectFrom(qWaitingQueue)
			.where(qWaitingQueue.userId.eq(userId).and(qWaitingQueue.seatDetailId.eq(seatDetailId)))
			.fetchOne();

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
			.execute();

		if (updatedCount == 0) {
			CustomException.nullPointer("대기열 항목을 찾을 수 없습니다.", this.getClass());
		}
	}

	@Override
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

		return (int)position;
	}

	/**
	 *
	 * @param waitingQueue
	 * @return
	 */
	@Override
	public int getQueuePosition(WaitingQueue waitingQueue) {
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

	/* Redis를 DB처럼 사용 */
	@Override
	public int getQueuePosition(long waitingId, long seatDetailId) {
		queueKey += seatDetailId;

		Double rank = Objects.requireNonNull(redisTemplate.opsForZSet().rangeByScore(queueKey, 0, -1))
			.stream()
			.map(Object -> (WaitingQueue) Object)
			.filter(waitingQueue -> waitingQueue.getWaitingId() == waitingId)
			.findFirst()
			.map(waitingQueue -> redisTemplate.opsForZSet().score(queueKey, waitingQueue))
			.orElseThrow(() -> new IllegalArgumentException("사용자의 대기열 순위를 찾을 수 없습니다."));

		return rank.intValue() + 1; // 1-based 순위 반환
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

	@Override
	public void deleteWaitingQueueFromUser(WaitingQueue waitingQueue) {
		queryFactory.delete(qWaitingQueue)
			.where(qWaitingQueue.userId.eq(waitingQueue.getWaitingId())
				.and(qWaitingQueue.seatDetailId.eq(waitingQueue.getSeatDetailId() ))
			).execute();

	}
}
