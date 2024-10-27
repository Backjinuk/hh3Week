package com.example.hh3week.integration.adapter.out.persistence;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import com.example.hh3week.application.port.out.TokenRepositoryPort;
import com.example.hh3week.domain.token.entity.Token;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
@Sql({"classpath:schema.sql", "classpath:data.sql"})
public class TokenRepositoryImplTest {

	@Autowired
	private TokenRepositoryPort tokenRepositoryImpl;


	@Test
	@DisplayName("새로운 토큰 생성 - 정상적으로 추가됨")
	void 새로운토큰생성_정상적으로추가됨() {
		// Given
		Token newToken = Token.builder()
			.userId(103L) // userId를 설정
			.token("token789")
			.issuedAt(LocalDateTime.of(2024, 1, 1, 12, 0))
			.expiresAt(LocalDateTime.of(2025, 1, 1, 12, 0))
			.build();

		// When
		Token savedToken = tokenRepositoryImpl.createToken(newToken);

		// Then
		assertThat(savedToken.getTokenId()).isGreaterThan(0);
		assertThat(savedToken.getUserId()).isEqualTo(103L);
		assertThat(savedToken.getToken()).isEqualTo("token789");
		assertThat(savedToken.getIssuedAt()).isEqualTo(LocalDateTime.of(2024, 1, 1, 12, 0));
		assertThat(savedToken.getExpiresAt()).isEqualTo(LocalDateTime.of(2025, 1, 1, 12, 0));
	}

	@Test
	@DisplayName("토큰 인증 - 유효한 토큰일 경우 Token 객체 반환")
	void 토큰인증_유효한토큰일경우Token객체반환() {
		// Given
		String validTokenStr = "token123";

		// When
		Optional<Token> tokenOpt = tokenRepositoryImpl.authenticateToken(validTokenStr);

		// Then
		assertThat(tokenOpt).isPresent();
		Token token = tokenOpt.get();
		assertThat(token.getToken()).isEqualTo(validTokenStr);
		assertThat(token.getExpiresAt()).isAfter(LocalDateTime.now());
	}

	@Test
	@DisplayName("토큰 인증 - 만료된 토큰일 경우 Optional.empty() 반환")
	void 토큰인증_만료된토큰일경우OptionalEmpty반환() {
		// Given
		String expiredTokenStr = "token456";

		// When
		Optional<Token> tokenOpt = tokenRepositoryImpl.authenticateToken(expiredTokenStr);

		// Then
		assertThat(tokenOpt).isNotPresent();
	}

	@Test
	@DisplayName("토큰 인증 - 존재하지 않는 토큰일 경우 Optional.empty() 반환")
	void 토큰인증_존재하지않는토큰일경우OptionalEmpty반환() {
		// Given
		String nonExistentTokenStr = "nonExistentToken";

		// When
		Optional<Token> tokenOpt = tokenRepositoryImpl.authenticateToken(nonExistentTokenStr);

		// Then
		assertThat(tokenOpt).isNotPresent();
	}

	@Test
	@DisplayName("토큰 만료 - 존재하지 않는 토큰일 경우 예외 발생")
	void 토큰만료_존재하지않는토큰일경우예외발생() {
		// Given
		String nonExistentTokenStr = "nonExistentToken";

		// When & Then
		assertThrows(InvalidDataAccessApiUsageException.class, () -> {
			tokenRepositoryImpl.expireToken(nonExistentTokenStr);
		});
	}

	@Test
	@DisplayName("사용자 ID로 토큰 조회 - 해당 사용자에게 할당된 토큰이 존재할 경우 반환")
	void 사용자ID로토큰조회_해당사용자에게할당된토큰이존재할경우반환() {
		// Given
		long userId = 101L;

		// When
		Optional<Token> tokenOpt = tokenRepositoryImpl.getTokenByUserId(userId);

		// Then
		assertThat(tokenOpt).isPresent();
		Token token = tokenOpt.get();
		assertThat(token.getUserId()).isEqualTo(userId);
		assertThat(token.getExpiresAt()).isAfter(LocalDateTime.now());
	}

	@Test
	@DisplayName("사용자 ID로 토큰 조회 - 해당 사용자에게 할당된 토큰이 없을 경우 Optional.empty() 반환")
	void 사용자ID로토큰조회_해당사용자에게할당된토큰이없을경우OptionalEmpty반환() {
		// Given
		long userId = 9999L;

		// When
		Optional<Token> tokenOpt = tokenRepositoryImpl.getTokenByUserId(userId);

		// Then
		assertThat(tokenOpt).isNotPresent();
	}
}
