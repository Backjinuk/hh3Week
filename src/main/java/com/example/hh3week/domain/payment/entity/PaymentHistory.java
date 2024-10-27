package com.example.hh3week.domain.payment.entity;

import java.time.LocalDateTime;

import com.example.hh3week.adapter.in.dto.payment.PaymentHistoryDto;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentHistory {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long paymentId;

	private long userId;

	private long reservationId;

	private long paymentAmount;

	private LocalDateTime paymentDt;

	@Enumerated(EnumType.STRING)
	private PaymentStatus paymentStatus;

	@Builder
	public PaymentHistory(long paymentId, long userId, long reservationId, long paymentAmount, LocalDateTime paymentDt,
		PaymentStatus paymentStatus) {
		this.paymentId = paymentId;
		this.userId = userId;
		this.reservationId = reservationId;
		this.paymentAmount = paymentAmount;
		this.paymentDt = paymentDt;
		this.paymentStatus = paymentStatus;
	}



	public static PaymentHistory ToEntity(PaymentHistoryDto paymentHistoryDto){
		return PaymentHistory.builder()
			.paymentId(paymentHistoryDto.getPaymentId())
			.userId(paymentHistoryDto.getUserId())
			.reservationId(paymentHistoryDto.getReservationId())
			.paymentAmount(paymentHistoryDto.getPaymentAmount())
			.paymentStatus(paymentHistoryDto.getPaymentStatus())
			.build();
	}
}
