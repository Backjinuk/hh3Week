// src/test/java/com/example/hh3week/application/useCase/UserUseCaseInteractTest.java

package com.example.hh3week.integration.application.port.in;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import com.example.hh3week.adapter.in.dto.user.UserDto;
import com.example.hh3week.adapter.in.dto.user.UserPointHistoryDto;
import com.example.hh3week.application.port.in.UserUseCase;
import com.example.hh3week.domain.user.entity.PointStatus;

@SpringBootTest
@Transactional
@Sql({"classpath:schema.sql", "classpath:data.sql"})
public class UserUseCaseIntegrationTest {

	@Autowired
	private UserUseCase userUseCase;


	@Test
	@DisplayName("사용자 포인트 충전 - 정상적으로 포인트가 증가하고 히스토리에 기록됨")
	void 사용자포인트충전_정상적으로증가하고히스토리에기록됨() {
		// Given
		long userId = 1L; // 초기 포인트 0
		long depositAmount = 5000L;
		UserPointHistoryDto depositDto = UserPointHistoryDto.builder()
			.userId(userId)
			.pointAmount(depositAmount)
			.pointStatus(PointStatus.EARN)
			.pointDt(LocalDateTime.now())
			.build();

		// When
		UserPointHistoryDto savedHistory = userUseCase.handleUserPoint(depositDto);

		// Then
		assertThat(savedHistory).isNotNull();
		assertThat(savedHistory.getHistoryId()).isGreaterThan(0);
		assertThat(savedHistory.getUserId()).isEqualTo(userId);
		assertThat(savedHistory.getPointAmount()).isEqualTo(5000L);
		assertThat(savedHistory.getPointStatus()).isEqualTo(PointStatus.EARN);

		// 사용자 포인트 조회
		UserDto user = userUseCase.getUserInfo(userId);
		assertThat(user.getPointBalance()).isEqualTo(100000L);

		// 포인트 히스토리 조회
		List<UserPointHistoryDto> histories = userUseCase.getUserPointHistoryListByUserId(userId);
		assertThat(histories).hasSize(6);
		UserPointHistoryDto history = histories.get(histories.size() - 1);
		assertThat(history.getPointAmount()).isEqualTo(5000L);
		assertThat(history.getPointStatus()).isEqualTo(PointStatus.EARN);
	}

	@Test
	@DisplayName("사용자 포인트 사용 - 정상적으로 포인트가 감소하고 히스토리에 기록됨")
	void 사용자포인트사용_정상적으로감소하고히스토리에기록됨() {
		// Given
		long userId = 1L; // 초기 포인트 10000
		long useAmount = 2000L;
		UserPointHistoryDto useDto = UserPointHistoryDto.builder()
			.userId(userId)
			.pointAmount(useAmount)
			.pointDt(LocalDateTime.now())
			.pointStatus(PointStatus.USE)
			.build();

		// When
		UserPointHistoryDto savedHistory = userUseCase.handleUserPoint(useDto);

		// Then
		assertThat(savedHistory).isNotNull();
		assertThat(savedHistory.getHistoryId()).isGreaterThan(0);
		assertThat(savedHistory.getUserId()).isEqualTo(userId);
		assertThat(savedHistory.getPointAmount()).isEqualTo(useAmount);
		assertThat(savedHistory.getPointStatus()).isEqualTo(PointStatus.USE);

		// 사용자 포인트 조회
		UserDto user = userUseCase.getUserInfo(userId);
		assertThat(user.getPointBalance()).isEqualTo(100000L);

		// 포인트 히스토리 조회
		List<UserPointHistoryDto> histories = userUseCase.getUserPointHistoryListByUserId(userId);
		assertThat(histories).hasSize(6); // 기존 2개 + 새로운 1개
		UserPointHistoryDto history = histories.get(histories.size() - 1); // 가장 최근
		assertThat(history.getPointAmount()).isEqualTo(useAmount);
		assertThat(history.getPointStatus()).isEqualTo(PointStatus.USE);
	}

