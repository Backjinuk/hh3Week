package com.example.hh3week.adapter.in.dto.user;

import java.time.LocalDateTime;

import com.example.hh3week.application.domain.user.entity.PointStatus;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserPointHistoryDto {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long historyId;

	private long userId;

	private long pointAmount;

	private PointStatus pointStatus;

	private LocalDateTime pointDt;
}
