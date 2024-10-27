package com.example.hh3week.adapter.in.dto.token;

import java.time.LocalDateTime;

import com.example.hh3week.domain.token.entity.Token;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TokenDto {
	private long tokenId;

	private long userId;

	private String token;

	private LocalDateTime issuedAt;

	private LocalDateTime expiresAt;

	@Builder
	public TokenDto(long tokenId, long userId, String token, LocalDateTime issuedAt, LocalDateTime expiresAt) {
		this.tokenId = tokenId;
		this.userId = userId;
		this.token = token;
		this.issuedAt = issuedAt;
		this.expiresAt = expiresAt;
	}


	public static TokenDto ToDto(Token token){
		return TokenDto.builder()
			.tokenId(token.getTokenId())
			.userId(token.getUserId())
			.token(token.getToken())
			.issuedAt(token.getIssuedAt())
			.expiresAt(token.getExpiresAt())
			.build();
	}
}
