package com.example.hh3week.application.domain.payment.entity;

public enum PaymentStatus {
	PENDING,      // 결제 처리 대기 중
	COMPLETED,    // 결제 완료
	FAILED,       // 결제 실패
	REFUNDED,     // 결제 환불됨
	CANCELED      // 결제 취소됨
}
