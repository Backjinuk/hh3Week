package com.example.hh3week.domain.concert.entity;

import java.time.LocalDateTime;

import com.example.hh3week.adapter.in.dto.concert.ConcertDto;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)

public class Concert {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long concertId;

	private String concertName;

	private String concertContent;

	private LocalDateTime startDt;

	private LocalDateTime endDt;

	@Builder
	public Concert(long concertId, String concertName, String concertContent, LocalDateTime startDt,
		LocalDateTime endDt) {
		this.concertId = concertId;
		this.concertName = concertName;
		this.concertContent = concertContent;
		this.startDt = startDt;
		this.endDt = endDt;
	}

	public static Concert ToEntity(ConcertDto concertDto) {
		return Concert.builder()
			.concertName(concertDto.getConcertName())
			.concertContent(concertDto.getConcertContent())
			.startDt(concertDto.getStartDt())
			.endDt(concertDto.getEndDt())
			.build();
	}
}
