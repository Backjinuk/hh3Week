package com.example.hh3week.adapter.in.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PaymentController {
	// 결제 API
	@PostMapping("/payments")
	public ResponseEntity<Map<String, Object>> processPayment(@RequestBody Map<String, Object> request) {
		// 결제 정보 가져오기
		String reservationId = (String) request.get("reservationId");
		double paymentAmount = (double) request.get("paymentAmount");

		// Mock 결제 처리
		Map<String, Object> response = new HashMap<>();
		response.put("paymentId", "PAY987654");
		response.put("status", "COMPLETED");
		response.put("seatOwnership", "ASSIGNED");

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}
