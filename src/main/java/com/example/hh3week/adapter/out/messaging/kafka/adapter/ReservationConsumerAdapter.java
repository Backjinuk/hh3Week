// package com.example.hh3week.adapter.out.messaging.kafka.adapter;
//
// import java.util.concurrent.CompletableFuture;
//
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.kafka.annotation.KafkaListener;
// import org.springframework.kafka.core.KafkaTemplate;
// import org.springframework.stereotype.Service;
//
// import com.example.hh3week.adapter.in.dto.token.TokenDto;
// import com.example.hh3week.adapter.out.streaming.kafka.adapter.ResponseHolder;
// import com.example.hh3week.adapter.out.streaming.kafka.dto.SeatReservationRequest;
// import com.example.hh3week.adapter.out.streaming.kafka.dto.SeatReservationResponse;
// import com.example.hh3week.application.port.in.ReservationUseCase;
//
// import lombok.extern.slf4j.Slf4j;
//
// @Slf4j
// @Service
// public class ReservationConsumerAdapter {
//
// 	private final ReservationUseCase reservationUseCase;
// 	private final KafkaTemplate<String, SeatReservationResponse> responseKafkaTemplate;
// 	private final ResponseHolder responseHolder;
//
//
// 	@Value("${kafka.topics.seat-reservations-response}")
// 	private String responseTopic;
//
// 	@Autowired
// 	public ReservationConsumerAdapter(ReservationUseCase reservationUseCase, KafkaTemplate<String, SeatReservationResponse> responseKafkaTemplate, ResponseHolder responseHolder) {
// 		this.reservationUseCase = reservationUseCase;
// 		this.responseKafkaTemplate = responseKafkaTemplate;
// 		this.responseHolder = responseHolder;
// 	}
//
// 	@KafkaListener(topics = "${kafka.topics.seat-reservations-response}", groupId = "reservation-group")
// 	public void consumeReservationRequest(SeatReservationRequest request) {
// 		String correlationId = request.getCorrelationId();
// 		long userId = request.getUserId();
// 		long seatDetailId = request.getSeatDetailId();
//
// 		try {
// 			// 예약 처리
// 			TokenDto tokenDto = reservationUseCase.reserveSeat(userId, seatDetailId);
//
// 			// 성공 응답 생성
// 			SeatReservationResponse response = new SeatReservationResponse(correlationId, tokenDto, null);
// 			responseKafkaTemplate.send(responseTopic, correlationId, response);
//
// 		} catch (Exception e) {
//
// 			// 실패 응답 생성
// 			SeatReservationResponse response = new SeatReservationResponse(correlationId, null, e.getMessage());
// 			responseKafkaTemplate.send(responseTopic, correlationId, response);
// 		}
// 	}
//
//
//
// 	@KafkaListener(topics = "${kafka.topics.reserve-seat-success}", groupId = "reservation-group")
// 	public void handleReserveSeatSuccess(SeatReservationRequest.Success event) {
// 		String correlationId = event.getCorrelationId();
// 		CompletableFuture<TokenDto> future = responseHolder.getResponse(correlationId);
// 		if (future != null) {
// 			future.complete(event.getToken());
// 			log.info("ReserveSeatSuccess received and future completed. CorrelationId={}", correlationId);
// 		} else {
// 			log.warn("No matching future found for ReserveSeatSuccess. CorrelationId={}", correlationId);
// 		}
// 	}
//
// 	@KafkaListener(topics = "${kafka.topics.reserve-seat-failure}", groupId = "reservation-group")
// 	public void handleReserveSeatFailure(SeatReservationRequest.Failure event) {
// 		String correlationId = event.getCorrelationId();
// 		CompletableFuture<TokenDto> future = responseHolder.getResponse(correlationId);
// 		if (future != null) {
// 			future.completeExceptionally(new RuntimeException(event.getReason()));
// 			log.info("ReserveSeatFailure received and future completed exceptionally. CorrelationId={}", correlationId);
// 		} else {
// 			log.warn("No matching future found for ReserveSeatFailure. CorrelationId={}", correlationId);
// 		}
// 	}
//
// }
