package com.example.hh3week.adapter.in.dto.reservation;



import com.example.hh3week.domain.reservation.entity.ReservationSeat;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReservationSeatDto {
	private long seatId;

	private long concertId;

	private long maxCapacity;

	private long currentReserved;

	@Builder
	public ReservationSeatDto(long seatId, long concertId, long maxCapacity, long currentReserved) {
		this.seatId = seatId;
		this.concertId = concertId;
		this.maxCapacity = maxCapacity;
		this.currentReserved = currentReserved;
	}

	public static ReservationSeatDto toDto (ReservationSeat reservationSeat){
		return ReservationSeatDto.builder()
			.seatId(reservationSeat.getSeatId())
			.concertId(reservationSeat.getConcertId())
			.maxCapacity(reservationSeat.getMaxCapacity())
			.currentReserved(reservationSeat.getCurrentReserved())
			.build();
	}
}
