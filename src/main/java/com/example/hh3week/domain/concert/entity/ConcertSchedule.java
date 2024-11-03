package com.example.hh3week.domain.concert.entity;

import java.time.LocalDateTime;

import com.example.hh3week.adapter.in.dto.concert.ConcertScheduleDto;

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
public class ConcertSchedule {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long concertScheduleId;

	private long concertId;

	@Enumerated(EnumType.STRING)
	private ConcertScheduleStatus concertScheduleStatus;

	private long concertPrice;

	private LocalDateTime startDt;

	private LocalDateTime endDt;

	// @Version
	// private long version;

	@Builder
	public ConcertSchedule(long concertScheduleId, long concertId, ConcertScheduleStatus concertScheduleStatus,
		long concertPrice, LocalDateTime startDt, LocalDateTime endDt) {
		this.concertScheduleId = concertScheduleId;
		this.concertId = concertId;
		this.concertScheduleStatus = concertScheduleStatus;
		this.concertPrice = concertPrice;
		this.startDt = startDt;
		this.endDt = endDt;
	}

	public static ConcertSchedule ToEntity (ConcertScheduleDto concertScheduleDto){
		return ConcertSchedule.builder()
			.concertScheduleId(concertScheduleDto.getConcertScheduleId())
			.concertId(concertScheduleDto.getConcertId())
			.concertScheduleStatus(concertScheduleDto.getConcertScheduleStatus())
			.concertPrice(concertScheduleDto.getConcertPrice())
			.startDt(concertScheduleDto.getStartDt())
			.endDt(concertScheduleDto.getEndDt())
			.build();
	}
}
