package com.example.hh3week.application.port.out;

import java.util.concurrent.CompletableFuture;

import com.example.hh3week.adapter.in.dto.token.TokenDto;
import com.example.hh3week.adapter.in.dto.waitingQueue.WaitingQueueDto;

public interface ReservationMessagingPort {

	CompletableFuture<TokenDto> sendReservationRequest(long userId, long seatDetailId);

	void addToWaitingQueueRequest(long userId, long seatDetailId, String message, String addWaitingQueueResponse);


	void issuedTokensRequest(long userId, long seatDetailId, WaitingQueueDto waitingQueueDto, String topics);
}
