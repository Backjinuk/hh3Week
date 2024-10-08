package com.example.hh3week.domain.reservation.entity;

import com.example.hh3week.adapter.in.dto.reservation.ReservationSeatDto;

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
public class ReservationSeat {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long seatId;

	private long concertId;

	private long maxCapacity;

	private long currentReserved;

	@Builder
	public ReservationSeat(long seatId, long concertId, long maxCapacity, long currentReserved) {
		this.seatId = seatId;
		this.concertId = concertId;
		this.maxCapacity = maxCapacity;
		this.currentReserved = currentReserved;
	}

	public static ReservationSeat toEntity(ReservationSeatDto reservationSeatDto){
		return ReservationSeat.builder()
			.seatId(reservationSeatDto.getSeatId())
			.concertId(reservationSeatDto.getConcertId())
			.maxCapacity(reservationSeatDto.getMaxCapacity())
			.currentReserved(reservationSeatDto.getCurrentReserved())
			.build();
	}
}
