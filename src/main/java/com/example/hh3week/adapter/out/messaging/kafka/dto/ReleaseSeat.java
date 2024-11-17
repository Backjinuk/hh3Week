package com.example.hh3week.adapter.out.messaging.kafka.dto;

import lombok.Data;

@Data
public class ReleaseSeat {

	private String correlationId;
	private long userId;
	private long seatDetailId;

	public ReleaseSeat() {}

	public ReleaseSeat(String correlationId, long userId, long seatDetailId) {
		this.correlationId = correlationId;
		this.userId = userId;
		this.seatDetailId = seatDetailId;
	}}
