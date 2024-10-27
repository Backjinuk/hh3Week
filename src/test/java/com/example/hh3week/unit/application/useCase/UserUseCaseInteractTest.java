package com.example.hh3week.unit.application.useCase;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.hh3week.adapter.in.dto.user.UserDto;
import com.example.hh3week.adapter.in.dto.user.UserPointHistoryDto;
import com.example.hh3week.application.service.UserService;
import com.example.hh3week.application.useCase.UserUseCaseInteract;
import com.example.hh3week.domain.user.entity.PointStatus;
import com.example.hh3week.domain.user.entity.UserPointHistory;

class UserUseCaseInteractTest {
	@Mock
	private UserService userService;

	@InjectMocks
	private UserUseCaseInteract userUseCaseInteract;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}


	@Test
	@DisplayName("사용자 포인트 충전 성공")
	void handleUserPointTransaction_Success_Deposit() {
		// Given
		long userId = 1L;
		long depositAmount = 100L;
		UserPointHistoryDto userPointHistoryDto = UserPointHistoryDto.builder()
			.historyId(0L)
			.userId(userId)
			.pointAmount(depositAmount)
			.pointStatus(PointStatus.EARN)
			.pointDt(LocalDateTime.now())
			.build();

		UserDto userDto = UserDto.builder()
			.userId(userId)
			.userName("testUser")
			.pointBalance(500L)
			.build();

		UserPointHistory savedHistory = UserPointHistory.builder()
			.historyId(1L)
			.userId(userId)
			.pointAmount(depositAmount)
			.pointStatus(PointStatus.EARN)
			.pointDt(userPointHistoryDto.getPointDt())
			.build();

		when(userService.getUserInfo(userId)).thenReturn(userDto);
		when(userService.addUserPointHistoryInUser(any(UserPointHistoryDto.class))).thenReturn(UserPointHistoryDto.toDto(savedHistory));

		// When
		UserPointHistoryDto result = userUseCaseInteract.handleUserPoint(userPointHistoryDto);

		// Then
		assertThat(result).isNotNull();
		assertThat(result.getHistoryId()).isEqualTo(1L);
		assertThat(result.getUserId()).isEqualTo(userId);
		assertThat(result.getPointAmount()).isEqualTo(depositAmount);
		assertThat(result.getPointStatus()).isEqualTo(PointStatus.EARN);
		assertThat(result.getPointDt()).isEqualTo(savedHistory.getPointDt());

		// Verify interactions
		verify(userService, times(1)).getUserInfo(userId);
		verify(userService, times(1)).depositBalance(userDto, depositAmount);
		verify(userService, times(1)).addUserPointHistoryInUser(userPointHistoryDto);
	}



	@Test
	@DisplayName("사용자 포인트 사용 성공")
	void handleUserPointTransaction_Success_Use() {
		// Given
		long userId = 1L;
		long useAmount = 100L;
		UserPointHistoryDto userPointHistoryDto = UserPointHistoryDto.builder()
			.historyId(0L)
			.userId(userId)
			.pointAmount(useAmount)
			.pointStatus(PointStatus.USE)
			.pointDt(LocalDateTime.now())
			.build();

		UserDto userDto = UserDto.builder()
			.userId(userId)
			.userName("testUser")
			.pointBalance(500L)
			.build();

		UserPointHistory savedHistory = UserPointHistory.builder()
			.historyId(2L)
			.userId(userId)
			.pointAmount(useAmount)
			.pointStatus(PointStatus.USE)
			.pointDt(userPointHistoryDto.getPointDt())
			.build();

		when(userService.getUserInfo(userId)).thenReturn(userDto);
		when(userService.addUserPointHistoryInUser(any(UserPointHistoryDto.class))).thenReturn(UserPointHistoryDto.toDto(savedHistory));

		// When
		UserPointHistoryDto result = userUseCaseInteract.handleUserPoint(userPointHistoryDto);

		// Then
		assertThat(result).isNotNull();
		assertThat(result.getHistoryId()).isEqualTo(2L);
		assertThat(result.getUserId()).isEqualTo(userId);
		assertThat(result.getPointAmount()).isEqualTo(useAmount);
		assertThat(result.getPointStatus()).isEqualTo(PointStatus.USE);
		assertThat(result.getPointDt()).isEqualTo(savedHistory.getPointDt());

		// Verify interactions
		verify(userService, times(1)).getUserInfo(userId);
		verify(userService, times(1)).useBalance(userDto, useAmount);
		verify(userService, times(1)).addUserPointHistoryInUser(userPointHistoryDto);
	}



	@Test
	@DisplayName("사용자 포인트 히스토리 DTO가 null일 때 예외 발생")
	void handleUserPointTransaction_NullDto_ThrowsException() {
		// Given
		UserPointHistoryDto userPointHistoryDto = null;

		// When & Then
		assertThatThrownBy(() -> userUseCaseInteract.handleUserPoint(userPointHistoryDto))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("포인트 히스토리 DTO가 null일 수 없습니다.");

		// Verify no interactions
		verify(userService, never()).getUserInfo(anyLong());
		verify(userService, never()).depositBalance(any(UserDto.class), anyLong());
		verify(userService, never()).useBalance(any(UserDto.class), anyLong());
		verify(userService, never()).addUserPointHistoryInUser(any(UserPointHistoryDto.class));
	}

	@Test
	@DisplayName("포인트 금액이 음수일 때 예외 발생")
	void handleUserPointTransaction_NegativeAmount_ThrowsException() {
		// Given
		long userId = 1L;
		long useAmount = -100L;
		UserPointHistoryDto userPointHistoryDto = UserPointHistoryDto.builder()
			.historyId(0L)
			.userId(userId)
			.pointAmount(useAmount)
			.pointStatus(PointStatus.USE)
			.pointDt(LocalDateTime.now())
			.build();

		// When & Then
		assertThatThrownBy(() -> userUseCaseInteract.handleUserPoint(userPointHistoryDto))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("포인트 변화 금액은 음수일 수 없습니다.");

		// Verify no interactions
		verify(userService, never()).getUserInfo(anyLong());
		verify(userService, never()).depositBalance(any(UserDto.class), anyLong());
		verify(userService, never()).useBalance(any(UserDto.class), anyLong());
		verify(userService, never()).addUserPointHistoryInUser(any(UserPointHistoryDto.class));
	}


	@Test
	@DisplayName("사용 금액이 포인트보다 많을 때 예외 발생")
	void handleUserPointTransaction_UseAmountExceedsBalance_ThrowsException() {
		// Given
		long userId = 1L;
		long useAmount = 600L; // 현재 잔액 500L보다 많음
		UserPointHistoryDto userPointHistoryDto = UserPointHistoryDto.builder()
			.historyId(0L)
			.userId(userId)
			.pointAmount(useAmount)
			.pointStatus(PointStatus.USE)
			.pointDt(LocalDateTime.now())
			.build();

		UserDto userDto = UserDto.builder()
			.userId(userId)
			.userName("testUser")
			.pointBalance(500L)
			.build();

		when(userService.getUserInfo(userId)).thenReturn(userDto);

		// When & Then
		assertThatThrownBy(() -> userUseCaseInteract.handleUserPoint(userPointHistoryDto))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("사용 금액이 포인트보다 많습니다.");

		// Verify interactions
		verify(userService, times(1)).getUserInfo(userId);
		verify(userService, never()).useBalance(any(UserDto.class), anyLong());
		verify(userService, never()).addUserPointHistoryInUser(any(UserPointHistoryDto.class));
	}


	@Test
	@DisplayName("특정 사용자의 포인트 히스토리 조회 성공 (데이터 존재)")
	void getUserPointHistoryListByUserId_Success_WithData() {
		// Given
		long userId = 1L;

		UserPointHistoryDto historyDto1 = UserPointHistoryDto.builder()
			.historyId(1L)
			.userId(userId)
			.pointAmount(100L)
			.pointStatus(PointStatus.EARN)
			.pointDt(LocalDateTime.now().minusDays(2))
			.build();

		UserPointHistoryDto historyDto2 = UserPointHistoryDto.builder()
			.historyId(2L)
			.userId(userId)
			.pointAmount(-50L)
			.pointStatus(PointStatus.USE)
			.pointDt(LocalDateTime.now().minusDays(1))
			.build();

		List<UserPointHistoryDto> mockHistoryList = List.of(historyDto1, historyDto2);

		when(userService.getUserPointHistoryFindByUserId(userId)).thenReturn(mockHistoryList);

		// When
		List<UserPointHistoryDto> result = userUseCaseInteract.getUserPointHistoryListByUserId(userId);

		// Then
		assertThat(result).isNotNull();
		assertThat(result).hasSize(2);
		assertThat(result).containsExactly(historyDto1, historyDto2);

		verify(userService, times(1)).getUserPointHistoryFindByUserId(userId);
	}

	@Test
	@DisplayName("특정 사용자의 포인트 히스토리 조회 성공 (데이터 없음)")
	void getUserPointHistoryListByUserId_Success_NoData() {
		// Given
		long userId = 2L;

		List<UserPointHistoryDto> mockHistoryList = List.of(); // 빈 리스트

		when(userService.getUserPointHistoryFindByUserId(userId)).thenReturn(mockHistoryList);

		// When
		List<UserPointHistoryDto> result = userUseCaseInteract.getUserPointHistoryListByUserId(userId);

		// Then
		assertThat(result).isNotNull();
		assertThat(result).isEmpty();

		verify(userService, times(1)).getUserPointHistoryFindByUserId(userId);
	}

	@Test
	@DisplayName("특정 사용자의 포인트 히스토리 조회 시 Repository가 null을 반환할 때 빈 리스트 반환")
	void getUserPointHistoryListByUserId_NullFromRepository_ReturnsEmptyList() {
		// Given
		long userId = 3L;

		when(userService.getUserPointHistoryFindByUserId(userId)).thenReturn(List.of());

		// When
		List<UserPointHistoryDto> result = userUseCaseInteract.getUserPointHistoryListByUserId(userId);

		// Then
		assertThat(result).isNotNull();
		assertThat(result).isEmpty();

		verify(userService, times(1)).getUserPointHistoryFindByUserId(userId);
	}


}

