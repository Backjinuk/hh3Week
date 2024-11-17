package com.example.hh3week.application.useCase;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.hh3week.adapter.in.dto.reservation.ReservationSeatDetailDto;
import com.example.hh3week.adapter.in.dto.reservation.ReservationSeatDto;
import com.example.hh3week.adapter.in.dto.token.TokenDto;
import com.example.hh3week.adapter.in.dto.waitingQueue.WaitingQueueDto;
import com.example.hh3week.adapter.out.messaging.kafka.dto.ReleaseSeat;
import com.example.hh3week.application.port.in.ReservationUseCase;
import com.example.hh3week.application.service.ReservationService;
import com.example.hh3week.application.service.TokenService;
import com.example.hh3week.application.service.WaitingQueueService;
import com.example.hh3week.domain.reservation.entity.ReservationSeatDetail;
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

		if (waitingQueueService.isUserInQueue(userId, seatDetailId)) {
			throw new IllegalArgumentException("사용자가 이미 대기열에 등록되어 있습니다.");
		}

		ReservationSeatDetailDto seatDetail = reservationService.getSeatDetailById(seatDetailId);

		if (seatDetail.getReservationStatus() == ReservationStatus.AVAILABLE) {
			seatDetail.setReservationStatus(ReservationStatus.PENDING);
			reservationService.updateSeatDetailStatus(seatDetail);
		}

		return issuedTokens(userId, seatDetailId);
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


	private TokenDto issuedTokens(long userId, long seatDetailId){

		// 대기열에 추가
		WaitingQueueDto waitingQueueDto = waitingQueueService.addWaitingQueue(buildWaitingQueueDto(userId, seatDetailId));

		// 대기열 위치 계산
		int queuePosition = waitingQueueService.getQueuePosition(waitingQueueDto);

		// 토큰 발급
		long remainingTime = calculateRemainingTime(queuePosition);

		return tokenService.createToken(userId, queuePosition, remainingTime, seatDetailId);
	}



	@KafkaListener(topics = "${kafka.topics.release-seat}", groupId = "saga-group")
	@Retryable(value = {Exception.class}, maxAttempts = 3, backoff = @Backoff(delay = 5000))
	public void handleReleaseSeat(ReleaseSeat event) throws Exception {

		ReservationSeatDetailDto reservationSeatDetail = ReservationSeatDetailDto.builder()
			.seatDetailId(event.getSeatDetailId())
			.reservationStatus(ReservationStatus.AVAILABLE)
			.build();

		// 좌석 상태를 AVAILABLE로 되돌림
		reservationService.updateSeatDetailStatus(reservationSeatDetail);

		WaitingQueueDto waitingQueueDto = WaitingQueueDto.builder()
			.userId(event.getUserId())
			.seatDetailId(event.getSeatDetailId())
			.build();

		// 대기열에서 사용자 제거
		waitingQueueService.deleteWaitingQueueFromUser(waitingQueueDto);

		// 로그 기록
		log.info("좌석 상태 원복 완료: UserId={}, SeatDetailId={}", event.getUserId(), event.getSeatDetailId());
	}



	@Recover
	public void recover(ReleaseSeat event, Exception e) {
		// 보상 트랜잭션 최종 실패 시 알림 또는 추가 조치
		log.error("좌석 상태 원복 최종 실패: UserId={}, SeatDetailId={}, Reason={}", event.getUserId(), event.getSeatDetailId(),
			e.getMessage());
		// 필요 시, 관리자에게 알림을 보내거나 로그를 기록할 수 있습니다.
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
