package com.example.hh3week.adapter.in.dto.waitingQueue;

import java.time.LocalDateTime;

import com.example.hh3week.domain.waitingQueue.entity.WaitingStatus;

import lombok.Data;

@Data
public class WaitingQueueDto {
	private long waitingId;

	private long userId;

	private long concertScheduleId;

	private LocalDateTime reservationDt;

	private WaitingStatus waitingStatus;

	private Long priority;
}
