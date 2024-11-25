package com.example.hh3week.adapter.out.streaming.kafka.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddToWaitingQueueRequest {

	private String correlationId;
	private long userId;
	private long seatDetailId;
	private String message;
}
