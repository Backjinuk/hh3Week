package com.example.hh3week.application.port.in;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.example.hh3week.adapter.in.dto.reservation.ReservationSeatDto;
import com.example.hh3week.adapter.in.dto.token.TokenDto;

public interface ReservationUseCase {

	List<ReservationSeatDto> getAvailableReservationSeatList(long concertScheduleId);

	TokenDto reserveSeat(long userId, long seatId);

	CompletableFuture<TokenDto> sendReservationRequest(long userId, long seatId);

}


