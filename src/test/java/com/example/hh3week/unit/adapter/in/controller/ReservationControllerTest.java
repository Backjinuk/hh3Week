package com.example.hh3week.unit.adapter.in.controller;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.example.hh3week.adapter.in.controller.ReservationController;
import com.example.hh3week.adapter.in.dto.reservation.ReservationSeatDto;
import com.example.hh3week.adapter.in.dto.token.TokenDto;
import com.example.hh3week.application.port.in.ReservationUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(ReservationController.class)
public class ReservationControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private ReservationUseCase reservationUseCase;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	@DisplayName("예약 가능한 좌석 목록 조회 성공")
	@WithMockUser(roles = "USER") // 모의 인증 사용자 설정
	void 예약_가능한_좌석_목록_조회_성공() throws Exception {
		// Given
		long concertScheduleId = 1L;
		Map<String, Long> requestBody = new HashMap<>();
		requestBody.put("concertScheduleId", concertScheduleId);

		ReservationSeatDto seat1 = ReservationSeatDto.builder()
			.seatId(101L)
			.concertId(1L)
			.maxCapacity(100L)
			.currentReserved(50L)
			.build();

		ReservationSeatDto seat2 = ReservationSeatDto.builder()
			.seatId(102L)
			.concertId(1L)
			.maxCapacity(100L)
			.currentReserved(60L)
			.build();

		List<ReservationSeatDto> expectedSeats = Arrays.asList(seat1, seat2);

		when(reservationUseCase.getAvailableReservationSeatList(concertScheduleId)).thenReturn(expectedSeats);

		// When & Then
		mockMvc.perform(
				post("/api/v1/reservations/getAvailableReservationSeatList")
					.with(csrf())
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(requestBody)))
			.andExpect(status().isOk())
			.andExpect(content().json(objectMapper.writeValueAsString(expectedSeats)));

		verify(reservationUseCase, times(1)).getAvailableReservationSeatList(concertScheduleId);
	}

	@Test
	@DisplayName("좌석 예약 요청 성공")
	@WithMockUser(roles = "USER") // 모의 인증 사용자 설정
	void 좌석_예약_요청_성공() throws Exception {
		// Given
		long userId = 10L;
		long seatDetailId = 1001L;
		Map<String, Long> requestBody = new HashMap<>();
		requestBody.put("userId", userId);
		requestBody.put("seatDetailId", seatDetailId);

		TokenDto tokenDto = TokenDto.builder().token("sample-token-12345").expiresAt(LocalDateTime.now()).build();

		when(reservationUseCase.reserveSeat(userId, seatDetailId)).thenReturn(tokenDto);

		// When & Then
		mockMvc.perform(post("/api/v1/reservations/reserveSeat")
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestBody)))
			.andExpect(status().isOk())
			.andExpect(content().json(objectMapper.writeValueAsString(tokenDto)));

		verify(reservationUseCase, times(1)).reserveSeat(userId, seatDetailId);
	}

	@Test
	@DisplayName("예약 가능한 좌석 조회 시 concertScheduleId 누락")
	@WithMockUser(roles = "USER") // 모의 인증 사용자 설정
	void 예약_가능한_좌석_조회시_concertScheduleId_누락() throws Exception {
		// Given
		Map<String, Long> requestBody = new HashMap<>();
		// concertScheduleId 누락

		// When & Then
		mockMvc.perform(
				post("/api/v1/reservations/getAvailableReservationSeatList")
					.with(csrf())
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(requestBody))
			)
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("concertScheduleId는 필수 입력 항목입니다."));
	}

	@Test
	@DisplayName("좌석 예약 요청 시 userId 누락")
	@WithMockUser(roles = "USER") // 모의 인증 사용자 설정
	void 좌석_예약_요청시_userId_누락() throws Exception {
		// Given
		long seatDetailId = 1001L;
		Map<String, Long> requestBody = new HashMap<>();
		// userId 누락
		requestBody.put("seatDetailId", seatDetailId);

		// When & Then
		mockMvc.perform(post("/api/v1/reservations/reserveSeat")
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestBody)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("userId와 seatDetailId는 필수 입력 항목입니다."));
	}

	@Test
	@DisplayName("좌석 예약 요청 시 seatDetailId 누락")
	@WithMockUser(roles = "USER") // 모의 인증 사용자 설정
	void 좌석_예약_요청시_seatDetailId_누락() throws Exception {
		// Given
		long userId = 10L;
		Map<String, Long> requestBody = new HashMap<>();
		requestBody.put("userId", userId);
		// seatDetailId 누락

		// When & Then
		mockMvc.perform(post("/api/v1/reservations/reserveSeat")
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestBody)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("userId와 seatDetailId는 필수 입력 항목입니다."));
	}

}
