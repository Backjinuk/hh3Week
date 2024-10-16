package com.example.hh3week.application.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.hh3week.adapter.in.dto.payment.PaymentHistoryDto;
import com.example.hh3week.application.port.out.PaymentRepositoryPort;
import com.example.hh3week.domain.payment.entity.PaymentHistory;

@Service
public class PaymentHistoryService {

	private final PaymentRepositoryPort paymentHistoryRepositoryPort;

	public PaymentHistoryService(PaymentRepositoryPort paymentRepositoryPort) {
		this.paymentHistoryRepositoryPort = paymentRepositoryPort;
	}

	/*
	 * 결제 관련 service
	 *
	 * 결제 내역 등록
	 */
	public void registerPaymentHistory(PaymentHistoryDto paymentHistoryDto) {
		// 결제 내역 등록 로직 구현 예정
		paymentHistoryRepositoryPort.registerPaymentHistory(PaymentHistory.ToEntity(paymentHistoryDto));
	}

	/*
	 * 결제 내역 가지고 오기
	 */
	public PaymentHistoryDto getPaymentHistory(long paymentId) {
		// 결제 내역 조회 로직 구현 예정
		return PaymentHistoryDto.ToDto(paymentHistoryRepositoryPort.getPaymentHistory(paymentId));
	}

	/*
	 * 사용자별 결제 내역 조회
	 */
	public List<PaymentHistoryDto> getPaymentHistoryByUserId(long userId) {
		// 사용자별 결제 내역 조회 로직 구현 예정
		return paymentHistoryRepositoryPort.getPaymentHistoryByUserId(userId).stream().map(PaymentHistoryDto::ToDto).toList();
	}

}
