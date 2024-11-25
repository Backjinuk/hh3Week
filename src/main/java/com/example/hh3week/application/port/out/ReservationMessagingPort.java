package com.example.hh3week.application.port.out;

import java.util.concurrent.CompletableFuture;

import com.example.hh3week.adapter.in.dto.token.TokenDto;
import com.example.hh3week.adapter.in.dto.waitingQueue.WaitingQueueDto;
import com.example.hh3week.adapter.out.streaming.kafka.dto.TokenIssuedResponse;

public interface ReservationMessagingPort {

	// CompletableFuture<TokenDto> sendReservationRequest(long userId, long seatDetailId);

	CompletableFuture<TokenDto> validateUserInQueue(long userId, long seatDetailId);

	void addToWaitingQueueRequest(String correlationId, long userId, long seatDetailId, String message);

	void issuedTokensRequest(String correlationId,long userId, long seatDetailId, WaitingQueueDto waitingQueueDto);

	void issuedTokensResponse(String correlationId, TokenIssuedResponse response);
}
