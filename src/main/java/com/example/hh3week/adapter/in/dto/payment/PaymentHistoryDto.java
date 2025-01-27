package com.example.hh3week.adapter.in.dto.payment;


import com.example.hh3week.domain.payment.entity.PaymentHistory;
import com.example.hh3week.domain.payment.entity.PaymentStatus;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Data
@NoArgsConstructor
public class PaymentHistoryDto {
	private long paymentId;

	private long userId;

	private long reservationId;

	private long paymentAmount;

	private PaymentStatus paymentStatus;

	private String token;

	@Builder
	public PaymentHistoryDto(long paymentId, long userId, long reservationId, long paymentAmount,
		PaymentStatus paymentStatus) {
		this.paymentId = paymentId;
		this.userId = userId;
		this.reservationId = reservationId;
		this.paymentAmount = paymentAmount;
		this.paymentStatus = paymentStatus;
	}

	public static PaymentHistoryDto ToDto(PaymentHistory paymentHistory){
		return PaymentHistoryDto.builder()
			.paymentId(paymentHistory.getPaymentId())
			.userId(paymentHistory.getUserId())
			.reservationId(paymentHistory.getReservationId())
			.paymentAmount(paymentHistory.getPaymentAmount())
			.paymentStatus(paymentHistory.getPaymentStatus())
			.build();
	}
}
