package com.example.hh3week.unit.application.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.hh3week.adapter.in.dto.payment.PaymentHistoryDto;
import com.example.hh3week.application.port.out.PaymentRepositoryPort;
import com.example.hh3week.application.service.PaymentHistoryService;
import com.example.hh3week.domain.payment.entity.PaymentHistory;
import com.example.hh3week.domain.payment.entity.PaymentStatus;

class PaymentHistoryServiceTest {

	@Mock
	private PaymentRepositoryPort paymentHistoryRepositoryPort;

	@InjectMocks
	private PaymentHistoryService paymentHistoryService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	@DisplayName("결제 내역 등록 성공")
	void 결제_내역_등록_성공() {
		// given
		PaymentHistoryDto paymentHistoryDto = PaymentHistoryDto.builder()
			.paymentId(1L)
			.userId(1L)
			.paymentAmount(10000L)
			.paymentStatus(PaymentStatus.COMPLETED)
			.build();

		PaymentHistory paymentHistory = PaymentHistory.ToEntity(paymentHistoryDto);

		when(paymentHistoryRepositoryPort.registerPaymentHistory(any())).thenReturn(paymentHistory);

		// when
		PaymentHistoryDto paymentHistoryDto1 = paymentHistoryService.registerPaymentHistory(paymentHistoryDto);

		// then
		assertThat(paymentHistoryDto1.getPaymentId()).isEqualTo(1);
		assertThat(paymentHistoryDto1.getPaymentAmount()).isEqualTo(10000L);
		assertThat(paymentHistoryDto1.getPaymentStatus()).isEqualTo(PaymentStatus.COMPLETED);
	}

	@Test
	@DisplayName("결제 내역 조회 성공")
	void 결제_내역_조회_성공() {
		// given
		long paymentId = 1L;
		PaymentHistoryDto paymentHistoryDto = PaymentHistoryDto.builder()
			.paymentId(paymentId)
			.userId(1L)
			.paymentAmount(10000L)
			.paymentStatus(PaymentStatus.COMPLETED)
			.build();

		when(paymentHistoryRepositoryPort.getPaymentHistory(paymentId)).thenReturn(
			PaymentHistory.ToEntity(paymentHistoryDto));

		// when
		PaymentHistoryDto result = paymentHistoryService.getPaymentHistory(paymentId);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getPaymentId()).isEqualTo(paymentId);
		assertThat(result.getPaymentStatus()).isEqualTo(PaymentStatus.COMPLETED);
		verify(paymentHistoryRepositoryPort, times(1)).getPaymentHistory(paymentId);
	}

	@Test
	@DisplayName("사용자별 결제 내역 조회 성공")
	void 사용자별_결제_내역_조회_성공() {
		// given
		long userId = 1L;
		PaymentHistoryDto paymentHistoryDto1 = PaymentHistoryDto.builder()
			.paymentId(1L)
			.userId(userId)
			.paymentAmount(10000L)
			.paymentStatus(PaymentStatus.COMPLETED)
			.build();

		PaymentHistoryDto paymentHistoryDto2 = PaymentHistoryDto.builder()
			.paymentId(2L)
			.userId(userId)
			.paymentAmount(20000L)
			.paymentStatus(PaymentStatus.COMPLETED)
			.build();

		List<PaymentHistory> paymentHistoryList = Arrays.asList(paymentHistoryDto1, paymentHistoryDto2).stream().map(PaymentHistory::ToEntity).toList();

		when(paymentHistoryRepositoryPort.getPaymentHistoryByUserId(userId)).thenReturn(paymentHistoryList);

		// when
		List<PaymentHistoryDto> result = paymentHistoryService.getPaymentHistoryByUserId(userId);

		// then
		assertThat(result).hasSize(2);
		assertThat(result.get(0).getUserId()).isEqualTo(userId);
		assertThat(result.get(1).getUserId()).isEqualTo(userId);
		verify(paymentHistoryRepositoryPort, times(1)).getPaymentHistoryByUserId(userId);
	}

	@Test
	@DisplayName("결제 내역 조회 실패 - 존재하지 않는 결제 ID")
	void 결제_내역_조회_실패_존재하지_않는_결제_ID() {
		// given
		long paymentId = 999L;
		when(paymentHistoryRepositoryPort.getPaymentHistory(paymentId)).thenReturn(PaymentHistory.builder().build());

		// when
		PaymentHistoryDto result = paymentHistoryService.getPaymentHistory(paymentId);

		// then
		assertThat(result.getPaymentId()).isEqualTo(0);
		verify(paymentHistoryRepositoryPort, times(1)).getPaymentHistory(paymentId);
	}
}
