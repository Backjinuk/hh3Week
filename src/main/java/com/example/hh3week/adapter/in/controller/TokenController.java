// src/main/java/com/example/hh3week/adapter/in/controller/TokenController.java
package com.example.hh3week.adapter.in.controller;

import com.example.hh3week.adapter.in.dto.token.TokenDto;
import com.example.hh3week.adapter.in.dto.user.UserDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/api/v1")
public class TokenController {

	// 토큰 저장소 (토큰 문자열 -> TokenDto)
	private final ConcurrentHashMap<String, TokenDto> tokenStore = new ConcurrentHashMap<>();
	private final AtomicLong tokenIdGenerator = new AtomicLong(1);

	/**
	 * 토큰 생성 API
	 *
	 * @param userDto 사용자 정보
	 * @return 생성된 토큰 정보
	 */
	@PostMapping("/tokens")
	public ResponseEntity<TokenDto> createToken(@RequestBody UserDto userDto) {
		if (userDto.getUserId() == 0) {
			throw new IllegalArgumentException("User ID는 필수입니다.");
		}

		Long tokenId = tokenIdGenerator.getAndIncrement();
		String token = UUID.randomUUID().toString();
		LocalDateTime issuedAt = LocalDateTime.now();
		LocalDateTime expiresAt = issuedAt.plusMinutes(5);

		TokenDto tokenDto = new TokenDto(tokenId, userDto.getUserId(), token, issuedAt, expiresAt);
		tokenStore.put(token, tokenDto);

		return new ResponseEntity<>(tokenDto, HttpStatus.CREATED);
	}

	/**
	 * 대기 번호 조회 API
	 *
	 * @param token 조회할 토큰 값
	 * @return 대기 번호
	 */
	@GetMapping("/tokens/{token}/queue")
	public ResponseEntity<Integer> getQueueNumber(@PathVariable String token) {
		TokenDto tokenDto = tokenStore.get(token);
		if (tokenDto == null) {
			throw new IllegalArgumentException("토큰을 찾을 수 없습니다.");
		}

		// Mock 대기 번호: 토큰 ID를 대기 번호로 사용
		Integer queueNumber = Math.toIntExact(tokenDto.getTokenId());

		return new ResponseEntity<>(queueNumber, HttpStatus.OK);
	}
}
