package com.example.hh3week.application.port.in;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.example.hh3week.adapter.in.dto.reservation.ReservationSeatDto;
import com.example.hh3week.adapter.in.dto.token.TokenDto;
import com.example.hh3week.adapter.in.dto.waitingQueue.WaitingQueueDto;

public interface ReservationUseCase {

	List<ReservationSeatDto> getAvailableReservationSeatList(long concertScheduleId);

	TokenDto reserveSeat(long userId, long seatId);

	CompletableFuture<TokenDto> sendReservationRequest(long userId, long seatId);

	void validateReservationEligibility(long userId, long seatDetailId);

	WaitingQueueDto addWaitingQueue(long userId, long seatDetailId);

	TokenDto issuedToken(long userId, long seatDetailId, WaitingQueueDto waitingQueueDto);
}


