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

	@Test
	@DisplayName("특정 날짜 범위 내의 콘서트 조회 성공 - 날짜 있을때")
	void 특정_날짜_범위_내의_콘서트_조회_성공() throws Exception {
		// given
		LocalDateTime startDt = LocalDateTime.of(2024, 01, 01, 19, 00, 00);
		LocalDateTime endDt = LocalDateTime.of(2024, 02, 11, 19, 00, 00);

		ConcertScheduleDto concertScheduleDto = ConcertScheduleDto.builder().startDt(startDt).endDt(endDt).build();

		String concertDtoJson = objectMapper.writeValueAsString(concertScheduleDto);

		// when

		// then
		mockMvc.perform(post("/api/v1/concerts/getAvailableConcertDates").contentType(MediaType.APPLICATION_JSON)
				.content(concertDtoJson))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.length()").value(2))
			.andExpect(jsonPath("$[0].concertPrice").value(10000))
			.andExpect(jsonPath("$[1].concertPrice").value(15000));
	}

	@Test
	@DisplayName("특정 날짜 범위 내의 콘서트 조회 성공 - 날짜 없을때")
	void 특정_날짜_범위_내의_콘서트_조회_성공_날짜_없을때() throws Exception {
		// given

		ConcertScheduleDto concertScheduleDto = ConcertScheduleDto.builder().build();

		String concertDtoJson = objectMapper.writeValueAsString(concertScheduleDto);

		// when

		// then
		mockMvc.perform(post("/api/v1/concerts/getAvailableConcertDates").contentType(MediaType.APPLICATION_JSON)
				.content(concertDtoJson))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.length()").value(3))
			.andExpect(jsonPath("$[0].concertPrice").value(10000))
			.andExpect(jsonPath("$[1].concertPrice").value(15000));
	}

	@Test
	@DisplayName("특정 날짜 범위 내의 콘서트 조회 - 일치하는 날짜 없을때")
	void 특정_날짜_범위_내의_콘서트_조회_성공_일치하는_날짜_없을때() throws Exception {
		// given
		LocalDateTime startDt = LocalDateTime.of(2024, 11, 01, 19, 00, 00);
		LocalDateTime endDt = LocalDateTime.of(2024, 12, 11, 19, 00, 00);

		ConcertScheduleDto concertScheduleDto = ConcertScheduleDto.builder().startDt(startDt).endDt(endDt).build();

		String concertDtoJson = objectMapper.writeValueAsString(concertScheduleDto);

		// when

		// then
		mockMvc.perform(post("/api/v1/concerts/getAvailableConcertDates").contentType(MediaType.APPLICATION_JSON)
			.content(concertDtoJson)).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(0));
	}

	@Test
	@DisplayName("특정 콘서트 조회 성공")
	void 특정_콘서트_조회_성공() throws Exception {
		// given
		ConcertDto concertDto = ConcertDto.builder().concertId(1).build();

		String concertDtoJson = objectMapper.writeValueAsString(concertDto);
		// when

		// then
		mockMvc.perform(post("/api/v1/concerts/getConcertScheduleFindById").contentType(MediaType.APPLICATION_JSON)
				.content(concertDtoJson))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.concertScheduleId").value(1))
			.andExpect(jsonPath("$.concertScheduleStatus").value("AVAILABLE"))
			.andExpect(jsonPath("$.concertPrice").value(10000));
	}

	@Test
	@DisplayName("특정 콘서트 조회 실패 - 아이디 없음")
	void 특정_콘서트_조회_실패_아이디_없음() throws Exception {
		// given
		ConcertDto concertDto = ConcertDto.builder().build();

		String concertDtoJson = objectMapper.writeValueAsString(concertDto);

		// when

		// then
		mockMvc.perform(post("/api/v1/concerts/getConcertScheduleFindById").contentType(MediaType.APPLICATION_JSON)
			.content(concertDtoJson)).andExpect(status().isBadRequest()); // 500 상태 코드 대
	}


}