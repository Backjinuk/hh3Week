// src/main/java/com/example/hh3week/adapter/in/controller/UserController.java
package com.example.hh3week.adapter.in.controller;

import com.example.hh3week.adapter.in.dto.user.UserDto;
import com.example.hh3week.adapter.in.dto.user.UserPointHistoryDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/api/v1")
public class UserController {

	// 사용자 잔액을 저장할 메모리 맵 (userId -> pointBalance)
	private final ConcurrentHashMap<Long, Integer> userBalances = new ConcurrentHashMap<>();
	private final AtomicLong userIdGenerator = new AtomicLong(1);

	/**
	 * 잔액 충전 API
	 *
	 * @param userPointHistoryDto 사용자 ID와 충전할 금액 정보
	 * @return 업데이트된 UserDto
	 */
	@PostMapping("/balance")
	public ResponseEntity<UserDto> chargeBalance(@RequestBody UserPointHistoryDto userPointHistoryDto) {
		Long userId = userPointHistoryDto.getUserId();
		Integer pointAmount = Math.toIntExact(userPointHistoryDto.getPointAmount());

		// 유효성 검사: userId와 pointAmount가 null이 아니고, pointAmount가 음수가 아닌지 확인
		if (userId == null || pointAmount == null || pointAmount < 0) {
			throw new IllegalArgumentException("User ID와 충전 금액은 필수이며, 충전 금액은 음수가 될 수 없습니다.");
		}

		// 기존 잔액 조회 (없을 경우 0으로 초기화)
		Integer currentBalance = userBalances.getOrDefault(userId, 0);

		// 잔액 충전
		Integer updatedBalance = currentBalance + pointAmount;
		userBalances.put(userId, updatedBalance);

		// 사용자 정보 생성 (Mock)
		UserDto userDto = new UserDto(userId, "향해 테스트", updatedBalance );

		return new ResponseEntity<>(userDto, HttpStatus.OK);
	}

	/**
	 * 잔액 조회 API
	 *
	 * @param userId 사용자 ID
	 * @return UserDto
	 */
	@GetMapping("/balance/{userId}")
	public ResponseEntity<UserDto> getBalance(@PathVariable Long userId) {
		// 사용자 잔액 조회
		Integer currentBalance = userBalances.get(userId);

		if (currentBalance == null) {
			throw new IllegalArgumentException("사용자를 찾을 수 없습니다.");
		}

		// 사용자 정보 생성 (Mock)
		UserDto userDto = new UserDto(userId, currentBalance, "향해 테스트");

		return new ResponseEntity<>(userDto, HttpStatus.OK);
	}
}
