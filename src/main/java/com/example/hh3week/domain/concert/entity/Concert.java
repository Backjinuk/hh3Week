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

	@Builder
	public Concert(long concertId, String concertName, String concertContent) {
		this.concertId = concertId;
		this.concertName = concertName;
		this.concertContent = concertContent;
	}





	public static Concert ToEntity(ConcertDto concertDto) {
		return Concert.builder()
			.concertName(concertDto.getConcertName())
			.concertContent(concertDto.getConcertContent())
			.build();
	}
}
