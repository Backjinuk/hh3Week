package com.example.hh3week.domain.reservation;

import lombok.Data;

@Data
public class SeatReservationRequest {
	private String correlationId;
	private long userId;
	private long seatDetailId;

	// Constructors
	public SeatReservationRequest() {}

	public SeatReservationRequest(String correlationId, long userId, long seatDetailId) {
		this.correlationId = correlationId;
		this.userId = userId;
		this.seatDetailId = seatDetailId;
	}

}