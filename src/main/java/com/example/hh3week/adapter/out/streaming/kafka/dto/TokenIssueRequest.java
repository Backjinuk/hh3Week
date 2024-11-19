package com.example.hh3week.adapter.out.streaming.kafka.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TokenIssueRequest {
	private long userId;
	private long queuePosition;
	private long remainingTime;
	private long seatDetailId;

}
