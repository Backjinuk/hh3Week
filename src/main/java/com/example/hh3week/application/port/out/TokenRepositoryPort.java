package com.example.hh3week.application.port.out;

import java.util.Optional;

import com.example.hh3week.domain.token.entity.Token;

public interface TokenRepositoryPort {
	Token createToken(Token token);

	Optional<Token> authenticateToken(String token);

	void expireToken(String token);

	Optional<Token> getTokenByUserId(long userId);
}
