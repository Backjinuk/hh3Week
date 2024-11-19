package com.example.hh3week.adapter.out.streaming.kafka.adapter;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.example.hh3week.adapter.in.dto.token.TokenDto;
import com.example.hh3week.adapter.in.dto.waitingQueue.WaitingQueueDto;
import com.example.hh3week.adapter.out.streaming.kafka.dto.AddToWaitingQueueResponse;
import com.example.hh3week.adapter.out.streaming.kafka.dto.TokenIssuedRequest;
import com.example.hh3week.adapter.out.streaming.kafka.dto.UserQueueValidationRequest;
import com.example.hh3week.application.port.out.ReservationMessagingPort;
import com.example.hh3week.adapter.out.streaming.kafka.dto.SeatReservationRequest;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ReservationProducerAdapter implements ReservationMessagingPort {
	private final KafkaTemplate<String, Object> kafkaTemplate;
	private final ResponseHolder responseHolder;

	@Value("${kafka.topics.seat-reservations}")
	private String seatReservations;

	@Autowired
	public ReservationProducerAdapter(KafkaTemplate<String, Object> kafkaTemplate, ResponseHolder responseHolder) {
		this.kafkaTemplate = kafkaTemplate;
		this.responseHolder = responseHolder;
	}

	public CompletableFuture<TokenDto> sendReservationRequest(long userId, long seatDetailId) {
		String correlationId = UUID.randomUUID().toString();
		SeatReservationRequest request = new SeatReservationRequest(correlationId, userId, seatDetailId);
		CompletableFuture<TokenDto> future = new CompletableFuture<>();

		// Correlation ID를 키로 사용하여 메시지 전송
		kafkaTemplate.send(seatReservations, correlationId, request);

		// ResponseHolder에 Correlation ID와 Future 매핑
		responseHolder.addResponse(correlationId, future);

		return future;
	}


	public void validateUserInQueue(long userId, long seatDetailId){
		kafkaTemplate.send("user-queue-validation-topic", new UserQueueValidationRequest(userId, seatDetailId));
	}

	@Override
	public void addToWaitingQueueRequest(long userId, long seatDetailId, String message,
		String topic) {

		AddToWaitingQueueResponse response = new AddToWaitingQueueResponse(userId, seatDetailId, message);
		kafkaTemplate.send(topic, response);
	}

	@Override
	public void issuedTokensRequest(long userId, long seatDetailId, WaitingQueueDto waitingQueueDto, String topics) {
		String correlationId = UUID.randomUUID().toString();
		CompletableFuture<TokenDto> future = new CompletableFuture<>();

		TokenIssuedRequest tokenIssuedRequest = new TokenIssuedRequest(userId, seatDetailId, waitingQueueDto);
		kafkaTemplate.send(topics, tokenIssuedRequest);


	}
}
