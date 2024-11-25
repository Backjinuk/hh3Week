// package com.example.hh3week.adapter.out.messaging.kafka.adapter;// CreateTokenFailureListener.java
//
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.kafka.core.KafkaTemplate;
// import org.springframework.kafka.annotation.KafkaListener;
// import org.springframework.stereotype.Service;
//
// import com.example.hh3week.adapter.out.messaging.kafka.dto.CreateTokenRequest;
// import com.example.hh3week.adapter.out.messaging.kafka.dto.ReleaseSeat;
//
// import lombok.extern.slf4j.Slf4j;
//
// @Slf4j
// @Service
// public class CreateTokenFailureListener {
//
// 	private final KafkaTemplate<String, ReleaseSeat> kafkaTemplate;
// 	private final String releaseSeatTopic;
//
// 	@Autowired
// 	public CreateTokenFailureListener(KafkaTemplate<String, ReleaseSeat> kafkaTemplate,
// 		@Value("${kafka.topics.release-seat}") String releaseSeatTopic) {
// 		this.kafkaTemplate = kafkaTemplate;
// 		this.releaseSeatTopic = releaseSeatTopic;
// 	}
//
// 	@KafkaListener(topics = "${kafka.topics.create-token-failure}", groupId = "saga-group")
// 	public void handleCreateTokenFailure(CreateTokenRequest.Failure event) {
//
// 		// 보상 트랜잭션 수행을 위한 ReleaseSeat 이벤트 발행
// 		ReleaseSeat releaseSeat = new ReleaseSeat(event.getCorrelationId(), event.getUserId(), event.getSeatDetailId());
// 		kafkaTemplate.send(releaseSeatTopic, releaseSeat);
// 		log.info("CreateTokenFailure received and ReleaseSeat event sent. CorrelationId={}", event.getCorrelationId());
// 	}
// }
