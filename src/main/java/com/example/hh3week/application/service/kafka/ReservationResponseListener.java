package com.example.hh3week.application.service.kafka;

import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.example.hh3week.adapter.in.dto.token.TokenDto;
import com.example.hh3week.domain.reservation.SeatReservationResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ReservationResponseListener {

	private final ResponseHolder responseHolder;

	@Autowired
	public ReservationResponseListener(ResponseHolder responseHolder) {
		this.responseHolder = responseHolder;
	}

	@KafkaListener(topics = "seat-reservations-response", groupId = "reservation-response-group")
	public void listen(SeatReservationResponse response) {
		String correlationId = response.getCorrelationId();
		CompletableFuture<TokenDto> future = responseHolder.getResponse(correlationId);
		if (future != null) {
			if (response.getError() != null) {
				future.completeExceptionally(new IllegalArgumentException(response.getError()));
			} else {
				future.complete(response.getToken());
			}
			log.info("예약 응답 수신: correlationId={}, token={}, error={}",
				correlationId, response.getToken(), response.getError());
		} else {
			log.warn("응답을 찾을 수 없습니다: correlationId={}", correlationId);
		}
	}
}
