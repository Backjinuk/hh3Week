package com.example.hh3week.adapter.out.streaming.kafka.adapter;

import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.example.hh3week.adapter.in.dto.token.TokenDto;
import com.example.hh3week.adapter.in.dto.waitingQueue.WaitingQueueDto;
import com.example.hh3week.adapter.out.streaming.kafka.dto.AddToWaitingQueueResponse;
import com.example.hh3week.adapter.out.streaming.kafka.dto.SeatReservationRequest;
import com.example.hh3week.adapter.out.streaming.kafka.dto.UserQueueValidationRequest;
import com.example.hh3week.application.port.in.ReservationUseCase;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ReservationConsumerAdapter {

	private final ReservationUseCase reservationUseCase;
	private final KafkaTemplate<String, Object> kafkaTemplate;
	private final ResponseHolder responseHolder;
	private final String responseTopic = "seat-reservations-response";

	@Autowired
	public ReservationConsumerAdapter(ReservationUseCase reservationUseCase,
		KafkaTemplate<String, Object> kafkaTemplate, ResponseHolder responseHolder) {
		this.reservationUseCase = reservationUseCase;
		this.kafkaTemplate = kafkaTemplate;
		this.responseHolder = responseHolder;
	}

	@KafkaListener(topics = "seat-reservations", groupId = "reservation-group")
	public void consumeReservationRequest(SeatReservationRequest request) {
		String correlationId = request.getCorrelationId();
		long userId = request.getUserId();
		long seatDetailId = request.getSeatDetailId();

		try {
			// 예약 처리
			TokenDto tokenDto = reservationUseCase.reserveSeat(userId, seatDetailId);

			// `CompletableFuture`를 통해 응답을 처리
			CompletableFuture<TokenDto> future = responseHolder.getResponse(correlationId);
			if (future != null) {
				future.complete(tokenDto); // 성공 시 `tokenDto` 완료
			} else {
				log.warn("응답을 찾을 수 없습니다: correlationId={}", correlationId);
			}

			// // 성공 응답 생성
			// SeatReservationResponse response = new SeatReservationResponse(correlationId, tokenDto, null);
			// responseKafkaTemplate.send(responseTopic, correlationId, response);
		} catch (Exception e) {

			// `CompletableFuture`에 예외를 완료 처리
			CompletableFuture<TokenDto> future = responseHolder.getResponse(correlationId);
			if (future != null) {
				future.completeExceptionally(new IllegalArgumentException(e.getMessage()));
			}

			// // 실패 응답 생성
			// SeatReservationResponse response = new SeatReservationResponse(correlationId, null, e.getMessage());
			// responseKafkaTemplate.send(responseTopic, correlationId, response);
		}
	}


	@KafkaListener(topics = "user-queue-validation-topic", groupId = "reservation-group")
	public void consumeUserQueueValidationRequest(UserQueueValidationRequest request){
		long userId = request.getUserId();
		long seatDetailId = request.getSeatDetailId();

		try{

			//로직 수행
			reservationUseCase.validateReservationEligibility(userId, seatDetailId);

			//이벤트 발행
			AddToWaitingQueueResponse response = new AddToWaitingQueueResponse(userId, seatDetailId, "성공적으로 대기열에 추가되었습니다.");
			kafkaTemplate.send("add-waiting-queue-response", response);
		}catch (Exception e){

			AddToWaitingQueueResponse response = new AddToWaitingQueueResponse(userId, seatDetailId, "대기열에 추가 실패" + e.getMessage());
			kafkaTemplate.send("add-waiting-queue-response", response);

		}

	}

	@KafkaListener(topics = "add-waiting-queue-response", groupId="reservation-group")
	public void consumeAddWaitingQueueResponse(AddToWaitingQueueResponse response){
		long userId = response.getUserId();
		long seatDetailId = response.getSeatDetailId();

		WaitingQueueDto waitingQueueDto = reservationUseCase.addWaitingQueue(userId, seatDetailId);

		kafkaTemplate.send("", waitingQueueDto);

	}

}
