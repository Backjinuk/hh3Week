package com.example.hh3week.integration.adapter.in.controller;// src/test/java/com/example/hh3week/adapter/in/controller/ReservationControllerIntegrationTest.java

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import com.example.hh3week.adapter.in.dto.concert.ConcertScheduleDto;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Sql({"classpath:schema.sql", "classpath:data.sql"})
public class ReservationControllerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper; // JSON 변환을 위한 ObjectMapper

	@Value("${testToken}")
	private String testToken;
	/**
	 * 예약 가능한 좌석 목록 조회 - 정상 요청 시 좌석 목록 반환
	 */
	@Test
	@DisplayName("예약 가능한 좌석 목록 조회 - 정상 요청 시 좌석 목록 반환")
	void getAvailableReservationSeatList_정상요청시좌석목록반환() throws Exception {
		// Given
		ConcertScheduleDto concertScheduleDto = ConcertScheduleDto.builder().concertScheduleId(1L).build();

		// When
		mockMvc.perform(
				post("/api/v1/reservations/getAvailableReservationSeatList").contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(concertScheduleDto)))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.length()").value(1))
			.andExpect(jsonPath("$[0].seatId").value(1))
			.andExpect(jsonPath("$[0].reservationSeatDetailDtoList.length()").value(50))
			.andExpect(jsonPath("$[0].reservationSeatDetailDtoList[0].seatDetailId").value(1))
			.andExpect(jsonPath("$[0].reservationSeatDetailDtoList[0].seatCode").value("A1"))
			.andReturn();

	}

	/**
	 * 예약 가능한 좌석 목록 조회 - 잘못된 concertScheduleId 시 예외 발생
	 */
	@Test
	@DisplayName("예약 가능한 좌석 목록 조회 - 잘못된 concertScheduleId 시 예외 발생")
	void getAvailableReservationSeatList_잘못된ConcertScheduleId시예외발생() throws Exception {
		// Given
		ConcertScheduleDto concertScheduleDto = ConcertScheduleDto.builder().concertScheduleId(0L) // 잘못된 ID
			.build();

		// When & Then
		mockMvc.perform(
			post("/api/v1/reservations/getAvailableReservationSeatList").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(concertScheduleDto))).andExpect(status().isBadRequest());
	}

	/**
	 * 좌석 예약 요청 - 정상 요청 시 토큰 반환
	 */
	@Test
	@DisplayName("좌석 예약 요청 - 정상 요청 시 토큰 반환")
	void reserveSeat_정상요청시토큰반환() throws Exception {
		// Given
		Map<String, Long> requestBodyMap = new HashMap<>();
		requestBodyMap.put("userId", 1L);
		requestBodyMap.put("seatDetailId", 1L); // 예약 가능한 좌석

		// When & Then
		MvcResult mvcResult = mockMvc.perform(
				post("/api/v1/reservations/reserveSeat").contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(requestBodyMap)))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.tokenId").value(7))
			.andExpect(jsonPath("$.userId").value(1))
			.andReturn();

	}

	/**
	 * 좌석 예약 요청 - 누락된 userId 시 예외 발생
	 */
	@Test
	@DisplayName("좌석 예약 요청 - 누락된 userId 시 예외 발생")
	void reserveSeat_누락된UserId시예외발생() throws Exception {
		// Given
		Map<String, Long> requestBodyMap = new HashMap<>();
		// userId 누락
		requestBodyMap.put("seatDetailId", 201L);

		// When & Then
		mockMvc.perform(post("/api/v1/reservations/reserveSeat").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestBodyMap)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("userId와 seatDetailId는 필수 입력 항목입니다."));
	}

	/**
	 * 좌석 예약 요청 - 누락된 seatDetailId 시 예외 발생
	 */
	@Test
	@DisplayName("좌석 예약 요청 - 누락된 seatDetailId 시 예외 발생")
	void reserveSeat_누락된SeatDetailId시예외발생() throws Exception {
		// Given
		Map<String, Long> requestBodyMap = new HashMap<>();
		requestBodyMap.put("userId", 1001L);
		// seatDetailId 누락

		// When & Then
		mockMvc.perform(post("/api/v1/reservations/reserveSeat").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestBodyMap)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("userId와 seatDetailId는 필수 입력 항목입니다."));
	}

	/**
	 * 좌석 예약 요청 - 예약 불가능한 좌석 예약 시 예외 발생
	 */
	@Test
	@DisplayName("좌석 예약 요청 - 예약 불가능한 좌석 예약 시 예외 발생")
	void reserveSeat_예약불가능한좌석예약시예외발생() throws Exception {
		// Given
		Map<String, Long> requestBodyMap = new HashMap<>();
		requestBodyMap.put("userId", 1002L);
		requestBodyMap.put("seatDetailId", 204L); // 예약 불가능한 좌석

		// When & Then
		mockMvc.perform(post("/api/v1/reservations/reserveSeat").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestBodyMap)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("해당 좌석을 찾을수 없습니다."));
	}

	/**
	 * 좌석 예약 요청 - 서비스 계층에서 예외 발생 시 적절한 응답 반환
	 */
	@Test
	@DisplayName("좌석 예약 요청 - 서비스 계층에서 예외 발생 시 적절한 응답 반환")
	void reserveSeat_서비스계층에서예외발생시적절한응답반환() throws Exception {
		// Given
		Map<String, Long> requestBodyMap = new HashMap<>();
		requestBodyMap.put("userId", 1L);
		requestBodyMap.put("seatDetailId", 1L); // 정상적인 좌석

		// 먼저, 좌석을 예약하여 상태를 변경
		mockMvc.perform(post("/api/v1/reservations/reserveSeat").contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(requestBodyMap))).andExpect(status().isOk());

		// 다시 같은 좌석을 예약하려고 시도
		mockMvc.perform(post("/api/v1/reservations/reserveSeat").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestBodyMap)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("사용자가 이미 대기열에 등록되어 있습니다."));

	}

}


