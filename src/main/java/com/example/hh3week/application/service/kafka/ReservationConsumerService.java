package com.example.hh3week.application.service.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.example.hh3week.application.port.in.ReservationUseCase;
import com.example.hh3week.domain.reservation.SeatReservationRequest;
import com.example.hh3week.domain.reservation.SeatReservationResponse;
import com.example.hh3week.adapter.in.dto.token.TokenDto;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ReservationConsumerService {
	private final ReservationUseCase reservationUseCase;
	private final KafkaTemplate<String, SeatReservationResponse> responseKafkaTemplate;
	private final String responseTopic = "seat-reservations-response";

	@Autowired
	public ReservationConsumerService(ReservationUseCase reservationUseCase,
		KafkaTemplate<String, SeatReservationResponse> responseKafkaTemplate) {
		this.reservationUseCase = reservationUseCase;
		this.responseKafkaTemplate = responseKafkaTemplate;
	}

	@KafkaListener(topics = "seat-reservations", groupId = "reservation-group")
	@Transactional
	public void listen(SeatReservationRequest request) {
		String correlationId = request.getCorrelationId();
		try {
			TokenDto token = reservationUseCase.reserveSeat(request.getUserId(), request.getSeatDetailId());
			// 예약 성공 시, 응답 메시지 생성 및 발행
			SeatReservationResponse response = new SeatReservationResponse(correlationId, token, null);
			responseKafkaTemplate.send(responseTopic, correlationId, response);
			log.info("예약 처리 완료: correlationId={}, userId={}, seatDetailId={}",
				correlationId, request.getUserId(), request.getSeatDetailId());
		} catch (Exception e) {
			log.error("예약 처리 실패: correlationId={}, userId={}, seatDetailId={}, error={}",
				correlationId, request.getUserId(), request.getSeatDetailId(), e.getMessage());
			// 예약 실패 시, 에러 메시지 발행
			SeatReservationResponse response = new SeatReservationResponse(correlationId, null, e.getMessage());
			responseKafkaTemplate.send(responseTopic, correlationId, response);
		}
	}
}
