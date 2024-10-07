package com.example.hh3week.adapter.in.dto.concert;

import java.time.LocalDateTime;

import com.example.hh3week.application.domain.concert.entity.Concert;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ConcertDto {

	private long concertId;

	private String concertName;

	private String concertContent;

	private LocalDateTime startDt;

	private LocalDateTime endDt;

	@Builder
	public ConcertDto(long concertId, String concertName, String concertContent, LocalDateTime startDt,
		LocalDateTime endDt) {
		this.concertId = concertId;
		this.concertName = concertName;
		this.concertContent = concertContent;
		this.startDt = startDt;
		this.endDt = endDt;
	}

	public static ConcertDto toDto(Concert concert){
		return ConcertDto.builder()
			.concertId(concert.getConcertId())
			.concertName(concert.getConcertName())
			.concertContent(concert.getConcertContent())
			.startDt(concert.getStartDt())
			.endDt(concert.getEndDt())
			.build();
	}
}
