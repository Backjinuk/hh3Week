package com.example.hh3week.adapter.out.streaming.kafka.dto;

import com.example.hh3week.adapter.in.dto.waitingQueue.WaitingQueueDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TokenIssuedRequest{
	private String correlationId;
	private long userId;
	private long seatDetailId;
	private WaitingQueueDto waitingQueueDto;
}
