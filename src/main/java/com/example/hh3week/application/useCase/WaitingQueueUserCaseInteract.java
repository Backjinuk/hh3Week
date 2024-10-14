package com.example.hh3week.application.useCase;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.hh3week.adapter.in.dto.token.TokenDto;
import com.example.hh3week.adapter.in.dto.waitingQueue.WaitingQueueDto;
import com.example.hh3week.application.port.in.WaitingQueueUserCase;
import com.example.hh3week.application.service.TokenService;
import com.example.hh3week.application.service.WaitingQueueService;
import com.example.hh3week.domain.waitingQueue.entity.WaitingQueue;
import com.example.hh3week.domain.waitingQueue.entity.WaitingStatus;

@Service
public class WaitingQueueUserCaseInteract implements WaitingQueueUserCase {

	private final WaitingQueueService waitingQueueService;
	private final TokenService tokenService;

	@Autowired
	public WaitingQueueUserCaseInteract(WaitingQueueService waitingQueueService, TokenService tokenService) {
		this.waitingQueueService = waitingQueueService;
		this.tokenService = tokenService;
	}

	/**
	 * 사용자 대기열에 추가 후 토큰을 발급하는 메서드입니다.
	 *
	 * @param userId 사용자의 UUID
	 * @param concertScheduleId 콘서트 스케줄의 고유 ID
	 * @param remainingTime 잔여 시간
	 * @return 생성된 토큰 정보
	 */
	@Transactional
	public TokenDto registerUserInQueueAndGenerateToken(long userId, long concertScheduleId, long remainingTime) {
		// 사용자가 이미 대기열에 있는지 확인
		if (waitingQueueService.isUserInQueue(userId, concertScheduleId)) {
			throw new IllegalStateException("사용자가 이미 대기열에 등록되어 있습니다.");
		}

		// 대기열에 사용자 추가
		WaitingQueueDto waitingQueueDto = WaitingQueueDto.builder()
															.userId(userId)
															.concertScheduleId(concertScheduleId)
															.waitingStatus(WaitingStatus.WAITING)
															.priority(1) //임의의 숫자
															.reservationDt(LocalDateTime.now())
															.build();

		waitingQueueService.addWaitingQueue(waitingQueueDto);

		// 대기열 순서 및 잔여 시간 정보를 바탕으로 토큰 생성
		return tokenService.createToken(userId, waitingQueueDto.getPriority(), remainingTime);
	}

	/**
	 * 사용자 토큰의 유효성을 검증하는 메서드입니다.
	 *
	 * @param token JWT 토큰
	 * @return 유효한 토큰인지 여부
	 */
	public Long validateTokenAndGetUserId(String token) {
		return tokenService.validateTokenAndGetUserId(token);
	}
}
