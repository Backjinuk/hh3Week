package com.example.hh3week.adapter.in.controller;

import com.example.hh3week.adapter.in.dto.user.UserDto;
import com.example.hh3week.adapter.in.dto.user.UserPointHistoryDto;
import com.example.hh3week.application.useCase.UserUseCaseInteract;
import com.example.hh3week.domain.user.entity.PointStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserControllerTest {

	private MockMvc mockMvc;

	@Mock
	private UserUseCaseInteract userUseCaseInteractor;

	@InjectMocks
	private UserController userController;

	private ObjectMapper objectMapper;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		// ObjectMapper 설정
		objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
	}

	@Test
	@DisplayName("포인트 충전 성공")
	void handlePointTransaction_Success() throws Exception {
		// Given
		long userId = 1L;
		long depositAmount = 100L;
		UserPointHistoryDto requestDto = UserPointHistoryDto.builder()
			.historyId(0L)
			.userId(userId)
			.pointAmount(depositAmount)
			.pointStatus(PointStatus.EARN)
			.pointDt(LocalDateTime.now())
			.build();

		UserPointHistoryDto responseDto = UserPointHistoryDto.builder()
			.historyId(1L)
			.userId(userId)
			.pointAmount(depositAmount)
			.pointStatus(PointStatus.EARN)
			.pointDt(requestDto.getPointDt())
			.build();

		when(userUseCaseInteractor.handleUserPoint(any(UserPointHistoryDto.class))).thenReturn(responseDto);

		// When & Then
		mockMvc.perform(post("/api/v1/point")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestDto)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.historyId").value(1L))
			.andExpect(jsonPath("$.userId").value(userId))
			.andExpect(jsonPath("$.pointAmount").value(depositAmount))
			.andExpect(jsonPath("$.pointStatus").value("EARN"));

		verify(userUseCaseInteractor, times(1)).handleUserPoint(any(UserPointHistoryDto.class));
	}


	@Test
	@DisplayName("포인트 충전 시 잘못된 입력 (음수 금액)")
	void handlePointTransaction_InvalidAmount() throws Exception {
		// Given
		long userId = 1L;
		long depositAmount = -100L;
		UserPointHistoryDto requestDto = UserPointHistoryDto.builder()
			.historyId(0L)
			.userId(userId)
			.pointAmount(depositAmount)
			.pointStatus(PointStatus.EARN)
			.pointDt(LocalDateTime.now())
			.build();

		when(userUseCaseInteractor.handleUserPoint(any(UserPointHistoryDto.class)))
			.thenThrow(new IllegalArgumentException("포인트 금액은 양수여야 합니다."));

		// When & Then
		mockMvc.perform(post("/api/v1/point")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestDto)))
			.andExpect(status().isBadRequest());

		verify(userUseCaseInteractor, times(1)).handleUserPoint(any(UserPointHistoryDto.class));
	}

	@Test
	@DisplayName("사용자 잔액 조회 성공")
	void getBalance_Success() throws Exception {
		// Given
		long userId = 1L;
		UserDto userDto = UserDto.builder()
			.userId(userId)
			.userName("testUser")
			.pointBalance(400L)
			.build();

		when(userUseCaseInteractor.getUserInfo(userId)).thenReturn(userDto);

		// When & Then
		mockMvc.perform(get("/api/v1/balance/{userId}", userId))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.userId").value(userId))
			.andExpect(jsonPath("$.userName").value("testUser"))
			.andExpect(jsonPath("$.pointBalance").value(400L));

		verify(userUseCaseInteractor, times(1)).getUserInfo(userId);
	}

	@Test
	@DisplayName("사용자 잔액 조회 시 사용자 없음")
	void getBalance_UserNotFound() throws Exception {
		// Given
		long userId = 2L;

		when(userUseCaseInteractor.getUserInfo(userId)).thenThrow(new IllegalArgumentException("사용자를 찾을 수 없습니다."));

		// When & Then
		mockMvc.perform(get("/api/v1/balance/{userId}", userId))
			.andExpect(status().isNotFound());

		verify(userUseCaseInteractor, times(1)).getUserInfo(userId);
	}

	@Test
	@DisplayName("특정 사용자의 포인트 히스토리 조회 성공 (데이터 존재)")
	void getUserPointHistory_Success_WithData() throws Exception {
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

		when(userUseCaseInteractor.getUserPointHistoryListByUserId(userId)).thenReturn(mockHistoryList);

		// When & Then
		mockMvc.perform(get("/api/v1/point/history/{userId}", userId))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.length()").value(2))
			.andExpect(jsonPath("$[0].historyId").value(1L))
			.andExpect(jsonPath("$[0].userId").value(userId))
			.andExpect(jsonPath("$[0].pointAmount").value(100L))
			.andExpect(jsonPath("$[0].pointStatus").value("EARN"))
			.andExpect(jsonPath("$[1].historyId").value(2L))
			.andExpect(jsonPath("$[1].userId").value(userId))
			.andExpect(jsonPath("$[1].pointAmount").value(-50L))
			.andExpect(jsonPath("$[1].pointStatus").value("USE"));

		verify(userUseCaseInteractor, times(1)).getUserPointHistoryListByUserId(userId);
	}

	@Test
	@DisplayName("특정 사용자의 포인트 히스토리 조회 성공 (데이터 없음)")
	void getUserPointHistory_Success_NoData() throws Exception {
		// Given
		long userId = 2L;

		List<UserPointHistoryDto> mockHistoryList = List.of(); // 빈 리스트

		when(userUseCaseInteractor.getUserPointHistoryListByUserId(userId)).thenReturn(mockHistoryList);

		// When & Then
		mockMvc.perform(get("/api/v1/point/history/{userId}", userId))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.length()").value(0));

		verify(userUseCaseInteractor, times(1)).getUserPointHistoryListByUserId(userId);
	}
}