	@Test
	@DisplayName("사용자 포인트 사용 - 포인트 부족 시 예외 발생")
	void 사용자포인트사용_포인트부족시예외발생() {
		// Given
		long userId = 1L; // 초기 포인트 0
		long useAmount = 100000L;
		UserPointHistoryDto useDto = UserPointHistoryDto.builder()
			.userId(userId)
			.pointAmount(useAmount)
			.pointStatus(PointStatus.USE)
			.pointDt(LocalDateTime.now())
			.build();

		// When & Then
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
			userUseCase.handleUserPoint(useDto);
		});

		assertThat(exception.getMessage()).isEqualTo("사용할 포인트가 부족합니다.");

		// 사용자 포인트 조회 (변화 없음)
		UserDto user = userUseCase.getUserInfo(userId);
		assertThat(user.getPointBalance()).isEqualTo(100000L);

		// 포인트 히스토리 조회 (변화 없음)
		List<UserPointHistoryDto> histories = userUseCase.getUserPointHistoryListByUserId(userId);
		assertThat(histories).hasSize(5); // 최초 포인트 사용 시 히스토리 추가되지 않음
	}

	@Test
	@DisplayName("사용자 포인트 처리 - 잘못된 포인트 상태 시 예외 발생")
	void 사용자포인트처리_잘못된포인트상태시예외발생() {
		// Given
		long userId = 1L;
		long amount = 1000L;
		UserPointHistoryDto invalidDto = UserPointHistoryDto.builder()
			.userId(userId)
			.pointAmount(amount)
			.pointStatus(null) // 잘못된 포인트 상태
			.build();

		// When & Then
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
			userUseCase.handleUserPoint(invalidDto);
		});

		assertThat(exception.getMessage()).isEqualTo("포인트 변화 유형은 null일 수 없습니다.");

		// 사용자 포인트 조회 (변화 없음)
		UserDto user = userUseCase.getUserInfo(userId);
		assertThat(user.getPointBalance()).isEqualTo(100000L);

		// 포인트 히스토리 조회 (변화 없음)
		List<UserPointHistoryDto> histories = userUseCase.getUserPointHistoryListByUserId(userId);
		assertThat(histories).hasSize(5); // 기존 1개
	}

	@Test
	@DisplayName("사용자 정보 조회 - 존재하는 사용자 ID일 경우 정보 반환")
	void 사용자정보조회_존재하는사용자ID일경우정보반환() {
		// Given
		long userId = 1L;

		// When
		UserDto user = userUseCase.getUserInfo(userId);

		// Then
		assertThat(user).isNotNull();
		assertThat(user.getUserId()).isEqualTo(userId);
		assertThat(user.getPointBalance()).isEqualTo(100000L);
	}

	@Test
	@DisplayName("사용자 포인트 히스토리 조회 - 존재하는 사용자 ID일 경우 히스토리 목록 반환")
	void 사용자포인트히스토리조회_존재하는사용자ID일경우히스토리목록반환() {
		// Given
		long userId = 1L;

		// When
		List<UserPointHistoryDto> histories = userUseCase.getUserPointHistoryListByUserId(userId);

		// Then
		assertThat(histories).hasSize(5); // EARN, USE
		assertThat(histories.get(0).getPointStatus()).isEqualTo(PointStatus.EARN);
		assertThat(histories.get(1).getPointStatus()).isEqualTo(PointStatus.USE);
	}

	@Test
	@DisplayName("사용자 정보 조회 - 존재하지 않는 사용자 ID일 경우 예외 발생")
	void 사용자정보조회_존재하지않는사용자ID일경우예외발생() {
		// Given
		long nonExistentUserId = 999L;

		// When & Then
		NullPointerException exception = assertThrows(NullPointerException.class, () -> {
			userUseCase.getUserInfo(nonExistentUserId);
		});

		assertThat(exception.getMessage()).isEqualTo("사용자를 찾을 수 없습니다." );
	}

	@Test
	@DisplayName("사용자 포인트 히스토리 조회 - 존재하지 않는 사용자 ID일 경우 빈 리스트 반환")
	void 사용자포인트히스토리조회_존재하지않는사용자ID일경우빈리스트반환() {
		// Given
		long nonExistentUserId = 999L;

		// When
		List<UserPointHistoryDto> histories = userUseCase.getUserPointHistoryListByUserId(nonExistentUserId);

		// Then
		assertThat(histories).isEmpty();
	}
}
