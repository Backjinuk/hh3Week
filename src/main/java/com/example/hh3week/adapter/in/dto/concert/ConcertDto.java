package com.example.hh3week.adapter.in.dto.concert;

import java.time.LocalDateTime;
import com.example.hh3week.domain.concert.entity.Concert;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
public class ConcertDto {

	private long concertId;

	private String concertName;

	private String concertContent;

	@Builder
	public ConcertDto(long concertId, String concertName, String concertContent) {
		this.concertId = concertId;
		this.concertName = concertName;
		this.concertContent = concertContent;
	}


	public static ConcertDto ToDto(Concert concert){
		return ConcertDto.builder()
			.concertId(concert.getConcertId())
			.concertName(concert.getConcertName())
			.concertContent(concert.getConcertContent())
			.build();
	}
}
