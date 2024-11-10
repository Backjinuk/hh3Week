package com.example.hh3week.application.useCase;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
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
	@Transactional
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

		// Step 1: 대기열에 이미 등록된 사용자 확인
		boolean userInQueue = waitingQueueService.isUserInQueue(userId, seatDetailId);

		if (userInQueue) {
			throw new IllegalArgumentException("사용자가 이미 대기열에 등록되어 있습니다.");
		}

		// Step 2: 좌석 상세 정보 조회
		ReservationSeatDetailDto seatDetail = reservationService.getSeatDetailById(seatDetailId);

		log.info("seatDetail 의 정보 {}",seatDetail.getReservationStatus());

		// Step 3: 좌석 상태 확인 및 예약 처리
		if (seatDetail.getReservationStatus() == ReservationStatus.AVAILABLE) {
			seatDetail.setReservationStatus(ReservationStatus.PENDING);
			reservationService.updateSeatDetailStatus(seatDetail);

			return tokenService.createToken(userId, 0, calculateRemainingTime(0), seatDetailId);
		} else {

			// 좌석이 AVAILABLE이 아닌 경우, 대기열에 사용자 추가
			WaitingQueueDto waitingQueueDto = waitingQueueService.addWaitingQueue( buildWaitingQueueDto(userId, seatDetailId));

			// 대기열 위치 계산
			int queuePosition = waitingQueueService.getQueuePosition(seatDetailId, waitingQueueDto.getWaitingId());

			// 토큰 발급
			long remainingTime = calculateRemainingTime(queuePosition);

			return tokenService.createToken(userId, queuePosition, remainingTime, seatDetailId);
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

	private int getQueuePosition2(String queueKey, long seatDetailId, long userId) {
		Long rank = redisTemplate.opsForZSet().rank(queueKey, String.valueOf(userId));

			return Objects.requireNonNull(rank).intValue() + 1; // 1-based index

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
