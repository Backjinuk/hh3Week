package com.example.hh3week.application.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.hh3week.adapter.in.dto.user.UserDto;
import com.example.hh3week.adapter.in.dto.user.UserPointHistoryDto;
import com.example.hh3week.application.port.out.UserRepositoryPort;
import com.example.hh3week.domain.user.entity.PointStatus;
import com.example.hh3week.domain.user.entity.User;
import com.example.hh3week.domain.user.entity.UserPointHistory;

class UserServiceTest {

	@Mock
	private UserRepositoryPort userRepositoryPort;

	@InjectMocks
	private UserService userService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	/*
	 *
	 *  잔액충전 / 잔액조회 api 구현
	 *
	 *
	 *  춘전 api
	 *  List 조회 api
	 *  user조회 api
	 *
	 * */

	@Test
	@DisplayName("잔액 충전 성공")
	void depositBalanceSuccess() {
		// Given
		long userId = 1L;
		long depositAmount = 100L;
		UserDto userDto = UserDto.builder()
			.userId(userId)
			.userName("testUser")
			.pointBalance(500L)
			.build();

		User user = User.toEntity(userDto);

		// 모킹 설정
		doNothing().when(userRepositoryPort).updateDepositBalance(any(User.class));

		// When
		userService.depositBalance(userDto, depositAmount);

		// Then
		assertThat(user.getPointBalance()).isEqualTo(500L);
	}


	@Test
	@DisplayName("잔액 충전 시 UserDto가 null일 때 예외 발생")
	void depositBalanceWithNullUserDto() {
		// Given
		UserDto userDto = null;
		long depositAmount = 100L;

		// When & Then
		assertThatThrownBy(() -> userService.depositBalance(userDto, depositAmount))
			.isInstanceOf(NullPointerException.class)
			.hasMessageContaining("UserDto는 null일 수 없습니다.");

		// verify that updateDepositBalance is never called
		verify(userRepositoryPort, never()).updateDepositBalance(any(User.class));
	}


	@Test
	@DisplayName("잔액 충전 시 음수 금액을 충전할 때 예외 발생")
	void depositBalanceWithNegativeAmount() {
		// Given
		long userId = 1L;
		long depositAmount = -100L;
		UserDto userDto = UserDto.builder()
			.userId(userId)
			.userName("testUser")
			.pointBalance(500L)
			.build();

		// When & Then
		assertThatThrownBy(() -> userService.depositBalance(userDto, depositAmount))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("충전 금액은 음수일 수 없습니다.");

		verify(userRepositoryPort, never()).updateDepositBalance(any(User.class));
	}


	@Test
	@DisplayName("잔액 사용 성공")
	void useBalanceSuccess() {
		// Given
		long userId = 1L;
		long useAmount = 100L;
		UserDto userDto = UserDto.builder()
			.userId(userId)
			.userName("testUser")
			.pointBalance(500L)
			.build();

		User user = User.toEntity(userDto);

		// Mocking: updateDepositBalance는 아무 동작도 하지 않음
		doNothing().when(userRepositoryPort).updateDepositBalance(any(User.class));

		// When
		userService.useBalance(userDto, useAmount);

		// Then
		// ArgumentCaptor를 사용하여 updateDepositBalance에 전달된 User 객체를 캡처
		ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
		verify(userRepositoryPort, times(1)).updateDepositBalance(userCaptor.capture());
		User updatedUser = userCaptor.getValue();

		// 포인트 잔액이 올바르게 업데이트되었는지 검증
		assertThat(updatedUser.getPointBalance()).isEqualTo(400L); // 500L - 100L = 400L
		assertThat(updatedUser.getUserId()).isEqualTo(userId);
		assertThat(updatedUser.getUserName()).isEqualTo("testUser");
	}


	@Test
	@DisplayName("잔액 사용 시 음수 금액을 사용할 때 예외 발생")
	void useBalanceWithNegativeAmount() {
		// Given
		long userId = 1L;
		long useAmount = -100L;
		UserDto userDto = UserDto.builder()
			.userId(userId)
			.userName("testUser")
			.pointBalance(500L)
			.build();

		// When & Then
		assertThatThrownBy(() -> userService.useBalance(userDto, useAmount))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("충전 금액은 음수일 수 없습니다.");

		verify(userRepositoryPort, never()).updateDepositBalance(any(User.class));
	}




	@Test
	@DisplayName("잔액 사용 시 UserDto가 null일 때 예외 발생")
	void useBalanceWithNullUserDto() {
		// Given
		UserDto userDto = null;
		long useAmount = 100L;

		// When & Then
		assertThatThrownBy(() -> userService.useBalance(userDto, useAmount))
			.isInstanceOf(NullPointerException.class)
			.hasMessage("UserDto는 null일 수 없습니다.");

		verify(userRepositoryPort, never()).updateDepositBalance(any(User.class));
	}





