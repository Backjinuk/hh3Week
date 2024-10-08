package com.example.hh3week.application.service;

import org.springframework.stereotype.Service;

import com.example.hh3week.application.port.out.PaymentRepositoryPort;

@Service
public class PaymentHistoryService {

	private final PaymentRepositoryPort paymentHistoryRepositoryPort;

	public PaymentHistoryService(PaymentRepositoryPort paymentRepositoryPort) {
		this.paymentHistoryRepositoryPort = paymentRepositoryPort;
	}
}
