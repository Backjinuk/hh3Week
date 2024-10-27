package com.example.hh3week.application.port.out;

import java.util.List;

import com.example.hh3week.domain.payment.entity.PaymentHistory;

public interface PaymentRepositoryPort {
	PaymentHistory registerPaymentHistory(PaymentHistory paymentHistory);

	PaymentHistory getPaymentHistory(long paymentId);

	List<PaymentHistory> getPaymentHistoryByUserId(long userId);
}
