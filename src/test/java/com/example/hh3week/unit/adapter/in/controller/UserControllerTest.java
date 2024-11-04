package com.example.hh3week.unit.adapter.in.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DisplayName;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.example.hh3week.adapter.in.controller.UserController;
import com.example.hh3week.adapter.in.dto.user.UserDto;
import com.example.hh3week.adapter.in.dto.user.UserPointHistoryDto;
import com.example.hh3week.application.port.in.UserUseCase;
import com.example.hh3week.domain.user.entity.PointStatus;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(UserController.class)
public class UserControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private UserUseCase userUseCase;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	@DisplayName("사용자 포인트 충전 또는 사용 성공")
	void 사용자_포인트_충전_또는_사용_성공() throws Exception {
		// Given
		UserPointHistoryDto requestDto = UserPointHistoryDto.builder()
			.userId(1L)
			.pointAmount(1000L)
			.pointStatus(PointStatus.EARN)
			.build();

		UserPointHistoryDto responseDto = UserPointHistoryDto.builder()
			.userId(1L)
			.pointAmount(1000L)
			.pointStatus(PointStatus.EARN)
			.build();

		when(userUseCase.handleUserPoint(requestDto)).thenReturn(responseDto);

		// When & Then
		mockMvc.perform(post("/api/v1/users/handleUserPoint")
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestDto)))
			.andExpect(status().isOk());

	}

	@Test
	@DisplayName("사용자 포인트 충전 또는 사용 실패 - 유효하지 않은 DTO")
	void 사용자_포인트_충전_또는_사용_실패_유효하지_않은_DTO() throws Exception {
		// Given
		UserPointHistoryDto requestDto = UserPointHistoryDto.builder().userId(0L) // 유효하지 않은 userId
			.pointAmount(1000L).pointStatus(PointStatus.EARN).build();

		when(userUseCase.handleUserPoint(requestDto)).thenThrow(new IllegalArgumentException("userId는 필수 입력 항목입니다."));

		// When & Then
		mockMvc.perform(post("/api/v1/users/handleUserPoint")
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestDto)))
			.andExpect(status().isOk());

	}

	@Test
	@DisplayName("사용자 포인트 충전 또는 사용 시 서버 오류 발생")
	void 사용자_포인트_충전_또는_사용_시_서버_오류_발생() throws Exception {
		// Given
		UserPointHistoryDto requestDto = UserPointHistoryDto.builder()
			.userId(1L)
			.pointAmount(1000L)
			.pointStatus(PointStatus.EARN)
			.build();

		when(userUseCase.handleUserPoint(requestDto)).thenThrow(new RuntimeException("서버 오류"));

		// When & Then
		mockMvc.perform(post("/api/v1/users/handleUserPoint")
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestDto)))
			.andExpect(status().isOk());

	}

	@Test
	@DisplayName("사용자 포인트 히스토리 조회 성공")
	@WithMockUser(roles = "USER") // 모의 인증 사용자 설정
	void 사용자_포인트_히스토리_조회_성공() throws Exception {
		// Given
		UserDto requestDto = UserDto.builder().userId(1L).build();

		UserPointHistoryDto history1 = UserPointHistoryDto.builder()
			.userId(1L)
			.pointAmount(1000L)
			.pointStatus(PointStatus.EARN)
			.build();

		UserPointHistoryDto history2 = UserPointHistoryDto.builder()
			.userId(1L)
			.pointAmount(500L)
			.pointStatus(PointStatus.USE)
			.build();

		List<UserPointHistoryDto> expectedHistories = Arrays.asList(history1, history2);

		when(userUseCase.getUserPointHistoryListByUserId(1L)).thenReturn(expectedHistories);

		// When & Then
		mockMvc.perform(post("/api/v1/users/getUserPointHistoryListByUserId")
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestDto)))
			.andExpect(status().isOk())
			.andExpect(content().json(objectMapper.writeValueAsString(expectedHistories)));

		verify(userUseCase, times(1)).getUserPointHistoryListByUserId(1L);
	}

	@Test
	@DisplayName("사용자 포인트 히스토리 조회 실패 - userId 누락")
	@WithMockUser(roles = "USER") // 모의 인증 사용자 설정
	void 사용자_포인트_히스토리_조회_실패_userId_누락() throws Exception {
		// Given
		UserDto requestDto = UserDto.builder().userId(0L) // 유효하지 않은 userId
			.build();

		// When & Then
		mockMvc.perform(post("/api/v1/users/getUserPointHistoryListByUserId")
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestDto)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("userId는 필수 입력 항목입니다."));

		verify(userUseCase, never()).getUserPointHistoryListByUserId(anyLong());
	}

	@Test
	@DisplayName("사용자 포인트 히스토리 조회 시 서버 오류 발생")
	@WithMockUser(roles = "USER") // 모의 인증 사용자 설정
	void 사용자_포인트_히스토리_조회_시_서버_오류_발생() throws Exception {
		// Given
		UserDto requestDto = UserDto.builder().userId(999L) // 존재하지 않는 사용자 ID 등
			.build();

		when(userUseCase.getUserPointHistoryListByUserId(999L)).thenThrow(new RuntimeException("서버 오류"));

		// When & Then
		mockMvc.perform(post("/api/v1/users/getUserPointHistoryListByUserId")
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestDto)))
			.andExpect(status().isInternalServerError())
			.andExpect(jsonPath("$.message").value("서버 오류가 발생했습니다. 잠시 후 다시 시도해 주세요."));

		verify(userUseCase, times(1)).getUserPointHistoryListByUserId(999L);
	}
}
