package com.example.hh3week.adapter.in.controller;

import com.example.hh3week.adapter.in.dto.user.UserDto;
import com.example.hh3week.adapter.in.dto.user.UserPointHistoryDto;
import com.example.hh3week.application.port.in.UserUseCase;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

	private final UserUseCase userUseCase;

	public UserController(UserUseCase userUseCase) {
		this.userUseCase = userUseCase;
	}

	/**
	 * 사용자 포인트 충전 또는 사용을 처리하는 API 엔드포인트입니다.
	 *
	 * @param userPointHistoryDto 사용자 포인트 히스토리 DTO
	 * @return 저장된 사용자 포인트 히스토리 DTO
	 */
	@Operation(summary = "사용자 포인트 충전 또는 사용", description = "사용자가 포인트를 충전하거나 사용하는 요청을 처리합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "성공적으로 포인트을 처리하고 히스토리를 반환",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = UserPointHistoryDto.class))),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 데이터",
			content = @Content),
		@ApiResponse(responseCode = "500", description = "서버 오류",
			content = @Content)
	})
	@PostMapping("/handleUserPoint")
	public ResponseEntity<UserPointHistoryDto> handleUserPoint(
		@Parameter(description = "사용자 포인트 충전 또는 사용을 위한 DTO", required = true)
		@RequestBody UserPointHistoryDto userPointHistoryDto) {

		UserPointHistoryDto processedHistory = userUseCase.handleUserPoint(userPointHistoryDto);
		return ResponseEntity.ok(processedHistory);
	}

	/**
	 * 특정 사용자의 포인트 히스토리 목록을 조회하는 API 엔드포인트입니다.
	 *
	 * @param userDto 사용자 ID를 포함한 요청 본문
	 * @return 사용자의 포인트 히스토리 목록
	 */
	@Operation(summary = "사용자 포인트 히스토리 조회", description = "특정 사용자 ID를 기반으로 사용자의 포인트 히스토리 목록을 조회합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "성공적으로 포인트 히스토리 목록을 반환",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = UserPointHistoryDto.class))),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 데이터",
			content = @Content),
		@ApiResponse(responseCode = "500", description = "서버 오류",
			content = @Content)
	})
	@PostMapping("/getUserPointHistoryListByUserId")
	public ResponseEntity<List<UserPointHistoryDto>> getUserPointHistoryListByUserId(
		@Parameter(description = "포인트 히스토리 조회를 위한 사용자 ID", required = true)
		@RequestBody UserDto userDto) {

		long userId = userDto.getUserId();
		if (userId == 0) {
			throw new IllegalArgumentException("userId는 필수 입력 항목입니다.");
		}

		List<UserPointHistoryDto> pointHistoryList = userUseCase.getUserPointHistoryListByUserId(userId);
		return ResponseEntity.ok(pointHistoryList);
	}
}
