// src/test/java/com/example/hh3week/adapter/in/controller/UserControllerIntegrationTest.java

package com.example.hh3week.adapter.in.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.hh3week.adapter.in.dto.user.UserDto;
import com.example.hh3week.adapter.in.dto.user.UserPointHistoryDto;
import com.example.hh3week.domain.user.entity.PointStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UserControllerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper; // JSON 변환을 위한 ObjectMapper


	@Test
	@DisplayName("사용자 포인트 충전 - 정상적으로 포인트가 증가하고 히스토리에 기록됨")
	void 사용자포인트충전_정상적으로증가하고히스토리에기록됨() throws Exception {
		// Given
		long userId = 1L; // 초기 포인트 0

		UserPointHistoryDto depositDto = UserPointHistoryDto.builder()
			.userId(userId)
			.pointAmount(5000L)
			.pointDt(LocalDateTime.now())
			.pointStatus(PointStatus.EARN)
			.build();

		String requestBody = objectMapper.writeValueAsString(depositDto);

		// When & Then
		mockMvc.perform(post("/api/v1/users/handleUserPoint")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
			.andExpect(status().isOk()) // ResponseEntity.ok() returns 200
			.andExpect(jsonPath("$.historyId").exists())
			.andExpect(jsonPath("$.userId").value(userId))
			.andExpect(jsonPath("$.pointAmount").value(5000))
			.andExpect(jsonPath("$.pointStatus").value("EARN"));

		// 사용자 포인트 히스토리 조회
		UserDto userDto = UserDto.builder().userId(userId).build();
		String userDtoJson = objectMapper.writeValueAsString(userDto);

		mockMvc.perform(post("/api/v1/users/getUserPointHistoryListByUserId")
				.contentType(MediaType.APPLICATION_JSON)
				.content(userDtoJson))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.length()").value(6))
			.andExpect(jsonPath("$[0].pointAmount").value(10000))
			.andExpect(jsonPath("$[0].pointStatus").value("EARN"));
	}

	@Test
	@DisplayName("사용자 포인트 사용 - 정상적으로 포인트가 감소하고 히스토리에 기록됨")
	void 사용자포인트사용_정상적으로감소하고히스토리에기록됨() throws Exception {
		// Given
		long userId = 1L; // 초기 포인트 10000
		UserPointHistoryDto useDto = UserPointHistoryDto.builder()
			.userId(userId)
			.pointAmount(2000L)
			.pointDt(LocalDateTime.now())
			.pointStatus(PointStatus.USE)
			.build();

		String requestBody = objectMapper.writeValueAsString(useDto);

		// When & Then
		mockMvc.perform(post("/api/v1/users/handleUserPoint")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.historyId").exists())
			.andExpect(jsonPath("$.userId").value(userId))
			.andExpect(jsonPath("$.pointAmount").value(2000))
			.andExpect(jsonPath("$.pointStatus").value("USE"));

		// 사용자 포인트 히스토리 조회
		UserDto userDto = UserDto.builder().userId(userId).build();
		String userDtoJson = objectMapper.writeValueAsString(userDto);

		mockMvc.perform(post("/api/v1/users/getUserPointHistoryListByUserId")
				.contentType(MediaType.APPLICATION_JSON)
				.content(userDtoJson))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.length()").value(6)) // 기존 2개 + 새로운 1개
			.andExpect(jsonPath("$[0].pointAmount").value(10000))
			.andExpect(jsonPath("$[0].pointStatus").value("EARN"))
			.andExpect(jsonPath("$[1].pointAmount").value(5000))
			.andExpect(jsonPath("$[1].pointStatus").value("USE"))
			.andExpect(jsonPath("$[2].pointAmount").value(20000))
			.andExpect(jsonPath("$[2].pointStatus").value("EARN"));

	}

	@Test
	@DisplayName("사용자 포인트 사용 - 포인트 부족 시 예외 발생")
	void 사용자포인트사용_포인트부족시예외발생() throws Exception {
		// Given
		long userId = 3L; // 초기 포인트 0
		UserPointHistoryDto useDto = UserPointHistoryDto.builder()
			.userId(userId)
			.pointAmount(10000000000L)
			.pointDt(LocalDateTime.now())
			.pointStatus(PointStatus.USE)
			.build();

		String requestBody = objectMapper.writeValueAsString(useDto);

		// When & Then
		mockMvc.perform(post("/api/v1/users/handleUserPoint")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
			.andExpect(status().isBadRequest());

		// 사용자 포인트 히스토리 조회
		UserDto userDto = UserDto.builder().userId(userId).build();
		String userDtoJson = objectMapper.writeValueAsString(userDto);

		mockMvc.perform(post("/api/v1/users/getUserPointHistoryListByUserId")
				.contentType(MediaType.APPLICATION_JSON)
				.content(userDtoJson))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.length()").value(5));

	}

	@Test
	@DisplayName("사용자 포인트 처리 - 잘못된 포인트 상태 시 예외 발생")
	void 사용자포인트처리_잘못된포인트상태시예외발생() throws Exception {
		// Given
		long userId = 1L;
		UserPointHistoryDto invalidDto = UserPointHistoryDto.builder()
			.userId(userId)
			.pointAmount(1000L)
			.pointStatus(null) // 잘못된 포인트 상태
			.build();

		String requestBody = objectMapper.writeValueAsString(invalidDto);

		// When & Then
		mockMvc.perform(post("/api/v1/users/handleUserPoint")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
			.andExpect(status().isBadRequest());

		// 사용자 포인트 히스토리 조회
		UserDto userDto = UserDto.builder().userId(userId).build();
		String userDtoJson = objectMapper.writeValueAsString(userDto);

		mockMvc.perform(post("/api/v1/users/getUserPointHistoryListByUserId")
				.contentType(MediaType.APPLICATION_JSON)
				.content(userDtoJson))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.length()").value(5)); // 기존 1개
	}

	@Test
	@DisplayName("사용자 정보 조회 - 존재하는 사용자 ID일 경우 정보 반환")
	void 사용자정보조회_존재하는사용자ID일경우정보반환() throws Exception {
		// Given
		long userId = 1L;

		UserDto userDto = UserDto.builder().userId(userId).build();
		String userDtoJson = objectMapper.writeValueAsString(userDto);

		// When & Then
		mockMvc.perform(post("/api/v1/users/getUserPointHistoryListByUserId")
				.contentType(MediaType.APPLICATION_JSON)
				.content(userDtoJson))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.length()").value(5)) // 기존 2개 + 새로운 1개
			.andExpect(jsonPath("$[0].pointAmount").value(10000))
			.andExpect(jsonPath("$[0].pointStatus").value("EARN"))
			.andExpect(jsonPath("$[1].pointAmount").value(5000))
			.andExpect(jsonPath("$[1].pointStatus").value("USE"))
			.andExpect(jsonPath("$[2].pointAmount").value(20000))
			.andExpect(jsonPath("$[2].pointStatus").value("EARN"));
	}

	@Test
	@DisplayName("사용자 포인트 히스토리 조회 - 존재하지 않는 사용자 ID일 경우 예외 발생")
	void 사용자포인트히스토리조회_존재하지않는사용자ID일경우예외발생() throws Exception {
		// Given
		long nonExistentUserId = 999L;

		UserDto userDto = UserDto.builder().userId(nonExistentUserId).build();
		String userDtoJson = objectMapper.writeValueAsString(userDto);

		// When & Then
		mockMvc.perform(post("/api/v1/users/getUserPointHistoryListByUserId")
				.contentType(MediaType.APPLICATION_JSON)
				.content(userDtoJson))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.message").value("사용자를 찾을 수 없습니다. ID: " + nonExistentUserId));
	}

	@Test
	@DisplayName("사용자 포인트 히스토리 조회 - 존재하지 않는 사용자 ID일 경우 빈 리스트 반환")
	void 사용자포인트히스토리조회_존재하지않는사용자ID일경우빈리스트반환() throws Exception {
		// Given
		long nonExistentUserId = 999L;

		UserDto userDto = UserDto.builder().userId(nonExistentUserId).build();
		String userDtoJson = objectMapper.writeValueAsString(userDto);

		// When & Then
		mockMvc.perform(post("/api/v1/users/getUserPointHistoryListByUserId")
				.contentType(MediaType.APPLICATION_JSON)
				.content(userDtoJson))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.message").value("사용자를 찾을 수 없습니다. ID: " + nonExistentUserId));
	}
}
