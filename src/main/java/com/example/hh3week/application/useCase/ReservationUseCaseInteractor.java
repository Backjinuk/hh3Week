package com.example.hh3week.application.useCase;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

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
import com.example.hh3week.common.config.CustomException;
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
	private final AtomicLong tokenIdGenerator = new AtomicLong(1);

	public ReservationUseCaseInteractor(ReservationService reservationService, WaitingQueueService waitingQueueService,
		TokenService tokenService) {
		this.reservationService = reservationService;
		this.waitingQueueService = waitingQueueService;
		this.tokenService = tokenService;
	}

	@Override
	public List<ReservationSeatDto> getAvailableReservationSeatList(long concertScheduleId) {
		return reservationService.getAvailableReservationSeatList(concertScheduleId)
			.stream()
			.peek(reservationSeatDto -> reservationSeatDto.setReservationSeatDetailDtoList(
				reservationService.getAvailableReservationSeatDetailList(reservationSeatDto.getSeatId())))
			.toList();
	}

	/**
	 * 예약 가능한 좌석을 예약하거나, 대기열에 추가하고 토큰을 발급하는 메서드
	 *
	 * @param userId       사용자 ID
	 * @param seatDetailId 좌석 상세 ID
	 * @return 발급된 토큰 정보
	 */
	@Transactional
	public TokenDto reserveSeat(long userId, long seatDetailId) {
		log.info("사용자 {}가 좌석 상세 ID {}를 예약하려고 시도합니다.", userId, seatDetailId);
		try {
			// Step 1: 대기열에 이미 등록된 사용자 확인
			if (waitingQueueService.isUserInQueue(userId, seatDetailId)) {
				log.warn("사용자 {}가 이미 좌석 상세 ID {}에 대한 대기열에 등록되어 있습니다.", userId, seatDetailId);
				 CustomException.illegalArgument("사용자가 이미 대기열에 등록되어 있습니다.", new IllegalArgumentException(),
					this.getClass());
			}

			// Step 2: 비관적 잠금을 사용하여 좌석 상세 정보 조회
			ReservationSeatDetailDto seatDetailDto = reservationService.getSeatDetailByIdForUpdate(seatDetailId);

			// Step 3: 좌석 상태 확인 및 예약 처리
			if (seatDetailDto.getReservationStatus() == ReservationStatus.AVAILABLE) {
				// 좌석이 AVAILABLE인 경우, PENDING으로 상태 변경
				seatDetailDto.setReservationStatus(ReservationStatus.PENDING);
				reservationService.updateSeatDetailStatus(seatDetailDto);
				log.info("좌석 상세 ID {}가 사용자 {}에 의해 PENDING 상태로 변경되었습니다.", seatDetailId, userId);

				// 토큰 발급 (queueOrder=0)
				TokenDto tokenDto = tokenService.createToken(userId, 0, calculateRemainingTime(0), seatDetailId);

				WaitingQueue waitingQueue = WaitingQueue.builder()
					.userId(userId)
					.seatDetailId(seatDetailId)
					.priority(0)
					.waitingStatus(WaitingStatus.WAITING)
					.reservationDt(LocalDateTime.now())
					.build();

				long waitingId = waitingQueueService.addWaitingQueue(WaitingQueueDto.ToDto(waitingQueue));
				log.info("사용자 {}에게 토큰이 발급되었습니다: {}", userId, tokenDto);
				return tokenDto;
			} else {
				// 좌석이 AVAILABLE이 아닌 경우, 대기열에 사용자 추가
				long nextPriority = waitingQueueService.getNextPriority(seatDetailId);
				WaitingQueue waitingQueue = WaitingQueue.builder()
					.userId(userId)
					.seatDetailId(seatDetailId)
					.priority(nextPriority)
					.waitingStatus(WaitingStatus.WAITING)
					.reservationDt(LocalDateTime.now())
					.build();

				long waitingId = waitingQueueService.addWaitingQueue(WaitingQueueDto.ToDto(waitingQueue));
				log.info("사용자 {}가 대기열에 ID {}로 추가되었습니다.", userId, waitingId);

				// 대기열 위치 계산
				int queuePosition = waitingQueueService.getQueuePosition(waitingId);
				log.info("사용자 {}의 대기열 위치는 {}입니다. (좌석 상세 ID {})", userId, queuePosition, seatDetailId);

				// 토큰 발급
				long remainingTime = calculateRemainingTime(queuePosition);
				TokenDto tokenDto = tokenService.createToken(userId, queuePosition, remainingTime, seatDetailId);
				log.info("사용자 {}에게 토큰이 발급되었습니다: {}", userId, tokenDto);
				return tokenDto;
			}
		} catch (Exception e) {
			log.error("사용자 {}의 좌석 예약 중 오류 발생: {}", userId, e.getMessage());
			throw e;
		}
	}

	/**
	 * 대기열에서 남은 시간을 계산하는 메서드 (예시)
	 *
	 * @param queuePosition 대기열에서의 위치
	 * @return 남은 대기 시간 (초 단위)
	 */
	private long calculateRemainingTime(int queuePosition) {
		// 예시: 각 사용자당 10분의 대기 시간 부여
		return queuePosition * 600L;
	}
}
