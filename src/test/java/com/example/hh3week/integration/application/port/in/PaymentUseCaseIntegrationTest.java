package com.example.hh3week.integration.application.port.in;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import com.example.hh3week.adapter.in.dto.payment.PaymentHistoryDto;
import com.example.hh3week.adapter.in.dto.user.UserDto;
import com.example.hh3week.application.service.PaymentHistoryService;
import com.example.hh3week.application.service.UserService;
import com.example.hh3week.application.useCase.PaymentUseCaseInteractor;
import com.example.hh3week.domain.payment.entity.PaymentStatus;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
@Sql({"classpath:schema.sql", "classpath:data.sql"})
public class PaymentUseCaseIntegrationTest {

	@Autowired
	private PaymentUseCaseInteractor paymentUseCaseInteractor;

	@Autowired
	private UserService userService;

	@Autowired
	private PaymentHistoryService paymentHistoryService;

	/**
	 * 성공적인 결제 처리 테스트
	 */
	@Test
	@DisplayName("registerPaymentHistory - 성공적인 결제 처리")
	void registerPaymentHistory_성공적인_결제_처리() {
		// Given
		PaymentHistoryDto paymentHistoryDto = new PaymentHistoryDto();
		paymentHistoryDto.setToken(
			"eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwicXVldWVPcmRlciI6MSwicmVtYWluaW5nVGltZSI6NjAwLCJzZWF0RGV0YWlsSWQiOjMsImlhdCI6MTcyOTU5MTQ0MywiZXhwIjozNzI5Njc3ODQzfQ.V7jeMZu36Pq_DuQBufVX-ULuKpMgmFnYX3bFOmusKiAgwQh_-nPjrTR8Q8ewkM86EoNgexrW092gkjxuQIefWg"); // token_id=1, user_id=101, 유효한 토큰

		// When
		PaymentHistoryDto result = paymentUseCaseInteractor.registerPaymentHistory(paymentHistoryDto);

		// Then
		assertNotNull(result);
		assertNotNull(result.getPaymentId());
		assertEquals(15000, result.getPaymentAmount());
		assertEquals(PaymentStatus.COMPLETED, result.getPaymentStatus());

		// 사용자 잔액 검증 (user_id=101: 초기 20000 - 10000 = 10000)
		UserDto user = userService.getUserInfo(1L);
		assertEquals(100000, user.getPointBalance());

		// 결제 히스토리 검증
		PaymentHistoryDto fetchedPayment = paymentHistoryService.getPaymentHistory(result.getPaymentId());
		assertNotNull(fetchedPayment);
		assertEquals(result.getPaymentId(), fetchedPayment.getPaymentId());
		assertEquals(result.getPaymentAmount(), fetchedPayment.getPaymentAmount());
		assertEquals(result.getPaymentStatus(), fetchedPayment.getPaymentStatus());
	}

	/**
	 * 토큰 만료로 인한 결제 실패 테스트
	 */
	@Test
	@DisplayName("registerPaymentHistory - 토큰 만료로 인한 결제 실패")
	void registerPaymentHistory_토큰_만료로_인한_결제_실패() {
		// Given
		PaymentHistoryDto paymentHistoryDto = new PaymentHistoryDto();
		paymentHistoryDto.setToken(
			"eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIyIiwicXVldWVPcmRlciI6MiwicmVtYWluaW5nVGltZSI6MTIwMCwic2VhdERldGFpbElkIjo1LCJpYXQiOjE3Mjk1OTQxMzAsImV4cCI6MTcyOTU5NDEzMH0.uH9jId_nN-_3k_pKqGd6I-n_3TROmwf7aoc-5WCdDjVToXgHSw1Lm34GnwE8DXSD2o8Cy5vufgikghjgVkXfHw"); // token_id=2, user_id=102, 만료된 토큰

		// When & Then

		assertThatThrownBy(() -> paymentUseCaseInteractor.registerPaymentHistory(paymentHistoryDto)).isInstanceOf(
			IllegalArgumentException.class).hasMessage("토큰값이 만료되었습니다.");

	}

	/**
	 * 토큰 정보 없음으로 인한 결제 실패 테스트
	 */
	@Test
	@DisplayName("registerPaymentHistory - 토큰 정보 없음으로 인한 결제 실패")
	void registerPaymentHistory_토큰_정보_없음으로_인한_결제_실패() {
		// Given
		PaymentHistoryDto paymentHistoryDto = new PaymentHistoryDto();
		paymentHistoryDto.setToken("invalid-token"); // 존재하지 않는 토큰

		// When & Then
		assertThatThrownBy(() -> paymentUseCaseInteractor.registerPaymentHistory(paymentHistoryDto)).isInstanceOf(
			IllegalArgumentException.class).hasMessage("토큰값이 만료되었습니다.");
	}

