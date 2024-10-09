
package com.example.hh3week.adapter.in.controller;

import com.example.hh3week.adapter.in.dto.token.TokenDto;
import com.example.hh3week.adapter.in.dto.user.UserDto;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tokens")
public class TokenController {


	// 토큰 생성
	@PostMapping
	public ResponseEntity<TokenDto> generateToken(@RequestBody UserDto request) {

		long userId = request.getUserId();
		String token = UUID.randomUUID().toString();
		LocalDateTime issuedAt = LocalDateTime.now();
		LocalDateTime expiresAt = issuedAt.plusMinutes(5);
		long tokenId = Math.abs(new Random().nextLong());


		TokenDto tokenDto = new TokenDto(tokenId, userId, token, issuedAt, expiresAt);
		return new ResponseEntity<>(tokenDto, HttpStatus.CREATED);
	}
}
