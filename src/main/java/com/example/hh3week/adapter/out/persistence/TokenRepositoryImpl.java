package com.example.hh3week.adapter.out.persistence;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.example.hh3week.application.port.out.TokenRepositoryPort;
import com.example.hh3week.domain.token.entity.Token;

@Repository
public class TokenRepositoryImpl implements TokenRepositoryPort {
	@Override
	public Token createToken(Token token) {
		return null;
	}

	@Override
	public Optional<Token> authenticateToken(String token) {
		return Optional.empty();
	}

	@Override
	public void expireToken(String token) {

	}

	@Override
	public Optional<Token> getTokenByUserId(long userId) {
		return Optional.empty();
	}
}
