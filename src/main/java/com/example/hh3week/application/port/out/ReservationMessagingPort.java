package com.example.hh3week.application.port.out;

import java.util.concurrent.CompletableFuture;

import com.example.hh3week.adapter.in.dto.token.TokenDto;

public interface ReservationMessagingPort {

	CompletableFuture<TokenDto> sendReservationRequest(long userId, long seatDetailId);
}
