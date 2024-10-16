package com.example.hh3week.application.useCase;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.hh3week.adapter.in.dto.token.TokenDto;
import com.example.hh3week.adapter.in.dto.waitingQueue.WaitingQueueDto;
import com.example.hh3week.application.service.TokenService;
import com.example.hh3week.application.service.WaitingQueueService;
import com.example.hh3week.domain.waitingQueue.entity.WaitingStatus;

class WaitingQueueUserCaseInteractTest {
	@Mock
	private WaitingQueueService waitingQueueService;

	@Mock
	private TokenService tokenService;

	@InjectMocks
	private WaitingQueueUserCaseInteract waitingQueueUserCaseInteract;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	@DisplayName("대기열에 성공적으로 유저 등록 후 토큰 발급")
	public void registerUserInQueueAndGenerateToken_Success() {
		// given
		long userId = 1L;
		long concertScheduleId = 100L;
		long remainingTime = 3600L; // 1시간

		// 대기열에 등록되지 않았다고 가정
		when(waitingQueueService.isUserInQueue(userId, concertScheduleId)).thenReturn(false);

		// 대기열에 추가할 데이터
		WaitingQueueDto waitingQueueDto = WaitingQueueDto.builder()
			.userId(userId)
			.concertScheduleId(concertScheduleId)
			.waitingStatus(WaitingStatus.WAITING)
			.priority(1)
			.reservationDt(LocalDateTime.now())
			.build();

		// TokenDto 반환 모킹
		TokenDto tokenDto = TokenDto.builder()
			.tokenId(1L)
			.userId(userId)
			.token("mockedToken")
			.issuedAt(LocalDateTime.now())
			.expiresAt(LocalDateTime.now().plusSeconds(remainingTime))
			.build();

		// when
		when(waitingQueueService.addWaitingQueue(waitingQueueDto)).thenReturn(1L);
		when(tokenService.createToken(userId, waitingQueueDto.getPriority(), remainingTime)).thenReturn(tokenDto);

		TokenDto resultTokenDto = waitingQueueUserCaseInteract.registerUserInQueueAndGenerateToken(userId, concertScheduleId, remainingTime);

		// then
		assertThat(resultTokenDto).isNotNull();
		assertThat(resultTokenDto.getUserId()).isEqualTo(userId);
		assertThat(resultTokenDto.getToken()).isEqualTo("mockedToken");

		// verify
		verify(waitingQueueService, times(1)).isUserInQueue(userId, concertScheduleId);
		verify(waitingQueueService, times(1)).addWaitingQueue(any(WaitingQueueDto.class));
		verify(tokenService, times(1)).createToken(userId, waitingQueueDto.getPriority(), remainingTime);
	}

	@Test
	@DisplayName("이미 대기열에 있는 경우 예외 발생")
	public void registerUserInQueueAndGenerateToken_UserAlreadyInQueue() {
		// given
		long userId = 1L;
		long concertScheduleId = 100L;
		long remainingTime = 3600L;

		// 대기열에 이미 등록되어 있다고 가정
		when(waitingQueueService.isUserInQueue(userId, concertScheduleId)).thenReturn(true);

		// when & then
		assertThatThrownBy(() -> waitingQueueUserCaseInteract.registerUserInQueueAndGenerateToken(userId, concertScheduleId, remainingTime))
			.isInstanceOf(IllegalStateException.class)
			.hasMessage("사용자가 이미 대기열에 등록되어 있습니다.");

		// verify
		verify(waitingQueueService, times(1)).isUserInQueue(userId, concertScheduleId);
		verify(waitingQueueService, never()).addWaitingQueue(any(WaitingQueueDto.class));
		verify(tokenService, never()).createToken(anyLong(), anyInt(), anyLong());
	}

	@Test
	@DisplayName("토큰 검증 성공")
	public void validateTokenAndGetUserId_Success() {
		// given
		String token = "mockedToken";
		long userId = 1L;

		// when
		when(tokenService.validateTokenAndGetUserId(token)).thenReturn(userId);

		Long resultUserId = waitingQueueUserCaseInteract.validateTokenAndGetUserId(token);

		// then
		assertThat(resultUserId).isEqualTo(userId);

		// verify
		verify(tokenService, times(1)).validateTokenAndGetUserId(token);
	}

	@Test
	@DisplayName("토큰 검증 실패 (잘못된 토큰)")
	public void validateTokenAndGetUserId_Failure() {
		// given
		String invalidToken = "invalidToken";

		// when
		when(tokenService.validateTokenAndGetUserId(invalidToken)).thenReturn(null);

		Long resultUserId = waitingQueueUserCaseInteract.validateTokenAndGetUserId(invalidToken);

		// then
		assertThat(resultUserId).isNull();

		// verify
		verify(tokenService, times(1)).validateTokenAndGetUserId(invalidToken);
	}


}
