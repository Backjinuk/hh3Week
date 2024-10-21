package com.example.hh3week.adapter.in.controller;

import com.example.hh3week.adapter.in.dto.payment.PaymentHistoryDto;
import com.example.hh3week.domain.payment.entity.PaymentStatus;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/api/v1")
public class PaymentController {

	// 결제 내역을 저장할 메모리 맵 (paymentId -> PaymentHistoryDto)
	private final ConcurrentHashMap<Long, PaymentHistoryDto> paymentHistories = new ConcurrentHashMap<>();
	private final AtomicLong paymentIdGenerator = new AtomicLong(1);

	/**
	 * 결제 처리 API
	 *
	 * @param paymentHistoryDto 결제 요청 정보
	 * @return 결제 내역
	 */
	@PostMapping("/payments")
	public ResponseEntity<PaymentHistoryDto> processPayment(@RequestBody PaymentHistoryDto paymentHistoryDto) {
		Long userId = paymentHistoryDto.getUserId();
		Long reservationId = paymentHistoryDto.getReservationId();
		long amount = paymentHistoryDto.getPaymentAmount();

		// 유효성 검사: 필수 필드 확인
		if (userId == null || reservationId == null || amount == 0 || amount <= 0) {
			throw new IllegalArgumentException("User ID, Reservation ID, 그리고 유효한 금액은 필수입니다.");
		}

		// 결제 ID 생성
		Long paymentId = paymentIdGenerator.getAndIncrement();
		paymentHistoryDto.setPaymentId(paymentId);

		// 결제 상태 설정 (Mock 결제 처리)
		paymentHistoryDto.setPaymentStatus(PaymentStatus.COMPLETED);

		// 결제 내역 저장
		paymentHistories.put(paymentId, paymentHistoryDto);

		// 결제 완료 후 좌석 소유권 배정 및 대기열 토큰 만료는 실제 로직에서 처리
		// 여기서는 더미 데이터만 반환

		return new ResponseEntity<>(paymentHistoryDto, HttpStatus.OK);
	}

	/**
	 * 결제 내역 조회 API
	 *
	 * @param paymentId 조회할 결제 ID
	 * @return 결제 내역
	 */
	@GetMapping("/payments/{paymentId}")
	public ResponseEntity<PaymentHistoryDto> getPaymentHistory(@PathVariable Long paymentId) {
		PaymentHistoryDto paymentHistoryDto = paymentHistories.get(paymentId);
		if (paymentHistoryDto == null) {
			throw new IllegalArgumentException("결제 내역을 찾을 수 없습니다.");
		}
		return new ResponseEntity<>(paymentHistoryDto, HttpStatus.OK);
	}
}
