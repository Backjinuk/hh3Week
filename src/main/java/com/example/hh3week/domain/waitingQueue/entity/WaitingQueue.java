package com.example.hh3week.domain.waitingQueue.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.NaturalIdCache;

import com.example.hh3week.adapter.in.dto.waitingQueue.WaitingQueueDto;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WaitingQueue {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long waitingId;

	private long userId;

	private long concertScheduleId;

	private LocalDateTime reservationDt;

	@Enumerated(EnumType.STRING)
	private WaitingStatus waitingStatus;

	private long priority;

	@Builder
	public WaitingQueue(long waitingId, long userId, long concertScheduleId, LocalDateTime reservationDt,
		WaitingStatus waitingStatus, long priority) {
		this.waitingId = waitingId;
		this.userId = userId;
		this.concertScheduleId = concertScheduleId;
		this.reservationDt = reservationDt;
		this.waitingStatus = waitingStatus;
		this.priority = priority;
	}


	public static WaitingQueue ToEntity(WaitingQueueDto waitingQueueDto){
		return WaitingQueue.builder()
			.waitingId(waitingQueueDto.getWaitingId())
			.userId(waitingQueueDto.getUserId())
			.concertScheduleId(waitingQueueDto.getConcertScheduleId())
			.reservationDt(waitingQueueDto.getReservationDt())
			.waitingStatus(waitingQueueDto.getWaitingStatus())
			.priority(waitingQueueDto.getPriority())
			.build();
	}
}