package com.example.hh3week.adapter.out.streaming.kafka.dto;

import com.example.hh3week.adapter.in.dto.token.TokenDto;

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


	@Data
	public static class Success {
		private String correlationId; // 추가
		private long userId;
		private long seatDetailId;
		private TokenDto token; // 성공 시 TokenDto 포함

		public Success() { }

		public Success(String correlationId, long userId, long seatDetailId, TokenDto token) {
			this.correlationId = correlationId;
			this.userId = userId;
			this.seatDetailId = seatDetailId;
			this.token = token;
		}


	}

	@Data
	public static class Failure {
		private String correlationId; // 추가
		private long userId;
		private long seatDetailId;
		private String reason;

		public Failure() {}

		public Failure(String correlationId, long userId, long seatDetailId, String reason) {
			this.correlationId = correlationId;
			this.userId = userId;
			this.seatDetailId = seatDetailId;
			this.reason = reason;
		}
	}

}