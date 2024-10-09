package com.example.hh3week.domain.waitingQueue.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.NaturalIdCache;

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
public class WaitingQueue {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long waitingId;

	private long userId;

	private long concertScheduleId;

	private LocalDateTime reservationDt;

	private WaitingStatus waitingStatus;

	private Long priority;

	@Builder
	public WaitingQueue(long waitingId, long userId, long concertScheduleId, LocalDateTime reservationDt,
		WaitingStatus waitingStatus, Long priority) {
		this.waitingId = waitingId;
		this.userId = userId;
		this.concertScheduleId = concertScheduleId;
		this.reservationDt = reservationDt;
		this.waitingStatus = waitingStatus;
		this.priority = priority;
	}
}