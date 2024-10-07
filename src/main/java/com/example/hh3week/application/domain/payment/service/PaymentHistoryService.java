package com.example.hh3week.application.domain.payment.service;

import org.springframework.stereotype.Service;

import com.example.hh3week.application.domain.payment.repository.PaymentHistoryRepository;

@Service
public class PaymentHistoryService {

	private final PaymentHistoryRepository paymentHistoryRepository;

	public PaymentHistoryService(PaymentHistoryRepository paymentHistoryRepository) {
		this.paymentHistoryRepository = paymentHistoryRepository;
	}
}
