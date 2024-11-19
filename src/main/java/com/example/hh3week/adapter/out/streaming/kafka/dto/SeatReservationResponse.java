package com.example.hh3week.adapter.out.streaming.kafka.dto;

import com.example.hh3week.adapter.in.dto.token.TokenDto;

import lombok.Data;

@Data
public class SeatReservationResponse {
	private String correlationId;
	private TokenDto token;
	private String error;

	// Constructors
	public SeatReservationResponse() {
	}

	public SeatReservationResponse(String correlationId, TokenDto token, String error) {
		this.correlationId = correlationId;
		this.token = token;
		this.error = error;
	}



}