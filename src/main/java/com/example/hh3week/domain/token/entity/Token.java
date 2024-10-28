package com.example.hh3week.domain.token.entity;

import java.time.LocalDateTime;

import com.example.hh3week.adapter.in.dto.token.TokenDto;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Version;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Token {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long tokenId;

	private long userId;

	private String token;

	private LocalDateTime issuedAt;

	private LocalDateTime expiresAt;

	@Version
	private long version;

	@Builder
	public Token(long tokenId, long userId, String token, LocalDateTime issuedAt, LocalDateTime expiresAt,
		long version) {
		this.tokenId = tokenId;
		this.userId = userId;
		this.token = token;
		this.issuedAt = issuedAt;
		this.expiresAt = expiresAt;
	}

	public static Token ToEntity(TokenDto tokenDto) {
		return Token.builder()
			.tokenId(tokenDto.getTokenId())
			.userId(tokenDto.getUserId())
			.token(tokenDto.getToken())
			.issuedAt(tokenDto.getIssuedAt())
			.expiresAt(tokenDto.getExpiresAt())
			.build();
	}
}
