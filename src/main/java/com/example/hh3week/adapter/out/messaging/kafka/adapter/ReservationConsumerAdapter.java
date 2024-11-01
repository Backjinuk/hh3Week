package com.example.hh3week.adapter.out.messaging.kafka.adapter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.example.hh3week.adapter.in.dto.token.TokenDto;
import com.example.hh3week.application.port.in.ReservationUseCase;
import com.example.hh3week.adapter.out.messaging.kafka.dto.SeatReservationRequest;
import com.example.hh3week.adapter.out.messaging.kafka.dto.SeatReservationResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ReservationConsumerAdapter {

	private final ReservationUseCase reservationUseCase;
	private final KafkaTemplate<String, SeatReservationResponse> responseKafkaTemplate;
	private final String responseTopic = "seat-reservations-response";

	@Autowired
	public ReservationConsumerAdapter(ReservationUseCase reservationUseCase,
		KafkaTemplate<String, SeatReservationResponse> responseKafkaTemplate) {
		this.reservationUseCase = reservationUseCase;
		this.responseKafkaTemplate = responseKafkaTemplate;
	}

	@KafkaListener(topics = "seat-reservations", groupId = "reservation-group")
	public void consumeReservationRequest(SeatReservationRequest request) {
		String correlationId = request.getCorrelationId();
		long userId = request.getUserId();
		long seatDetailId = request.getSeatDetailId();

		log.info("Kafka 메시지 수신: correlationId={}, userId={}, seatDetailId={}", correlationId, userId, seatDetailId);

		try {
			// 예약 처리
			TokenDto tokenDto = reservationUseCase.reserveSeat(userId, seatDetailId);

			// 성공 응답 생성
			SeatReservationResponse response = new SeatReservationResponse(correlationId, tokenDto, null);
			responseKafkaTemplate.send(responseTopic, correlationId, response);
			log.info("예약 처리 완료: correlationId={}, userId={}, seatDetailId={}", correlationId, userId, seatDetailId);
		} catch (Exception e) {
			log.error("예약 처리 실패: correlationId={}, userId={}, seatDetailId={}, error={}",
				correlationId, userId, seatDetailId, e.getMessage());

			// 실패 응답 생성
			SeatReservationResponse response = new SeatReservationResponse(correlationId, null, e.getMessage());
			responseKafkaTemplate.send(responseTopic, correlationId, response);
		}
	}
}
