package com.example.hh3week.application.useCase;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.hh3week.adapter.in.dto.reservation.ReservationSeatDetailDto;
import com.example.hh3week.adapter.in.dto.reservation.ReservationSeatDto;
import com.example.hh3week.adapter.in.dto.token.TokenDto;
import com.example.hh3week.adapter.in.dto.waitingQueue.WaitingQueueDto;
import com.example.hh3week.application.port.in.ReservationUseCase;
import com.example.hh3week.application.service.ReservationService;
import com.example.hh3week.application.service.TokenService;
import com.example.hh3week.application.service.WaitingQueueService;
import com.example.hh3week.domain.reservation.entity.ReservationStatus;
import com.example.hh3week.domain.waitingQueue.entity.WaitingQueue;
import com.example.hh3week.domain.waitingQueue.entity.WaitingStatus;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ReservationUseCaseInteractor implements ReservationUseCase {

	private final ReservationService reservationService;
	private final WaitingQueueService waitingQueueService;
	private final TokenService tokenService;
	private final RedissonClient redissonClient;
	private final RedisTemplate<String, Object> redisTemplate;

	public ReservationUseCaseInteractor(ReservationService reservationService, WaitingQueueService waitingQueueService,
		TokenService tokenService, RedissonClient redissonClient, RedisTemplate<String, Object> redisTemplate) {
		this.reservationService = reservationService;
		this.waitingQueueService = waitingQueueService;
		this.tokenService = tokenService;
		this.redissonClient = redissonClient;
		this.redisTemplate = redisTemplate;
	}

	@Override
	public List<ReservationSeatDto> getAvailableReservationSeatList(long concertScheduleId) {
		return reservationService.getAvailableReservationSeatList(concertScheduleId)
			.stream()
			.peek(reservationSeatDto -> reservationSeatDto.setReservationSeatDetailDtoList(
				reservationService.getAvailableReservationSeatDetailList(reservationSeatDto.getSeatId())))
			.toList();
	}

	@Transactional
	public TokenDto reserveSeat(long userId, long seatDetailId) {
		String lockKey = "lock:seat:" + seatDetailId; // 락 키를 seatDetailId 기준으로 변경
		RLock lock = redissonClient.getLock(lockKey);
		boolean isLocked = false;
		try {
			isLocked = lock.tryLock(5, 30, TimeUnit.SECONDS);
			if (!isLocked) {
				throw new IllegalArgumentException("좌석 락을 획득할 수 없습니다. 다시 시도해주세요.");
			}

			// 실제 비즈니스 로직 수행
			return reserveSeatTransactional(userId, seatDetailId);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new IllegalArgumentException("락 획득이 인터럽트되었습니다.");
		} finally {
			if (isLocked && lock.isHeldByCurrentThread()) {
				lock.unlock();
			}
		}
	}

	public TokenDto reserveSeatTransactional(long userId, long seatDetailId) {
		String queueKey = "waitingQueue:" + seatDetailId;

		// Step 1: 대기열에 이미 등록된 사용자 확인
		if (redisTemplate.opsForZSet().score(queueKey, String.valueOf(userId)) != null) {
			throw new IllegalArgumentException("사용자가 이미 대기열에 등록되어 있습니다.");
		}


		// Step 2: 낙관적 락을 사용하여 좌석 상세 정보 조회
		ReservationSeatDetailDto seatDetail = reservationService.getSeatDetailById(seatDetailId);

		// Step 3: 좌석 상태 확인 및 예약 처리
		if (seatDetail.getReservationStatus() == ReservationStatus.AVAILABLE) {
			seatDetail.setReservationStatus(ReservationStatus.PENDING);
			reservationService.updateSeatDetailStatus(seatDetail); // @Version 필드 자동 업데이트

			log.info("사용자 {}의 좌석 예약 성공. 좌석 ID: {}", userId, seatDetailId);
			TokenDto token = tokenService.createToken(userId, 0, calculateRemainingTime(0), seatDetailId);

			// 토큰을 별도의 ZSet에 추가 (예: tokens ZSet)
			Boolean tokenAdded = redisTemplate.opsForZSet().add("tokens", String.valueOf(token.getTokenId()), (double) token.getTokenId());
			if (Boolean.TRUE.equals(tokenAdded)) {
				log.info("토큰 ID {}가 'tokens' ZSet에 성공적으로 추가되었습니다.", token.getTokenId());
			} else {
				log.warn("토큰 ID {}를 'tokens' ZSet에 추가하는 데 실패했습니다.", token.getTokenId());
			}

			return token;
		} else {
			// 좌석이 AVAILABLE이 아닌 경우, 대기열에 사용자 추가
			WaitingQueueDto waitingId = waitingQueueService.addWaitingQueue(buildWaitingQueueDto(userId, seatDetailId));

			// Redis ZSet에 사용자 추가 (userId를 멤버로, priority를 점수로 설정)
			Boolean userAdded = redisTemplate.opsForZSet().add(queueKey, String.valueOf(userId), (double) waitingId.getPriority());
			if (Boolean.TRUE.equals(userAdded)) {
				log.info("사용자 ID {}가 '{}' ZSet에 성공적으로 추가되었습니다.", userId, queueKey);
			} else {
				log.warn("사용자 ID {}를 '{}' ZSet에 추가하는 데 실패했습니다.", userId, queueKey);
			}

			// 대기열 위치 계산
			int queuePosition = getQueuePosition2(seatDetailId, userId);
			log.info("사용자 {}의 대기열 위치: {}", userId, queuePosition);

			// 토큰 발급
			long remainingTime = calculateRemainingTime(queuePosition);
			log.info("사용자 {}의 대기열 등록 완료. 좌석 ID: {}, 대기열 위치: {}, 남은 시간: {}초", userId, seatDetailId, queuePosition, remainingTime);

			TokenDto token = tokenService.createToken(userId, queuePosition, remainingTime, seatDetailId);

			// 토큰을 별도의 ZSet에 추가 (예: tokens ZSet)
			Boolean tokenAdded = redisTemplate.opsForZSet().add("tokens", String.valueOf(token.getTokenId()), (double) token.getTokenId());
			if (Boolean.TRUE.equals(tokenAdded)) {
				log.info("토큰 ID {}가 'tokens' ZSet에 성공적으로 추가되었습니다.", token.getTokenId());
			} else {
				log.warn("토큰 ID {}를 'tokens' ZSet에 추가하는 데 실패했습니다.", token.getTokenId());
			}

			return token;
		}
	}

	@Override
	public CompletableFuture<TokenDto> sendReservationRequest(long userId, long seatId) {
		return reservationService.sendReservationRequest(userId, seatId);
	}

	/**
	 * 대기열에서 남은 시간을 계산하는 메서드 (예시)
	 *
	 * @param queuePosition 대기열에서의 위치
	 * @return 남은 대기 시간 (초 단위)
	 */
	private long calculateRemainingTime(int queuePosition) {
		// 예시: 각 사용자당 5분의 대기 시간 부여
		return queuePosition * 300L;
	}

	private int getQueuePosition2(long seatDetailId, long userId) {
		String queueKey = "waitingQueue:" + seatDetailId;

		// Step 1: Redis에서 대기열 위치 조회
		Long rank = redisTemplate.opsForZSet().rank(queueKey, String.valueOf(userId));

		if (rank != null) {
			log.debug("Redis에서 사용자 ID {}의 대기열 순위: {}", userId, rank);
			return rank.intValue() + 1; // 1-based index
		} else {
			// Step 2: 캐시 미스 시 DB에서 대기열 조회 후 Redis에 캐싱
			List<WaitingQueue> queueList = waitingQueueService.getQueueBySeatDetailId(seatDetailId);
			if (queueList.stream().noneMatch(q -> q.getUserId() == userId)) {
				throw new IllegalArgumentException("사용자가 대기열에 존재하지 않습니다.");
			}

			// Redis에 대기열 데이터 추가
			String newQueueKey = "waitingQueue:" + seatDetailId;
			Set<ZSetOperations.TypedTuple<Object>> tuples = queueList.stream()
				.map(q -> new DefaultTypedTuple<Object>(String.valueOf(q.getUserId()), (double) q.getPriority()))
				.collect(Collectors.toSet());

			Long added = redisTemplate.opsForZSet().add(newQueueKey, tuples);

			if (Boolean.TRUE.equals(added)) {
				log.info("'{}' ZSet에 대기열 데이터가 성공적으로 추가되었습니다.", newQueueKey);
			} else {
				log.warn("'{}' ZSet에 대기열 데이터를 추가하는 데 실패했습니다.", newQueueKey);
			}

			// 대기열 위치 계산
			Long newRank = redisTemplate.opsForZSet().rank(newQueueKey, String.valueOf(userId));
			int queuePosition = (newRank != null) ? newRank.intValue() + 1 : 1;
			log.debug("Redis에서 사용자 ID {}의 새로운 대기열 순위: {}", userId, newRank);

			return queuePosition;
		}
	}

	private WaitingQueueDto buildWaitingQueueDto(long userId, long seatDetailId) {
		return WaitingQueueDto.builder()
			.userId(userId)
			.seatDetailId(seatDetailId)
			.waitingStatus(WaitingStatus.WAITING)
			.reservationDt(LocalDateTime.now())
			.priority(System.currentTimeMillis()) // priority를 enqueueTime으로 설정 (예시)
			.build();
	}

	private WaitingQueue buildWaitingQueue(long userId, long seatDetailId) {
		return WaitingQueue.builder()
			.userId(userId)
			.seatDetailId(seatDetailId)
			.waitingStatus(WaitingStatus.WAITING)
			.reservationDt(LocalDateTime.now())
			.priority(System.currentTimeMillis()) // priority를 enqueueTime으로 설정 (예시)
			.build();
	}
}
