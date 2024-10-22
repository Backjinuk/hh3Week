// src/test/java/com/example/hh3week/unit/adapter/in/controller/ConcertControllerTest.java

package com.example.hh3week.unit.adapter.in.controller;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import com.example.hh3week.adapter.in.controller.ConcertController;
import com.example.hh3week.adapter.in.dto.concert.ConcertDto;
import com.example.hh3week.adapter.in.dto.concert.ConcertScheduleDto;
import com.example.hh3week.application.port.in.ConcertUseCase;
import com.example.hh3week.domain.concert.entity.ConcertScheduleStatus;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(ConcertController.class)
public class ConcertControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private ConcertUseCase concertUseCase;

	@Autowired
	private ObjectMapper objectMapper;

	/**
	 * 테스트 시나리오:
	 * 1. /getAvailableConcertDates 엔드포인트에 유효한 ConcertScheduleDto를 전달하여 예약 가능한 콘서트 목록을 정상적으로 반환하는지 검증
	 */
	@Test
	@DisplayName("특정 날짜 범위 내의 사용 가능한 콘서트 조회 성공")
	@WithMockUser(roles = "USER") // 모의 인증 사용자 설정
	void 특정_날짜_범위_내_사용_가능한_콘서트_조회_성공() throws Exception {
		// Given
		ConcertScheduleDto requestDto = ConcertScheduleDto.builder()
			.startDt(LocalDateTime.of(2024, 5, 1, 0, 0))
			.endDt(LocalDateTime.of(2024, 5, 31, 23, 59))
			.build();

		ConcertScheduleDto concert1 = ConcertScheduleDto.builder()
			.concertScheduleId(1L)
			.concertId(101L)
			.concertScheduleStatus(ConcertScheduleStatus.AVAILABLE)
			.concertPrice(50000L)
			.startDt(LocalDateTime.of(2024, 5, 10, 20, 0))
			.endDt(LocalDateTime.of(2024, 5, 10, 22, 0))
			.build();

		ConcertScheduleDto concert2 = ConcertScheduleDto.builder()
			.concertScheduleId(2L)
			.concertId(102L)
			.concertScheduleStatus(ConcertScheduleStatus.AVAILABLE)
			.concertPrice(60000L)
			.startDt(LocalDateTime.of(2024, 5, 20, 18, 0))
			.endDt(LocalDateTime.of(2024, 5, 20, 20, 0))
			.build();

		List<ConcertScheduleDto> expectedConcerts = Arrays.asList(concert1, concert2);

		when(concertUseCase.getAvailableConcertDates(requestDto.getStartDt(), requestDto.getEndDt())).thenReturn(expectedConcerts);

		String json = objectMapper.writeValueAsString(requestDto);
		System.out.println("Request JSON: " + json); // JSON 확인용 로그

		// When & Then
		mockMvc.perform(post("/api/v1/concerts/getAvailableConcertDates")
				.with(csrf()) // CSRF 토큰 추가
				.contentType(MediaType.APPLICATION_JSON)
				.content(json))
			.andExpect(status().isOk())
			.andExpect(content().json(objectMapper.writeValueAsString(expectedConcerts)));

		verify(concertUseCase, times(1)).getAvailableConcertDates(requestDto.getStartDt(), requestDto.getEndDt());
	}


	/**
	 * 테스트 시나리오:
	 * 1. /getAvailableConcertDates 엔드포인트에 startDt와 endDt가 null인 ConcertScheduleDto를 전달하여 전체 예약 가능한 콘서트 목록을 반환하는지 검증
	 */
	@Test
	@DisplayName("startDt와 endDt가 null일 때 전체 예약 가능한 콘서트 조회 성공")
	@WithMockUser(roles = "USER")
	void startDt와_endDt가_null일_때_전체_예약_가능한_콘서트_조회_성공() throws Exception {
		// Given
		ConcertScheduleDto requestDto = ConcertScheduleDto.builder().startDt(null).endDt(null).build();

		ConcertScheduleDto concert1 = ConcertScheduleDto.builder()
			.concertScheduleId(1L)
			.concertId(101L)
			.concertScheduleStatus(ConcertScheduleStatus.AVAILABLE)
			.concertPrice(50000L)
			.startDt(LocalDateTime.of(2024, 5, 10, 20, 0))
			.endDt(LocalDateTime.of(2024, 5, 10, 22, 0))
			.build();

		ConcertScheduleDto concert2 = ConcertScheduleDto.builder()
			.concertScheduleId(2L)
			.concertId(102L)
			.concertScheduleStatus(ConcertScheduleStatus.AVAILABLE)
			.concertPrice(60000L)
			.startDt(LocalDateTime.of(2024, 5, 20, 18, 0))
			.endDt(LocalDateTime.of(2024, 5, 20, 20, 0))
			.build();

		List<ConcertScheduleDto> expectedConcerts = Arrays.asList(concert1, concert2);

		when(concertUseCase.getAvailableConcertDates(null, null)).thenReturn(expectedConcerts);

		// When & Then
		mockMvc.perform(post("/api/v1/concerts/getAvailableConcertDates")
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestDto)))
			.andExpect(status().isOk())
			.andExpect(content().json(objectMapper.writeValueAsString(expectedConcerts)));

		verify(concertUseCase, times(1)).getAvailableConcertDates(null, null);
	}

	/**
	 * 테스트 시나리오:
	 * 1. /getAvailableConcertDates 엔드포인트에서 ConcertUseCase가 예외를 던질 때, 500 Internal Server Error 응답을 반환하는지 검증
	 */
	@Test
	@DisplayName("특정 날짜 범위 내의 사용 가능한 콘서트 조회 시 서버 오류 발생")
	@WithMockUser(roles = "USER")
	void 특정_날짜_범위_내_사용_가능한_콘서트_조회_시_서버_오류_발생() throws Exception {
		// Given
		ConcertScheduleDto requestDto = ConcertScheduleDto.builder()
			.startDt(LocalDateTime.of(2024, 5, 1, 0, 0))
			.endDt(LocalDateTime.of(2024, 5, 31, 23, 59))
			.build();

		when(concertUseCase.getAvailableConcertDates(requestDto.getStartDt(), requestDto.getEndDt())).thenThrow(
			new RuntimeException("서버 오류"));

		// When & Then
		mockMvc.perform(post("/api/v1/concerts/getAvailableConcertDates")
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestDto)))
			.andExpect(status().isInternalServerError())
			.andExpect(jsonPath("$.message").value("서버 오류가 발생했습니다. 잠시 후 다시 시도해 주세요."));

		verify(concertUseCase, times(1)).getAvailableConcertDates(requestDto.getStartDt(), requestDto.getEndDt());
	}

	/**
	 * 테스트 시나리오:
	 * 1. /getConcertScheduleFindById 엔드포인트에 유효한 ConcertDto를 전달하여 특정 콘서트의 상세 정보를 정상적으로 반환하는지 검증
	 */
	@Test
	@DisplayName("특정 콘서트 조회 성공")
	@WithMockUser(roles = "USER")
	void 특정_콘서트_조회_성공() throws Exception {
		// Given
		ConcertDto requestDto = ConcertDto.builder().concertId(101L).build();

		ConcertScheduleDto expectedConcert = ConcertScheduleDto.builder()
			.concertScheduleId(1L)
			.concertId(101L)
			.concertScheduleStatus(ConcertScheduleStatus.AVAILABLE)
			.concertPrice(50000L)
			.startDt(LocalDateTime.of(2024, 5, 10, 20, 0))
			.endDt(LocalDateTime.of(2024, 5, 10, 22, 0))
			.build();

		when(concertUseCase.getConcertScheduleFindById(requestDto.getConcertId())).thenReturn(expectedConcert);

		// When & Then
		mockMvc.perform(post("/api/v1/concerts/getConcertScheduleFindById")
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestDto)))
			.andExpect(status().isOk())
			.andExpect(content().json(objectMapper.writeValueAsString(expectedConcert)));

		verify(concertUseCase, times(1)).getConcertScheduleFindById(requestDto.getConcertId());
	}

	/**
	 * 테스트 시나리오:
	 * 1. /getConcertScheduleFindById 엔드포인트에 concertId가 누락된 ConcertDto를 전달하여 400 Bad Request 응답을 반환하는지 검증
	 */
	@Test
	@DisplayName("특정 콘서트 조회 시 concertId 누락으로 인한 실패")
	@WithMockUser(roles = "USER")
	void 특정_콘서트_조회_시_concertId_누락으로_인한_실패() throws Exception {
		// Given
		ConcertDto requestDto = ConcertDto.builder()
			// .concertId(null) // concertId 누락
			.build();

		// When & Then
		mockMvc.perform(post("/api/v1/concerts/getConcertScheduleFindById")
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestDto)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("콘서트 스케줄을 조회할 수 없습니다."));

		// Since concertId is primitive 'long', default value is 0L
		verify(concertUseCase, never()).getConcertScheduleFindById(anyLong());
	}

	/**
	 * 테스트 시나리오:
	 * 1. /getConcertScheduleFindById 엔드포인트에서 ConcertUseCase가 예외를 던질 때, 500 Internal Server Error 응답을 반환하는지 검증
	 */
	@Test
	@DisplayName("특정 콘서트 조회 시 서버 오류 발생")
	@WithMockUser(roles = "USER")
	void 특정_콘서트_조회_시_서버_오류_발생() throws Exception {
		// Given
		ConcertDto requestDto = ConcertDto.builder().concertId(999L) // 존재하지 않는 콘서트 ID 등
			.build();

		when(concertUseCase.getConcertScheduleFindById(requestDto.getConcertId())).thenThrow(
			new RuntimeException("서버 오류"));

		// When & Then
		mockMvc.perform(post("/api/v1/concerts/getConcertScheduleFindById")
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestDto)))
			.andExpect(status().isInternalServerError())
			.andExpect(jsonPath("$.message").value("서버 오류가 발생했습니다. 잠시 후 다시 시도해 주세요."));

		verify(concertUseCase, times(1)).getConcertScheduleFindById(requestDto.getConcertId());
	}
}
