package com.example.hh3week.common.util.scheduler;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.example.hh3week.application.port.out.OutboxEventRepository;
import com.example.hh3week.domain.outBox.ReservationOutBox;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Component
public class ReservationOutboxScheduler {


	private final OutboxEventRepository outboxEventRepository;
	private final KafkaTemplate<String, Object> kafkaTemplate;

	@Autowired
	public ReservationOutboxScheduler(OutboxEventRepository outboxEventRepository, KafkaTemplate<String, Object> kafkaTemplate ) {
		this.outboxEventRepository = outboxEventRepository;
		this.kafkaTemplate = kafkaTemplate;
	}

	@Scheduled(fixedRate = 50000) // 5초마다 실행
	@Transactional
	public void dispatchUnprocessedEvents() {
		// 처리되지 않은 이벤트 조회
		List<ReservationOutBox> unprocessedEvents = outboxEventRepository.findByProcessedFalse();

		for (ReservationOutBox event : unprocessedEvents) {
			try {
				// Kafka로 메시지 발송
				kafkaTemplate.send(event.getAggregateType(), event.getAggregateId(), event.getPayload()).get();

				// 전송 성공 시 Outbox 상태 업데이트
				event.setProcessed(true);
				outboxEventRepository.updateEventOutBox(event);
				log.info("Outbox 이벤트 전송 성공: {}", event.getId());
			} catch (Exception e) {
				log.error("Outbox 이벤트 전송 실패: {}", event.getId(), e);
				// 실패 시 재시도 처리 로직 추가 가능
			}
		}
	}
}
