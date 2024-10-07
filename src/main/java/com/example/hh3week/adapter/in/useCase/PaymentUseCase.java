package com.example.hh3week.adapter.in.useCase;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.example.hh3week.application.domain.payment.service.PaymentHistoryService;


@Service
public class PaymentUseCase {

	private final PaymentHistoryService paymentHistoryService;

	public PaymentUseCase(PaymentHistoryService paymentHistoryService) {
		this.paymentHistoryService = paymentHistoryService;
	}
}
