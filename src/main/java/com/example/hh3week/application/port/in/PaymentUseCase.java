package com.example.hh3week.application.port.in;

import java.util.List;

import com.example.hh3week.adapter.in.dto.payment.PaymentHistoryDto;

public interface PaymentUseCase {

	PaymentHistoryDto registerPaymentHistory(PaymentHistoryDto paymentHistoryDto);

	PaymentHistoryDto getPaymentHistory(long paymentId);

	List<PaymentHistoryDto> getPaymentHistoryByUserId(long userId);

}
