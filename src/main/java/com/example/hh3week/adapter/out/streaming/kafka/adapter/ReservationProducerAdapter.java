package com.example.hh3week.adapter.out.streaming.kafka.adapter;

import java.time.LocalDateTime;
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
import com.example.hh3week.application.port.out.OutboxEventRepository;
import com.example.hh3week.application.port.out.ReservationMessagingPort;
import com.example.hh3week.domain.outBox.ReservationOutBox;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ReservationProducerAdapter implements ReservationMessagingPort {
	private final KafkaTemplate<String, Object> kafkaTemplate;
	private final ResponseHolder responseHolder;
	private final ObjectMapper objectMapper;
	private final OutboxEventRepository outboxEventRepository;

	@Value("${kafka.topics.user-queue-validation-topic}")
	private String userQueueValidationTopic;

	@Value("${kafka.topics.add-waiting-queue-response}")
	private String addWaitingQueueResponse;

	@Value("${kafka.topics.issued-token-request}")
	private String issuedTokenRequest;

	@Value("${kafka.topics.issued-token-response}")
	private String issuedTokenResponse;

	@Value("${kafka.topics.reservation-out-box-request}")
	private String reservationOutBoxRequest;

	@Autowired
	public ReservationProducerAdapter(KafkaTemplate<String, Object> kafkaTemplate, ResponseHolder responseHolder,
		ObjectMapper objectMapper, OutboxEventRepository outboxEventRepository) {
		this.kafkaTemplate = kafkaTemplate;
		this.responseHolder = responseHolder;
		this.objectMapper = objectMapper;
		this.outboxEventRepository = outboxEventRepository;
	}

	@Override
	public CompletableFuture<TokenDto> validateUserInQueue(long userId, long seatDetailId) {
		String correlationId = UUID.randomUUID().toString();
		UserQueueValidationRequest request = new UserQueueValidationRequest(correlationId, userId, seatDetailId);
		CompletableFuture<TokenDto> future = new CompletableFuture<>();

		try {

			// 이벤트를 Outbox 테이블에 저장
			String payload = objectMapper.writeValueAsString(request);
			ReservationOutBox event = ReservationOutBox.builder()
				.aggregateId(correlationId)
				.aggregateType(userQueueValidationTopic)
				.type("UserQueueValidationRequest")
				.payload(payload)
				.createdAt(LocalDateTime.now())
				.processed(false)
				.build();

			kafkaTemplate.send(reservationOutBoxRequest, event);

			// Kafka로 전송
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
					log.error("CompletableFuture 타임아웃 또는 예외 발생: correlationId={}, error={}", correlationId,
						ex.getMessage());
				}
			});
		} catch (Exception e) {
			log.error("Outbox 이벤트 저장 실패: {}", e.getMessage());
			future.completeExceptionally(e);
		}

		return future;
	}

	@Override
	public void addToWaitingQueueRequest(String correlationId, long userId, long seatDetailId, String message) {
		AddToWaitingQueueResponse response = new AddToWaitingQueueResponse(correlationId, userId, seatDetailId,
			message);
		kafkaTemplate.send(addWaitingQueueResponse, correlationId, response);
	}

	@Override
	public void issuedTokensRequest(String correlationId, long userId, long seatDetailId,
		WaitingQueueDto waitingQueueDto) {
		// 전달받은 correlationId를 사용하여 TokenIssuedRequest 전송
		TokenIssuedRequest tokenIssuedRequest = new TokenIssuedRequest(correlationId, userId, seatDetailId,
			waitingQueueDto);
		kafkaTemplate.send(issuedTokenRequest, correlationId, tokenIssuedRequest);
	}

	@Override
	public void issuedTokensResponse(String correlationId, TokenIssuedResponse response) {

		kafkaTemplate.send(issuedTokenResponse, correlationId, response);
	}



}
