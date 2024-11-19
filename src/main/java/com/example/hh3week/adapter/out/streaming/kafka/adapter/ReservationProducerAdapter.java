package com.example.hh3week.adapter.out.streaming.kafka.adapter;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.example.hh3week.adapter.in.dto.token.TokenDto;
import com.example.hh3week.adapter.out.streaming.kafka.dto.UserQueueValidationRequest;
import com.example.hh3week.application.port.out.ReservationMessagingPort;
import com.example.hh3week.adapter.out.streaming.kafka.dto.SeatReservationRequest;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ReservationProducerAdapter implements ReservationMessagingPort {
	private final KafkaTemplate<String, SeatReservationRequest> kafkaTemplate;
	private final KafkaTemplate<String, UserQueueValidationRequest> kafkaTemplate2;
	private final String requestTopic = "seat-reservations";
	private final ResponseHolder responseHolder;

	@Autowired
	public ReservationProducerAdapter(KafkaTemplate<String, SeatReservationRequest> kafkaTemplate,
		KafkaTemplate<String, UserQueueValidationRequest> kafkaTemplate2,
		ResponseHolder responseHolder) {
		this.kafkaTemplate = kafkaTemplate;
		this.kafkaTemplate2 = kafkaTemplate2;
		this.responseHolder = responseHolder;
	}

	public CompletableFuture<TokenDto> sendReservationRequest(long userId, long seatDetailId) {
		String correlationId = UUID.randomUUID().toString();
		SeatReservationRequest request = new SeatReservationRequest(correlationId, userId, seatDetailId);
		CompletableFuture<TokenDto> future = new CompletableFuture<>();

		// Correlation ID를 키로 사용하여 메시지 전송
		kafkaTemplate.send(requestTopic, correlationId, request);

		// ResponseHolder에 Correlation ID와 Future 매핑
		responseHolder.addResponse(correlationId, future);

		return future;
	}



	public void validateUserInQueue(long userId, long seatDetailId){
		kafkaTemplate2.send("user-queue-validation-topic", new UserQueueValidationRequest(userId, seatDetailId));
	}


}
