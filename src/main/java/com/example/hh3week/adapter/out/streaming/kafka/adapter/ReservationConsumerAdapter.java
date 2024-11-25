package com.example.hh3week.adapter.out.streaming.kafka.adapter;

import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.example.hh3week.adapter.in.dto.token.TokenDto;
import com.example.hh3week.adapter.in.dto.waitingQueue.WaitingQueueDto;
import com.example.hh3week.adapter.out.streaming.kafka.dto.AddToWaitingQueueResponse;
import com.example.hh3week.adapter.out.streaming.kafka.dto.SeatReservationRequest;
import com.example.hh3week.adapter.out.streaming.kafka.dto.TokenIssuedRequest;
import com.example.hh3week.adapter.out.streaming.kafka.dto.TokenIssuedResponse;
import com.example.hh3week.adapter.out.streaming.kafka.dto.UserQueueValidationRequest;
import com.example.hh3week.adapter.out.streaming.kafka.dto.UserQueueValidationResponse;
import com.example.hh3week.application.port.in.ReservationUseCase;
import com.example.hh3week.application.port.out.OutboxEventRepository;
import com.example.hh3week.application.port.out.ReservationMessagingPort;
import com.example.hh3week.domain.outBox.ReservationOutBox;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ReservationConsumerAdapter {

	private final ReservationUseCase reservationUseCase;
	private final ResponseHolder responseHolder;
	private final ReservationMessagingPort reservationMessagingPort;
	private final OutboxEventRepository outboxEventRepository;

	@Value("${kafka.topics.seat-reservations-response}")
	private String seatReservationsResponse;




	@Autowired
	public ReservationConsumerAdapter(ReservationUseCase reservationUseCase, ResponseHolder responseHolder, ReservationMessagingPort reservationMessagingPort,
		OutboxEventRepository outboxEventRepository) {
		this.reservationUseCase = reservationUseCase;
		this.responseHolder = responseHolder;
		this.reservationMessagingPort = reservationMessagingPort;
		this.outboxEventRepository = outboxEventRepository;
	}

	@Transactional
	@KafkaListener(topics = "${kafka.topics.reservation-out-box-request}", groupId="outBox-group")
	public void consumerReservationOutBoxRequest(ReservationOutBox reservationOutBox){

		//out box 테이블 저장
		outboxEventRepository.addReservationOutBox(reservationOutBox);
	}

	@KafkaListener(topics = "${kafka.topics.user-queue-validation-topic}", groupId = "reservation-group")
	public void consumeUserQueueValidationRequest(UserQueueValidationRequest request){
		long userId = request.getUserId();
		long seatDetailId = request.getSeatDetailId();
		String correlationId = request.getCorrelationId();

		try {
			// 예약 가능성 검증 로직 수행
			reservationUseCase.validateReservationEligibility(userId, seatDetailId);

			// 대기열에 추가 성공 메시지 전송
			String message = "성공적으로 대기열에 추가되었습니다.";
			reservationMessagingPort.addToWaitingQueueRequest(correlationId, userId, seatDetailId, message);

		} catch (Exception e) {
			log.error("대기열에 추가 실패: correlationId={}, userId={}, seatDetailId={}, error={}", correlationId, userId, seatDetailId, e.getMessage());
			String message = "대기열에 추가 실패: " + e.getMessage();
			reservationMessagingPort.addToWaitingQueueRequest(correlationId, userId, seatDetailId, message);
		}
	}


	@KafkaListener(topics = "${kafka.topics.add-waiting-queue-response}", groupId="reservation-group")
	public void consumeAddWaitingQueueResponse(AddToWaitingQueueResponse response){
		String correlationId = response.getCorrelationId();
		long userId = response.getUserId();
		long seatDetailId = response.getSeatDetailId();

		// 대기열에 추가 후 토큰 발급
		WaitingQueueDto waitingQueueDto = reservationUseCase.addWaitingQueue(userId, seatDetailId);
		reservationMessagingPort.issuedTokensRequest(correlationId, userId, seatDetailId, waitingQueueDto);
	}



	@KafkaListener(topics = "${kafka.topics.issued-token-request}", groupId ="reservation-group")
	public void consumeIssuedTokenRequest(TokenIssuedRequest tokenIssuedRequest){
		String correlationId = tokenIssuedRequest.getCorrelationId();
		long userId = tokenIssuedRequest.getUserId();
		long seatDetailId = tokenIssuedRequest.getSeatDetailId();
		WaitingQueueDto waitingQueueDto = tokenIssuedRequest.getWaitingQueueDto();

		try {
			// 토큰 발급 로직 수행
			TokenDto tokenDto = reservationUseCase.issuedToken(userId, seatDetailId, waitingQueueDto);

			// 토큰 발급 응답 생성 및 전송
			TokenIssuedResponse response = new TokenIssuedResponse(correlationId, tokenDto, null);
			reservationMessagingPort.issuedTokensResponse(correlationId, response);


		} catch (Exception e) {
			// 오류 발생 시 토큰 발급 실패 응답 전송
			TokenIssuedResponse response = new TokenIssuedResponse(correlationId, null, e.getMessage());
			reservationMessagingPort.issuedTokensResponse(correlationId, response);
		}
	}

	@KafkaListener(topics = "${kafka.topics.issued-token-response}", groupId ="reservation-group")
	public void consumeIssuedTokenResponse(TokenIssuedResponse response){
		String correlationId = response.getCorrelationId();
		TokenDto tokenDto = response.getTokenDto();
		String errorMessage = response.getMessage();


		// // Outbox 이벤트를 찾아 상태 업데이트
		// ReservationOutBox event = outboxEventRepository.findByAggregateId(correlationId);
		//
		// if (event != null) {
		// 	if (tokenDto != null) {
		// 		// 토큰 발급 성공 시
		// 		event.setProcessed(true); // 성공적으로 처리된 이벤트
		// 	} else {
		// 		// 실패 시, 적절한 오류 처리
		// 		event.setProcessed(false); // 실패한 경우도 처리 완료로 설정할 수 있음 (재시도 로직 구현 가능)
		// 		throw new IllegalArgumentException("OutBot Error 토큰 발행 실패");
		// 	}
		// 	outboxEventRepository.save(event);
		// }


		CompletableFuture<TokenDto> future = responseHolder.getResponse(correlationId);
		if (future != null) {
			if (tokenDto != null) {
				future.complete(tokenDto);
			} else {
				future.completeExceptionally(new IllegalArgumentException(errorMessage));
			}
		} else {
			log.warn("correlationId={}에 대한 CompletableFuture를 찾을 수 없습니다.", correlationId);
		}
	}



}