	/**
	 * 사용자 잔액 부족으로 인한 결제 실패 테스트
	 */
	@Test
	@DisplayName("registerPaymentHistory - 사용자 잔액 부족으로 인한 결제 실패")
	void registerPaymentHistory_사용자_잔액_부족으로_인한_결제_실패() {
		// Given
		PaymentHistoryDto paymentHistoryDto = new PaymentHistoryDto();
		paymentHistoryDto.setToken(
			"eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxMDQiLCJxdWV1ZU9yZGVyIjoyLCJyZW1haW5pbmdUaW1lIjoxMjAwLCJzZWF0RGV0YWlsSWQiOjQsImlhdCI6MTcyOTU5MTY4MiwiZXhwIjozNzI5Njc4MDgyfQ.A2u8Ux-oNuursObK8nVEX8ccdRQ8NHsUi2VlIp3RYfUgPm4WcUtW1V9Qf6xgcg38jD9Ij872gdhd50yROKOYJw"); // token_id=4, user_id=104, seat_detail_id=4

		// When & Then
		assertThatThrownBy(() -> paymentUseCaseInteractor.registerPaymentHistory(paymentHistoryDto)).isInstanceOf(
			IllegalArgumentException.class).hasMessage("사용할 포인트가 부족합니다.");

		// 사용자 잔액 검증 (user_id=104: 초기 5000 - 시도한 결제 금액=10000 -> 잔액 유지)
		UserDto user = userService.getUserInfo(104L);
		assertEquals(10, user.getPointBalance());
	}

	/**
	 * 좌석 상세정보 미존재로 인한 결제 실패 테스트
	 */
	@Test
	@DisplayName("registerPaymentHistory - 사용자 정보 미존재로 인한 결제 실패")
	void registerPaymentHistory_사용자_정보_미존재로_인한_결제_실패() {
		// Given
		PaymentHistoryDto paymentHistoryDto = new PaymentHistoryDto();
		paymentHistoryDto.setToken(
			"eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxMDEiLCJxdWV1ZU9yZGVyIjoxLCJyZW1haW5pbmdUaW1lIjoyMDAwLCJzZWF0RGV0YWlsSWQiOjk5OTksImlhdCI6MTcyOTU5MTQ0MywiZXhwIjozNzI5Njc4MTc5fQ.V7jeMZu36Pq_DuQBufVX-ULuKpMgmFnYX3bFOmusKiAgwQh_-nPjrTR8Q8ewkM86EoNgexrW092gkjxuQIefWg"); // token_id=5, user_id=999, seat_detail_id=9999

		// When & Then
		assertThatThrownBy(() -> paymentUseCaseInteractor.registerPaymentHistory(paymentHistoryDto)).isInstanceOf(
			IllegalArgumentException.class).hasMessage("토큰값이 만료되었습니다.");
	}

	/**
	 * 사용자 정보 미존재로 인한 결제 실패 테스트
	 */
	@Test
	@DisplayName("registerPaymentHistory - 좌석 상세정보 미존재로 인한 결제 실패")
	void registerPaymentHistory_좌석_상세정보_미존재로_인한_결제_실패() {
		// Given
		PaymentHistoryDto paymentHistoryDto = new PaymentHistoryDto();
		paymentHistoryDto.setToken(
			"eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI5OTkiLCJxdWV1ZU9yZGVyIjoxLCJyZW1haW5pbmdUaW1lIjo2MDAsInNlYXREZXRhaWxJZCI6NCwiaWF0IjoxNzI5NTkxNzc5LCJleHAiOjM3Mjk2NzgxNzl9.6s1px0ZC4ONr5sbRZQgqZ-CVecod4YxW4Z6Lylt_RqRNE9tx6UIpqL7ZEZChd_lRGin-WC3vbuR0nx7kr5os1A"); // token_id=6, user_id=9999, seat_detail_id=1

		// When & Then
		assertThatThrownBy(() -> paymentUseCaseInteractor.registerPaymentHistory(paymentHistoryDto)).isInstanceOf(
			NullPointerException.class).hasMessage("사용자를 찾을 수 없습니다.");

	}

}
