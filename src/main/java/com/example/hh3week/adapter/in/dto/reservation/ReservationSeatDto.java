package com.example.hh3week.adapter.in.dto.reservation;



import java.util.List;

import com.example.hh3week.domain.reservation.entity.ReservationSeat;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
public class ReservationSeatDto {
	private long seatId;

	private long concertId;

	private long maxCapacity;

	private long currentReserved;

	private List<ReservationSeatDetailDto> reservationSeatDetailDtoList;

	@Builder
	public ReservationSeatDto(long seatId, long concertId, long maxCapacity, long currentReserved) {
		this.seatId = seatId;
		this.concertId = concertId;
		this.maxCapacity = maxCapacity;
		this.currentReserved = currentReserved;
	}

	public static ReservationSeatDto ToDto (ReservationSeat reservationSeat){
		return ReservationSeatDto.builder()
			.seatId(reservationSeat.getSeatId())
			.concertId(reservationSeat.getConcertId())
			.maxCapacity(reservationSeat.getMaxCapacity())
			.currentReserved(reservationSeat.getCurrentReserved())
			.build();
	}
}
