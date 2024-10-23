package com.example.hh3week.integration.adapter.in.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.example.hh3week.adapter.in.dto.concert.ConcertDto;
import com.example.hh3week.adapter.in.dto.concert.ConcertScheduleDto;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Sql({"classpath:schema.sql", "classpath:data.sql"})
class ConcertControllerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	/**
	 * 성공적인 특정 날짜 범위 내의 콘서트 조회 테스트
	 */
	@Test
	@DisplayName("특정 날짜 범위 내의 콘서트 조회 성공 - 날짜 있을때")
	// JWT 인증이 필요한 경우, 실제 토큰을 헤더에 추가하는 방식으로 변경 필요
	void 특정_날짜_범위_내의_콘서트_조회_성공() throws Exception {
		// Given
		LocalDateTime startDt = LocalDateTime.of(2024, 1, 1, 19, 0, 0);
		LocalDateTime endDt = LocalDateTime.of(2024, 2, 11, 19, 0, 0);

		ConcertScheduleDto concertScheduleDto = ConcertScheduleDto.builder().startDt(startDt).endDt(endDt).build();

		String concertDtoJson = objectMapper.writeValueAsString(concertScheduleDto);

		// When & Then
		mockMvc.perform(post("/api/v1/concerts/getAvailableConcertDates").contentType(MediaType.APPLICATION_JSON)
				.header("Authorization",
					"Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIyIiwicXVldWVPcmRlciI6MSwicmVtYWluaW5nVGltZSI6NjAwLCJzZWF0RGV0YWlsSWQiOjUsImlhdCI6MTcyOTYwNTIzOSwiZXhwIjoxNzYxMTYyODM5fQ.Ski_jMqgz2CzCYRFUpiDbTHluIUO6wJF2zOUpoO0NvjuwHfgiE_RgfEzJYsiQVj0vWf5XCcg5-m599GqW6_Bbg ") // 실제 JWT 토큰 사용 시 주석 해제
				.content(concertDtoJson))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.length()").value(2))
			.andExpect(jsonPath("$[0].concertPrice").value(10000))
			.andExpect(jsonPath("$[1].concertPrice").value(15000));
	}

	/**
	 * 특정 날짜 범위 내의 콘서트 조회 성공 - 날짜 없을때
	 */
	@Test
	@DisplayName("특정 날짜 범위 내의 콘서트 조회 성공 - 날짜 없을때")
	void 특정_날짜_범위_내의_콘서트_조회_성공_날짜_없을때() throws Exception {
		// Given
		ConcertScheduleDto concertScheduleDto = ConcertScheduleDto.builder().build();

		String concertDtoJson = objectMapper.writeValueAsString(concertScheduleDto);

		// When & Then
		mockMvc.perform(post("/api/v1/concerts/getAvailableConcertDates").contentType(MediaType.APPLICATION_JSON)
				.header("Authorization",
					"Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIyIiwicXVldWVPcmRlciI6MSwicmVtYWluaW5nVGltZSI6NjAwLCJzZWF0RGV0YWlsSWQiOjUsImlhdCI6MTcyOTYwNTIzOSwiZXhwIjoxNzYxMTYyODM5fQ.Ski_jMqgz2CzCYRFUpiDbTHluIUO6wJF2zOUpoO0NvjuwHfgiE_RgfEzJYsiQVj0vWf5XCcg5-m599GqW6_Bbg ") // 실제 JWT 토큰 사용 시 주석 해제
				.content(concertDtoJson))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.length()").value(3))
			.andExpect(jsonPath("$[0].concertPrice").value(10000))
			.andExpect(jsonPath("$[1].concertPrice").value(15000))
			.andExpect(jsonPath("$[2].concertPrice").value(20000));
	}

	/**
	 * 특정 날짜 범위 내의 콘서트 조회 실패 - 일치하는 날짜 없을때
	 */
	@Test
	@DisplayName("특정 날짜 범위 내의 콘서트 조회 - 일치하는 날짜 없을때")
	void 특정_날짜_범위_내의_콘서트_조회_실패_일치하는_날짜_없을때() throws Exception {
		// Given
		LocalDateTime startDt = LocalDateTime.of(2024, 11, 1, 19, 0, 0);
		LocalDateTime endDt = LocalDateTime.of(2024, 12, 11, 19, 0, 0);

		ConcertScheduleDto concertScheduleDto = ConcertScheduleDto.builder().startDt(startDt).endDt(endDt).build();

		String concertDtoJson = objectMapper.writeValueAsString(concertScheduleDto);

		// When & Then
		mockMvc.perform(post("/api/v1/concerts/getAvailableConcertDates").contentType(MediaType.APPLICATION_JSON)
				.content(concertDtoJson))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("콘서트 스케줄을 찾을 수 없습니다."));
	}

	/**
	 * 특정 콘서트 조회 성공
	 */
	@Test
	@DisplayName("특정 콘서트 조회 성공")
	void 특정_콘서트_조회_성공() throws Exception {
		// Given
		ConcertDto concertDto = ConcertDto.builder().concertId(1).build();

		String concertDtoJson = objectMapper.writeValueAsString(concertDto);

		// When & Then
		mockMvc.perform(post("/api/v1/concerts/getConcertScheduleFindById").contentType(MediaType.APPLICATION_JSON)
				.content(concertDtoJson))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.concertScheduleId").value(1))
			.andExpect(jsonPath("$.concertScheduleStatus").value("AVAILABLE"))
			.andExpect(jsonPath("$.concertPrice").value(10000));
	}

	/**
	 * 특정 콘서트 조회 실패 - 아이디 없음
	 */
	@Test
	@DisplayName("특정 콘서트 조회 실패 - 아이디 없음")
	void 특정_콘서트_조회_실패_아이디_없음() throws Exception {
		// Given
		ConcertDto concertDto = ConcertDto.builder().build();

		String concertDtoJson = objectMapper.writeValueAsString(concertDto);

		// When & Then
		mockMvc.perform(post("/api/v1/concerts/getConcertScheduleFindById").contentType(MediaType.APPLICATION_JSON)
				.content(concertDtoJson))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("콘서트 스케줄을 조회할 수 없습니다."));
	}
}