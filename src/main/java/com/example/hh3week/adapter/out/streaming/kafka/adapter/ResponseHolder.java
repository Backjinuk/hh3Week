package com.example.hh3week.adapter.out.streaming.kafka.adapter;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.stereotype.Component;

import com.example.hh3week.adapter.in.dto.token.TokenDto;

@Component
public class ResponseHolder {

	private final ConcurrentMap<String, CompletableFuture<TokenDto>> responses = new ConcurrentHashMap<>();

	public void addResponse(String correlationId, CompletableFuture<TokenDto> future) {
		responses.put(correlationId, future);
	}

	public CompletableFuture<TokenDto> getResponse(String correlationId) {
		return responses.remove(correlationId);
	}

}
