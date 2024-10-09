package com.example.hh3week.adapter.in.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/mock-api/v1")
public class UserController {

	// 잔액 충전 / 조회 API
	@PostMapping("/balance")
	public ResponseEntity<Map<String, Object>> chargeBalance(@RequestBody Map<String, Object> request) {
		// 충전할 금액과 사용자 정보 가져오기
		String userId = (String) request.get("userId");
		double amount = (double) request.get("amount");

		// Mock 잔액 충전 및 조회
		Map<String, Object> response = new HashMap<>();
		response.put("userId", userId);
		response.put("newBalance", 10000 + amount);  // 임의로 기존 잔액 10000에서 충전

		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}
