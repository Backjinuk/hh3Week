package com.example.hh3week.adapter.out.streaming.kafka.adapter;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.example.hh3week.adapter.in.dto.token.TokenDto;
import com.example.hh3week.adapter.in.dto.waitingQueue.WaitingQueueDto;
import com.example.hh3week.adapter.out.streaming.kafka.dto.AddToWaitingQueueResponse;
import com.example.hh3week.adapter.out.streaming.kafka.dto.TokenIssuedRequest;
import com.example.hh3week.adapter.out.streaming.kafka.dto.TokenIssuedResponse;
import com.example.hh3week.adapter.out.streaming.kafka.dto.UserQueueValidationRequest;
import com.example.hh3week.application.port.out.ReservationMessagingPort;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ReservationProducerAdapter implements ReservationMessagingPort {
	private final KafkaTemplate<String, Object> kafkaTemplate;
	private final ResponseHolder responseHolder;

	@Value("${kafka.topics.user-queue-validation-topic}")
	private String userQueueValidationTopic;


	@Value("${kafka.topics.issued-token-request}")
	private String issuedTokenRequestTopic;

	@Value("${kafka.topics.issued-token-response}")
	private String issuedTokenResponseTopic;

	@Autowired
	public ReservationProducerAdapter(KafkaTemplate<String, Object> kafkaTemplate, ResponseHolder responseHolder) {
		this.kafkaTemplate = kafkaTemplate;
		this.responseHolder = responseHolder;
	}

	@Override
	public CompletableFuture<TokenDto> validateUserInQueue(long userId, long seatDetailId) {
		String correlationId = UUID.randomUUID().toString();
		UserQueueValidationRequest request = new UserQueueValidationRequest(correlationId, userId, seatDetailId);
		CompletableFuture<TokenDto> future = new CompletableFuture<>();

		// Correlation ID를 키로 사용하여 메시지 전송 (올바른 토픽으로 전송)
		kafkaTemplate.send(userQueueValidationTopic, correlationId, request);

		// ResponseHolder에 Correlation ID와 Future 매핑
		responseHolder.addResponse(correlationId, future);

		// 타임아웃 설정 (예: 30초)
		future.orTimeout(30, TimeUnit.SECONDS).whenComplete((result, ex) -> {
			if (ex != null) {
				CompletableFuture<TokenDto> existingFuture = responseHolder.getResponse(correlationId);
				if (existingFuture != null) {
					existingFuture.completeExceptionally(ex);
				}
				log.error("CompletableFuture 타임아웃 또는 예외 발생: correlationId={}, error={}", correlationId, ex.getMessage());
			}
		});

		return future;
	}

	@Override
	public void addToWaitingQueueRequest(String correlationId, long userId, long seatDetailId, String message, String topic) {
		AddToWaitingQueueResponse response = new AddToWaitingQueueResponse(correlationId, userId, seatDetailId, message);
		kafkaTemplate.send(topic, correlationId, response);
	}

	@Override
	public void issuedTokensRequest(String correlationId, long userId, long seatDetailId, WaitingQueueDto waitingQueueDto, String topic) {
		// 전달받은 correlationId를 사용하여 TokenIssuedRequest 전송
		TokenIssuedRequest tokenIssuedRequest = new TokenIssuedRequest(correlationId, userId, seatDetailId, waitingQueueDto);
		kafkaTemplate.send(topic, correlationId, tokenIssuedRequest);
	}

	@Override
	public void issuedTokensResponse(String issuedTokenResponse, String correlationId, TokenIssuedResponse response) {

		kafkaTemplate.send(issuedTokenResponse, correlationId, response);
	}
}
