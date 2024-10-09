package com.example.hh3week.adapter.in.dto.token;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class TokenDto {
	private long tokenId;

	private long userId;

	private String token;

	private LocalDateTime issuedAt;

	private LocalDateTime expiresAt;
}
