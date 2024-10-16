package com.example.hh3week.adapter.out.persistence;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.example.hh3week.application.port.out.PaymentRepositoryPort;
import com.example.hh3week.domain.payment.entity.PaymentHistory;

@Repository
public class PaymentRepositoryImpl implements PaymentRepositoryPort {

	/**
	 *
	 * @param paymentHistory
	 */
	@Override
	public void registerPaymentHistory(PaymentHistory paymentHistory) {

	}

	/**
	 *
	 * @param paymentId
	 * @return
	 */
	@Override
	public PaymentHistory getPaymentHistory(long paymentId) {
		return null;
	}

	/**
	 *
	 * @param userId
	 * @return
	 */
	@Override
	public List<PaymentHistory> getPaymentHistoryByUserId(long userId) {
		return List.of();
	}
}
