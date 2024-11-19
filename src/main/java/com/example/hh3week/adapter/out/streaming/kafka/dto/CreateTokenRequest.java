package com.example.hh3week.adapter.out.messaging.kafka.dto;

import com.example.hh3week.adapter.in.dto.token.TokenDto;

import lombok.Data;

@Data
public class CreateTokenRequest {

	private String correlationId; // 추가
	private long userId;
	private int queuePosition;
	private long remainingTime;
	private long seatDetailId;

	public CreateTokenRequest() {}

	public CreateTokenRequest(String correlationId, long userId, int queuePosition, long remainingTime, long seatDetailId) {
		this.correlationId = correlationId;
		this.userId = userId;
		this.queuePosition = queuePosition;
		this.remainingTime = remainingTime;
		this.seatDetailId = seatDetailId;
	}

	@Data
	public static class Success {
		private String correlationId;
		private long userId;
		private long seatDetailId;
		private TokenDto token;

		public Success() {}

		public Success(String correlationId, long userId, long seatDetailId, TokenDto token) {
			this.correlationId = correlationId;
			this.userId = userId;
			this.seatDetailId = seatDetailId;
			this.token = token;
		}
	}

	@Data
	public static class Failure{
		private String correlationId;
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