	@Test
	@DisplayName("사용자 정보 조회 성공")
	void getUserInfoSuccess() {
		// Given
		long userId = 1L;
		UserDto userDto = UserDto.builder()
			.userId(userId)
			.userName("testUser")
			.pointBalance(500L)
			.build();

		when(userRepositoryPort.getUserInfo(userId)).thenReturn(userDto);

		// When
		UserDto result = userService.getUserInfo(userId);

		// Then
		assertThat(result).isNotNull();
		assertThat(result.getUserId()).isEqualTo(userId);
		assertThat(result.getUserName()).isEqualTo("testUser");
		assertThat(result.getPointBalance()).isEqualTo(500L);
		verify(userRepositoryPort, times(1)).getUserInfo(userId);
	}


	@Test
	@DisplayName("사용자 정보 조회 시 사용자 미존재 오류 발생")
	void getUserInfoUserNotFound() {
		// Given
		long userId = 1L;
		when(userRepositoryPort.getUserInfo(userId)).thenReturn(null);

		// When & Then
		assertThatThrownBy(() -> userService.getUserInfo(userId))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("사용자를 찾을수 없습니다.");

		verify(userRepositoryPort, times(1)).getUserInfo(userId);
	}

	@Test
	@DisplayName("사용자 포인트 히스토리 추가 성공")
	void addUserPointHistoryInUserSuccess() {
		// Given
		long userId = 1L;
		UserPointHistoryDto userPointHistoryDto = UserPointHistoryDto.builder()
			.historyId(0)
			.userId(userId)
			.pointAmount(100L)
			.pointStatus(PointStatus.EARN)
			.pointDt(LocalDateTime.now())
			.build();

		UserPointHistory savedUserPointHistory = UserPointHistory.builder()
			.historyId(1L)
			.userId(userId)
			.pointAmount(100L)
			.pointStatus(PointStatus.EARN)
			.pointDt(userPointHistoryDto.getPointDt())
			.build();

		when(userRepositoryPort.addUserPointHistoryInUser(any(UserPointHistory.class))).thenReturn(savedUserPointHistory);

		// When
		UserPointHistoryDto result = userService.addUserPointHistoryInUser(userPointHistoryDto);

		// Then
		assertThat(result).isNotNull();
		assertThat(result.getHistoryId()).isEqualTo(1L);
		assertThat(result.getUserId()).isEqualTo(userId);
		assertThat(result.getPointAmount()).isEqualTo(100L);
		assertThat(result.getPointStatus()).isEqualTo(PointStatus.EARN); // Enum 비교
		assertThat(result.getPointDt()).isEqualTo(savedUserPointHistory.getPointDt());

		ArgumentCaptor<UserPointHistory> pointHistoryCaptor = ArgumentCaptor.forClass(UserPointHistory.class);
		verify(userRepositoryPort, times(1)).addUserPointHistoryInUser(pointHistoryCaptor.capture());

		UserPointHistory capturedPointHistory = pointHistoryCaptor.getValue();
		assertThat(capturedPointHistory.getUserId()).isEqualTo(userId);
		assertThat(capturedPointHistory.getPointAmount()).isEqualTo(100L);
		assertThat(capturedPointHistory.getPointStatus()).isEqualTo(PointStatus.EARN);
	}

	@Test
	@DisplayName("사용자 포인트 히스토리 추가 시 UserPointHistoryDto가 null일 때 예외 발생")
	void addUserPointHistoryInUserWithNullDto() {
		// Given
		UserPointHistoryDto userPointHistoryDto = null;

		// When & Then
		assertThatThrownBy(() -> userService.addUserPointHistoryInUser(userPointHistoryDto))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("UserPointHistoryDto는 null일 수 없습니다.");

		verify(userRepositoryPort, never()).addUserPointHistoryInUser(any(UserPointHistory.class));
	}
	@Test
	@DisplayName("사용자 포인트 히스토리 조회 시 히스토리가 없을 경우 빈 리스트 반환")
	void getUserPointHistoryFindByUserIdEmpty() {
		// Given
		long userId = 1L;
		List<UserPointHistory> userPointHistories = List.of(); // 빈 리스트

		when(userRepositoryPort.getUserPointHistoryFindByUserId(userId)).thenReturn(userPointHistories);

		// When
		List<UserPointHistoryDto> result = userService.getUserPointHistoryFindByUserId(userId);

		// Then
		assertThat(result).isNotNull();
		assertThat(result).isEmpty();

		verify(userRepositoryPort, times(1)).getUserPointHistoryFindByUserId(userId);
	}


}



