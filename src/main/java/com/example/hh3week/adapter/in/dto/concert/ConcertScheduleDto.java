package com.example.hh3week.adapter.in.dto.concert;

import java.time.LocalDateTime;

import com.example.hh3week.application.domain.concert.entity.ConcertSchedule;
import com.example.hh3week.application.domain.concert.entity.ConcertScheduleStatus;

import jakarta.persistence.Entity;
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
public class ConcertScheduleDto {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long concertScheduleId;

	private long concertId;

	private ConcertScheduleStatus concertScheduleStatus;

	private long concertPrice;

	@Builder
	public ConcertScheduleDto(long concertScheduleId, long concertId , ConcertScheduleStatus concertScheduleStatus, long concertPrice) {
		this.concertScheduleId = concertScheduleId;
		this.concertId = concertId;
		this.concertScheduleStatus = concertScheduleStatus;
		this.concertPrice = concertPrice;
	}

	public static ConcertScheduleDto toDto(ConcertSchedule concertSchedule){
		return ConcertScheduleDto.builder()
			.concertScheduleId(concertSchedule.getConcertScheduleId())
			.concertId(concertSchedule.getConcertId())
			.concertScheduleStatus(concertSchedule.getConcertScheduleStatus())
			.concertPrice(concertSchedule.getConcertPrice())
			.build();
	}
}
