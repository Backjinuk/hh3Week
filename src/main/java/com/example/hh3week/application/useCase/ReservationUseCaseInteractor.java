package com.example.hh3week.application.useCase;

import java.time.LocalDateTime;
import java.util.List;

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
import com.example.hh3week.domain.waitingQueue.entity.WaitingStatus;

@Service
public class ReservationUseCaseInteractor implements ReservationUseCase {

	private final ReservationService reservationService;
	private final WaitingQueueService waitingQueueService; // 대기열 서비스 추가
	private final TokenService tokenService; // 토큰 서비스 추가

	public ReservationUseCaseInteractor(ReservationService reservationService, WaitingQueueService waitingQueueService,
		TokenService tokenService) {
		this.reservationService = reservationService;
		this.waitingQueueService = waitingQueueService;
		this.tokenService = tokenService;
	}

	/*
	 * 예약 가능 좌석 조회 및 예약
	 * 1. 예약 가능한 좌석 list를 return 해줌
	 * 2. 사용자가 선택한 좌석을 보내면 그 좌석에 대기열을 걸어서 대기열 발급, 토큰값 내려줌
	 */

	/**
	 * 예약 가능한 좌석 목록을 반환하는 메서드
	 *
	 * @param concertScheduleId 콘서트 스케줄 ID
	 * @return 예약 가능한 좌석 목록
	 */
	@Override
	public List<ReservationSeatDto> getAvailableReservationSeatList(long concertScheduleId) {
		return reservationService.getAvailableReservationSeatList(concertScheduleId)
			.stream()
			.peek(reservationSeatDto -> reservationSeatDto.setReservationSeatDetailDtoList(
				reservationService.getAvailableReservationSeatDetailList(reservationSeatDto.getSeatId())))
			.toList();
	}

	/**
	 * 사용자가 좌석 예약을 요청할 때, 대기열을 생성하고 토큰을 발급하는 메서드
	 *
	 * @param userId 사용자 ID
	 * @param seatDetailId 좌석 ID
	 * @return 대기열 및 토큰 정보
	 */
	@Override
	@Transactional
	public TokenDto reserveSeat(long userId, long seatDetailId) {


		// 1. 대기열에 등록되지 않은 사용자만 진행
		if (waitingQueueService.isUserInQueue(userId, seatDetailId)) {
			throw new IllegalStateException("사용자가 이미 대기열에 등록되어 있습니다.");
		}

		// 2. 대기열에 사용자 추가
		WaitingQueueDto waitingQueueDto = WaitingQueueDto.builder()
			.userId(userId)
			.waitingStatus(WaitingStatus.WAITING)
			.seatDetailId(seatDetailId)
			.priority(1) // 우선순위 설정 (예시 값)
			.reservationDt(LocalDateTime.now())
			.build();

		long waitingId = waitingQueueService.addWaitingQueue(waitingQueueDto);

		// 3. 대기열 위치 계산
		int queuePosition = waitingQueueService.getQueuePosition(waitingId);

		// 4. 토큰 발급
		long remainingTime = calculateRemainingTime(queuePosition); // 남은 시간 계산 로직 (예시)
		TokenDto tokenDto = tokenService.createToken(userId, queuePosition, remainingTime);

		// 5. 좌석의 상태를 예약 상태로 변경
		ReservationSeatDetailDto seatDetailDto = reservationService.getSeatDetailById(seatDetailId);

		seatDetailDto.setReservationStatus(ReservationStatus.PENDING);
		reservationService.updateSeatDetailStatus(seatDetailDto);

		// 현재 남은 좌석 갯수 update
		ReservationSeatDto seatDto = reservationService.getSeatById(seatDetailDto.getSeatId());
		reservationService.updateSeatReservation(seatDto);

		// 5. 대기열 정보 및 토큰을 반환
		return tokenDto;
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
