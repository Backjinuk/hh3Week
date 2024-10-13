package com.example.hh3week.adapter.in.dto.waitingQueue;

import java.time.LocalDateTime;

import com.example.hh3week.domain.waitingQueue.entity.WaitingQueue;
import com.example.hh3week.domain.waitingQueue.entity.WaitingStatus;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class WaitingQueueDto {
	private long waitingId;

	private long userId;

	private long concertScheduleId;

	private LocalDateTime reservationDt;

	private WaitingStatus waitingStatus;

	private long priority;

	@Builder
	public WaitingQueueDto(long waitingId, long userId, long concertScheduleId, LocalDateTime reservationDt,
		WaitingStatus waitingStatus, long priority) {
		this.waitingId = waitingId;
		this.userId = userId;
		this.concertScheduleId = concertScheduleId;
		this.reservationDt = reservationDt;
		this.waitingStatus = waitingStatus;
		this.priority = priority;
	}



	public static WaitingQueueDto ToDto(WaitingQueue waitingQueue){

		if(waitingQueue == null){
			return null;
		}

		return WaitingQueueDto.builder()
			.waitingId(waitingQueue.getWaitingId())
			.concertScheduleId(waitingQueue.getConcertScheduleId())
			.userId(waitingQueue.getUserId())
			.reservationDt(waitingQueue.getReservationDt())
			.waitingStatus(waitingQueue.getWaitingStatus())
			.priority(waitingQueue.getPriority())
			.build();
	}
}
