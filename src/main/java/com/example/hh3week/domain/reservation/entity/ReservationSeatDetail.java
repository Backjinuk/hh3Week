package com.example.hh3week.domain.reservation.entity;

import com.example.hh3week.adapter.in.dto.reservation.ReservationSeatDetailDto;

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
public class ReservationSeatDetail {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long seatDetailId;

	private long userId;

	private long seatId;

	private String seatCode;

	@Enumerated(EnumType.STRING)
	private ReservationStatus reservationStatus;

	private long seatPrice;

	@Builder
	public ReservationSeatDetail(long seatDetailId, long userId, long seatId, String seatCode,
		ReservationStatus reservationStatus, long seatPrice) {
		this.seatDetailId = seatDetailId;
		this.userId = userId;
		this.seatId = seatId;
		this.seatCode = seatCode;
		this.reservationStatus = reservationStatus;
		this.seatPrice = seatPrice;
	}

	public static ReservationSeatDetail ToEntity(ReservationSeatDetailDto reservationSeatDetailDto){
		return ReservationSeatDetail.builder()
			.seatDetailId(reservationSeatDetailDto.getSeatDetailId())
			.userId(reservationSeatDetailDto.getUserId())
			.seatId(reservationSeatDetailDto.getSeatId())
			.seatCode(reservationSeatDetailDto.getSeatCode())
			.reservationStatus(reservationSeatDetailDto.getReservationStatus())
			.seatPrice(reservationSeatDetailDto.getSeatPrice())
			.build();
	}


}
