package com.example.hh3week.adapter.in.controller;

import com.example.hh3week.adapter.in.dto.user.UserDto;
import com.example.hh3week.adapter.in.dto.user.UserPointHistoryDto;
import com.example.hh3week.application.port.in.UserUseCase;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class UserController {

	private final UserUseCase userUseCase;

	public UserController(UserUseCase userUseCase) {
		this.userUseCase = userUseCase;
	}

	/**
	 * 포인트 충전 및 사용 API
	 *
	 * @param userPointHistoryDto 사용자 ID와 포인트 거래 정보
	 * @return 업데이트된 UserPointHistoryDto
	 */
	@PostMapping("/point")
	public ResponseEntity<UserPointHistoryDto> handlePointTransaction(@RequestBody UserPointHistoryDto userPointHistoryDto) {
		try {
			UserPointHistoryDto updatedHistory = userUseCase.handleUserPoint(userPointHistoryDto);
			return new ResponseEntity<>(updatedHistory, HttpStatus.OK);
		} catch (IllegalArgumentException e) {
			// 예외 메시지에 따라 다양한 HTTP 상태 코드를 반환할 수 있습니다.
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * 사용자 잔액 조회 API
	 *
	 * @param userId 사용자 ID
	 * @return UserDto
	 */
	@GetMapping("/balance/{userId}")
	public ResponseEntity<UserDto> getBalance(@PathVariable Long userId) {
		try {
			UserDto userDto = userUseCase.getUserInfo(userId);
			return new ResponseEntity<>(userDto, HttpStatus.OK);
		} catch (IllegalArgumentException e) {
			// 사용자를 찾을 수 없을 때 404 NOT FOUND 반환
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	/**
	 * 특정 사용자의 포인트 히스토리 조회 API
	 *
	 * @param userId 사용자 ID
	 * @return List<UserPointHistoryDto>
	 */
	@GetMapping("/point/history/{userId}")
	public ResponseEntity<List<UserPointHistoryDto>> getUserPointHistory(@PathVariable Long userId) {
		List<UserPointHistoryDto> historyList = userUseCase.getUserPointHistoryListByUserId(userId);
		return new ResponseEntity<>(historyList, HttpStatus.OK);
	}
}
