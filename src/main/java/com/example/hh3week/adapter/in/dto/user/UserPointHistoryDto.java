package com.example.hh3week.adapter.in.dto.user;

import java.time.LocalDateTime;

import com.example.hh3week.domain.user.entity.PointStatus;
import com.example.hh3week.domain.user.entity.UserPointHistory;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
public class UserPointHistoryDto {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long historyId;

	private long userId;

	private long pointAmount;

	private PointStatus pointStatus;

	private LocalDateTime pointDt;

	@Builder
	public UserPointHistoryDto(long historyId, long userId, long pointAmount, PointStatus pointStatus,
		LocalDateTime pointDt) {
		this.historyId = historyId;
		this.userId = userId;
		this.pointAmount = pointAmount;
		this.pointStatus = pointStatus;
		this.pointDt = pointDt;
	}

	public static UserPointHistoryDto toDto(UserPointHistory userPointHistory) {
		return UserPointHistoryDto
			.builder()
			.historyId(userPointHistory.getHistoryId())
			.userId(userPointHistory.getUserId())
			.pointAmount(userPointHistory.getPointAmount())
			.pointStatus(userPointHistory.getPointStatus())
			.pointDt(userPointHistory.getPointDt())
			.build();
	}
}
