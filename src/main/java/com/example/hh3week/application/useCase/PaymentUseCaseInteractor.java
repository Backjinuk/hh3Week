package com.example.hh3week.application.useCase;

import org.springframework.stereotype.Service;

import com.example.hh3week.application.port.in.PaymentUseCase;
import com.example.hh3week.application.service.PaymentHistoryService;

@Service
public class PaymentUseCaseInteractor implements PaymentUseCase {

	private final PaymentHistoryService paymentHistoryService;

	public PaymentUseCaseInteractor(PaymentHistoryService paymentHistoryService) {
		this.paymentHistoryService = paymentHistoryService;
	}
}
