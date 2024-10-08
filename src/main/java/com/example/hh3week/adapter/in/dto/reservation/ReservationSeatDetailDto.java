package com.example.hh3week.adapter.in.dto.reservation;

import com.example.hh3week.domain.reservation.entity.ReservationSeatDetail;
import com.example.hh3week.domain.reservation.entity.ReservationStatus;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReservationSeatDetailDto {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long seatDetailId;

	private long userId;

	private long seatId;

	private String seatCode;

	private ReservationStatus reservationStatus;

	private long seatPrice;

	@Builder
	public ReservationSeatDetailDto(long seatDetailId, long userId, long seatId, String seatCode,
		ReservationStatus reservationStatus, long seatPrice) {
		this.seatDetailId = seatDetailId;
		this.userId = userId;
		this.seatId = seatId;
		this.seatCode = seatCode;
		this.reservationStatus = reservationStatus;
		this.seatPrice = seatPrice;
	}

	public static ReservationSeatDetailDto toDto(ReservationSeatDetail reservationSeatDetail){
		return ReservationSeatDetailDto.builder()
			.seatDetailId(reservationSeatDetail.getSeatDetailId())
			.userId(reservationSeatDetail.getUserId())
			.seatId(reservationSeatDetail.getSeatId())
			.seatCode(reservationSeatDetail.getSeatCode())
			.seatPrice(reservationSeatDetail.getSeatPrice())
			.build();
	}
}
