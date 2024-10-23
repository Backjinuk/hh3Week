package com.example.hh3week.application.service;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.hh3week.adapter.in.dto.token.TokenDto;
import com.example.hh3week.application.port.out.TokenRepositoryPort;
import com.example.hh3week.common.util.jwt.JwtProvider;
import com.example.hh3week.domain.token.entity.Token;

@Service
public class TokenService {

	private final JwtProvider jwtProvider;
	private final TokenRepositoryPort tokenRepository;

	@Autowired
	public TokenService(JwtProvider jwtProvider, TokenRepositoryPort tokenRepository) {
		this.jwtProvider = jwtProvider;
		this.tokenRepository = tokenRepository;
	}

	/**
	 * 사용자 정보 및 대기열 정보를 받아 JWT 토큰을 생성하고 저장합니다.
	 *
	 * @param userId        사용자 고유 ID
	 * @param queueOrder    대기열 순서
	 * @param remainingTime 남은 대기 시간 (초 단위)
	 * @param seatDetailId
	 * @return 생성된 토큰의 DTO
	 */
	@Transactional
	public TokenDto createToken(long userId, long queueOrder, long remainingTime, long seatDetailId) {
		// JWT 토큰 생성
		String token = jwtProvider.generateToken(userId, queueOrder, remainingTime, seatDetailId);

		// 토큰 엔티티 생성
		TokenDto tokenEntity = TokenDto.builder()
			.userId(userId)
			.token(token)
			.issuedAt(LocalDateTime.now())
			.expiresAt(LocalDateTime.now().plusSeconds(remainingTime))
			.build();

		// 토큰 엔티티를 DB에 저장
		Token savedToken = tokenRepository.createToken(Token.ToEntity(tokenEntity));

		// DTO로 변환하여 반환
		return TokenDto.ToDto(savedToken);
	}

	/**
	 * 토큰의 유효성을 검증하고 사용자 ID를 반환합니다.
	 *
	 * @param token JWT 토큰
	 * @return 유효한 토큰이면 사용자 ID를 반환, 아니면 null
	 */
	public Long validateTokenAndGetUserId(String token) {
		if (jwtProvider.validateToken(token)) {
			return jwtProvider.getUserIdFromJWT(token);
		}

		return null;
	}

	public Map<String, Object> getTokensAllValue(String token) {
		if (jwtProvider.validateToken(token)) {
			return jwtProvider.getMapFromJWT(token);
		}

		return null;
	}

	/**
	 * 토큰이 만료되었는지 확인합니다.
	 *
	 * @param token JWT 토큰
	 * @return 만료되었으면 true, 아니면 false
	 */
	public boolean isTokenExpired(String token) {

		return jwtProvider.isTokenExpired(token);
	}
}
