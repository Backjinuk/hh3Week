// src/main/java/com/example/hh3week/adapter/out/persistence/TokenRepositoryImpl.java

package com.example.hh3week.adapter.out.persistence;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.hh3week.application.port.out.TokenRepositoryPort;
import com.example.hh3week.domain.token.entity.QToken;
import com.example.hh3week.domain.token.entity.Token;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Repository
public class TokenRepositoryImpl implements TokenRepositoryPort {

	private final JPAQueryFactory queryFactory;

	@PersistenceContext
	private EntityManager entityManager;

	private final QToken qToken = QToken.token1;

	public TokenRepositoryImpl(JPAQueryFactory queryFactory, EntityManager entityManager) {
		this.queryFactory = queryFactory;
		this.entityManager = entityManager;
	}

	/**
	 * 새로운 토큰을 생성하고 저장합니다.
	 *
	 * @param token 저장할 Token 객체
	 * @return 저장된 Token 객체
	 */
	@Override
	@Transactional
	public Token createToken(Token token) {
		entityManager.persist(token);
		return token;
	}

	/**
	 * 주어진 토큰 문자열을 인증하고 유효한 경우 Token 객체를 반환합니다.
	 *
	 * @param tokenStr 인증할 토큰 문자열
	 * @return 유효한 Token 객체가 있으면 Optional로 반환, 없으면 Optional.empty()
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<Token> authenticateToken(String tokenStr) {
		Token token = queryFactory.selectFrom(qToken)
			.where(qToken.token.eq(tokenStr)
				.and(qToken.expiresAt.after(LocalDateTime.now())))
			.fetchOne();
		return Optional.ofNullable(token);
	}

	/**
	 * 주어진 토큰 문자열을 만료 상태로 업데이트합니다.
	 *
	 * @param tokenStr 만료시킬 토큰 문자열
	 * @throws IllegalArgumentException 토큰이 존재하지 않을 경우
	 */
	@Override
	@Transactional
	public void expireToken(String tokenStr) {
		int updatedCount = (int) queryFactory.update(qToken)
			.set(qToken.expiresAt, LocalDateTime.now())
			.where(qToken.token.eq(tokenStr)
				.and(qToken.expiresAt.after(LocalDateTime.now())))
			.execute();

		if (updatedCount == 0) {
			throw new IllegalArgumentException("만료할 토큰을 찾을 수 없습니다: " + tokenStr);
		}
	}

	/**
	 * 주어진 사용자 ID에 해당하는 토큰을 조회합니다.
	 *
	 * @param userId 조회할 사용자 ID
	 * @return 사용자에게 할당된 Token 객체가 있으면 Optional로 반환, 없으면 Optional.empty()
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<Token> getTokenByUserId(long userId) {
		Token token = queryFactory.selectFrom(qToken)
			.where(qToken.userId.eq(userId)
				.and(qToken.expiresAt.after(LocalDateTime.now())))
			.fetchOne();
		return Optional.ofNullable(token);
	}
}
