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
	private final ConcurrentHashMap<Long, UserDto> userBalances = new ConcurrentHashMap<>();
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
		if (userId == null || pointAmount == null || pointAmount <= 0) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		// 기존 사용자 정보가 없으면 새로운 사용자 생성
		UserDto userDto = userBalances.getOrDefault(userId, new UserDto(userId, "향해 테스트", 0));

		// 잔액 충전
		int updatedBalance = (int)(userDto.getPointBalance() + pointAmount);

		// 사용자 정보 업데이트
		userDto.setPointBalance(updatedBalance);
		userBalances.put(userId, userDto);

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
		UserDto userDto = userBalances.get(userId);

		if (userDto == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		return new ResponseEntity<>(userDto, HttpStatus.OK);
	}
}
