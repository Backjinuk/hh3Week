package com.example.hh3week.application.domain.user.entity;

import java.time.LocalDateTime;

import com.example.hh3week.adapter.in.dto.user.UserPointHistoryDto;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class UserPointHistory {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long historyId;

	private long userId;

	private long pointAmount;

	private PointStatus pointStatus;

	private LocalDateTime pointDt;

	@Builder
	public UserPointHistory(long historyId, long userId, long pointAmount, PointStatus pointStatus,
		LocalDateTime pointDt) {
		this.historyId = historyId;
		this.userId = userId;
		this.pointAmount = pointAmount;
		this.pointStatus = pointStatus;
		this.pointDt = pointDt;
	}

	public static UserPointHistory toEntity(UserPointHistoryDto userPointHistoryDto){
		return UserPointHistory.builder()
			.historyId(userPointHistoryDto.getHistoryId())
			.userId(userPointHistoryDto.getUserId())
			.pointAmount(userPointHistoryDto.getPointAmount())
			.pointDt(userPointHistoryDto.getPointDt())
			.build();
	}
}
