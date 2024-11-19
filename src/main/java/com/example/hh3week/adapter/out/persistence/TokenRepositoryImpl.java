// src/main/java/com/example/hh3week/adapter/out/persistence/TokenRepositoryImpl.java

package com.example.hh3week.adapter.out.persistence;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import com.example.hh3week.application.port.out.TokenRepositoryPort;
import com.example.hh3week.common.config.exception.CustomException;
import com.example.hh3week.domain.token.entity.QToken;
import com.example.hh3week.domain.token.entity.Token;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceException;
import jakarta.validation.ConstraintViolationException;

@Repository
public class TokenRepositoryImpl implements TokenRepositoryPort {

	private final JPAQueryFactory queryFactory;
	private final QToken qToken = QToken.token1;
	private final RedisTemplate<String, Object> redisTemplate;
	@PersistenceContext
	private EntityManager entityManager;

	public TokenRepositoryImpl(JPAQueryFactory queryFactory, EntityManager entityManager,
		RedisTemplate<String, Object> redisTemplate) {
		this.queryFactory = queryFactory;
		this.entityManager = entityManager;
		this.redisTemplate = redisTemplate;
	}

	@Override
	public Token createToken(Token token) {

		// String tokenKey = "tokens:" + token.getUserId();
		//
		// // Redis에 토큰 저장
		// try {
		// 	redisTemplate.opsForZSet().add(tokenKey, token, LocalDateTime.now().getNano() );
		// 	// redisTemplate.expire(tokenKey, Duration.ofSeconds(remainingTime));
		// } catch (Exception e) {
		// 	// Redis 저장 실패 시, 데이터베이스 트랜잭션 롤백
		// 	throw new RuntimeException("Redis 저장 중 오류가 발생했습니다.", e);
		// }

		// 기존 DB를 사용한 로직
		try {
			entityManager.persist(token);
			return token;
		} catch (PersistenceException e) {
			if (e.getCause() instanceof ConstraintViolationException) {
				throw new IllegalArgumentException("이미 존재하는 토큰입니다.", e);
			}
			throw e;
		}
		// return token;
	}

	@Override
	public Optional<Token> authenticateToken(String tokenStr) {
		Token token = queryFactory.selectFrom(qToken)
			.where(qToken.token.eq(tokenStr).and(qToken.expiresAt.after(LocalDateTime.now())))
			.fetchOne();
		return Optional.ofNullable(token);
	}

	@Override
	public void expireToken(String tokenStr) {
		int updatedCount = (int)queryFactory.update(qToken)
			.set(qToken.expiresAt, LocalDateTime.now())
			.where(qToken.token.eq(tokenStr).and(qToken.expiresAt.after(LocalDateTime.now())))
			.execute();

		if (updatedCount == 0) {
			CustomException.illegalArgument("만료할 토큰을 찾을 수 없습니다", new IllegalArgumentException(), this.getClass());
		}
	}

	@Override
	public Optional<Token> getTokenByUserId(long userId) {
		Token token = queryFactory.selectFrom(qToken)
			.where(qToken.userId.eq(userId).and(qToken.expiresAt.after(LocalDateTime.now())))
			.fetchOne();
		return Optional.ofNullable(token);
	}
}
