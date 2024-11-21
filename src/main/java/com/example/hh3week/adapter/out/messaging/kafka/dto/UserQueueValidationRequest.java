package com.example.hh3week.adapter.out.messaging.kafka.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserQueueValidationRequest {
	private String correlationId;
	private long userId;
	private long seatDetailId;
}
