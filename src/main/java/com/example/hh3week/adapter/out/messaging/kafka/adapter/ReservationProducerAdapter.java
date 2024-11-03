package com.example.hh3week.adapter.out.messaging.kafka.adapter;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.example.hh3week.adapter.in.dto.token.TokenDto;
import com.example.hh3week.application.port.out.ReservationMessagingPort;
import com.example.hh3week.adapter.out.messaging.kafka.dto.SeatReservationRequest;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ReservationProducerAdapter implements ReservationMessagingPort {
	private final KafkaTemplate<String, SeatReservationRequest> kafkaTemplate;
	private final String requestTopic = "seat-reservations";
	private final ResponseHolder responseHolder;

	@Autowired
	public ReservationProducerAdapter(KafkaTemplate<String, SeatReservationRequest> kafkaTemplate,
		ResponseHolder responseHolder) {
		this.kafkaTemplate = kafkaTemplate;
		this.responseHolder = responseHolder;
	}

	public CompletableFuture<TokenDto> sendReservationRequest(long userId, long seatDetailId) {
		String correlationId = UUID.randomUUID().toString();
		SeatReservationRequest request = new SeatReservationRequest(correlationId, userId, seatDetailId);
		CompletableFuture<TokenDto> future = new CompletableFuture<>();

		// Correlation ID를 키로 사용하여 메시지 전송
		kafkaTemplate.send(requestTopic, correlationId, request);
		log.info("예약 요청 전송: correlationId={}, userId={}, seatDetailId={}", correlationId, userId, seatDetailId);

		// ResponseHolder에 Correlation ID와 Future 매핑
		responseHolder.addResponse(correlationId, future);

		return future;
	}
}
