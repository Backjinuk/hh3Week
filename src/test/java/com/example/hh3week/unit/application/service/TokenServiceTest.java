package com.example.hh3week.unit.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.hh3week.adapter.in.dto.token.TokenDto;
import com.example.hh3week.application.port.out.TokenRepositoryPort;
import com.example.hh3week.application.service.TokenService;
import com.example.hh3week.common.util.JwtProvider;
import com.example.hh3week.domain.token.entity.Token;

public class TokenServiceTest {

	@Mock
	private JwtProvider jwtProvider;

	@Mock
	private TokenRepositoryPort tokenRepository;

	@InjectMocks
	private TokenService tokenService;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	@DisplayName("JWT 토큰을 정상적으로 생성할 수 있다.")
	public void createToken_CreatesValidToken() {
		// given
		long userId = 1L;
		int queueOrder = 1;
		long remainingTime = 600L; // 10분
		String expectedToken = "test-jwt-token";

		// Mock the JwtProvider to return a token
		when(jwtProvider.generateToken(userId, queueOrder, remainingTime)).thenReturn(expectedToken);

		// Mock the repository to return the saved token
		Token savedToken = Token.builder()
			.tokenId(1L)
			.userId(userId)
			.token(expectedToken)
			.issuedAt(LocalDateTime.now())
			.expiresAt(LocalDateTime.now().plusSeconds(remainingTime))
			.build();
		when(tokenRepository.createToken(any(Token.class))).thenReturn(savedToken);

		// when
		TokenDto tokenDto = tokenService.createToken(userId, queueOrder, remainingTime);

		// then
		assertNotNull(tokenDto);
		assertEquals(expectedToken, tokenDto.getToken());
		assertEquals(userId, tokenDto.getUserId());
		verify(jwtProvider, times(1)).generateToken(userId, queueOrder, remainingTime);
		verify(tokenRepository, times(1)).createToken(any(Token.class));
	}

	@Test
	@DisplayName("유효한 JWT 토큰을 검증할 수 있다.")
	public void validateTokenAndGetUserId_ValidToken_ReturnsUserId() {
		// given
		String token = "valid-jwt-token";
		long expectedUserId = 1L;

		// Mock the JwtProvider to validate the token and return the userId
		when(jwtProvider.validateToken(token)).thenReturn(true);
		when(jwtProvider.getUserIdFromJWT(token)).thenReturn(expectedUserId);

		// when
		Long actualUserId = tokenService.validateTokenAndGetUserId(token);

		// then
		assertNotNull(actualUserId);
		assertEquals(expectedUserId, actualUserId);
		verify(jwtProvider, times(1)).validateToken(token);
		verify(jwtProvider, times(1)).getUserIdFromJWT(token);
	}

	@Test
	@DisplayName("유효하지 않은 JWT 토큰은 null을 반환한다.")
	public void validateTokenAndGetUserId_InvalidToken_ReturnsNull() {
		// given
		String token = "invalid-jwt-token";

		// Mock the JwtProvider to return false for token validation
		when(jwtProvider.validateToken(token)).thenReturn(false);

		// when
		Long actualUserId = tokenService.validateTokenAndGetUserId(token);

		// then
		assertNull(actualUserId);
		verify(jwtProvider, times(1)).validateToken(token);
		verify(jwtProvider, times(0)).getUserIdFromJWT(token); // getUserIdFromJWT should not be called
	}

	@Test
	@DisplayName("만료된 JWT 토큰을 확인할 수 있다.")
	public void isTokenExpired_ExpiredToken_ReturnsTrue() {
		// given
		String token = "expired-jwt-token";

		// Mock the JwtProvider to return true for expired token
		when(jwtProvider.isTokenExpired(token)).thenReturn(true);

		// when
		boolean isExpired = tokenService.isTokenExpired(token);

		// then
		assertTrue(isExpired);
		verify(jwtProvider, times(1)).isTokenExpired(token);
	}

	@Test
	@DisplayName("유효한 JWT 토큰이 만료되지 않았음을 확인할 수 있다.")
	public void isTokenExpired_ValidToken_ReturnsFalse() {
		// given
		String token = "valid-jwt-token";

		// Mock the JwtProvider to return false for valid token
		when(jwtProvider.isTokenExpired(token)).thenReturn(false);

		// when
		boolean isExpired = tokenService.isTokenExpired(token);

		// then
		assertFalse(isExpired);
		verify(jwtProvider, times(1)).isTokenExpired(token);
	}
}
