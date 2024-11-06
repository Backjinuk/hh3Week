package com.example.hh3week.application.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.hh3week.adapter.in.dto.reservation.ReservationSeatDetailDto;
import com.example.hh3week.adapter.in.dto.reservation.ReservationSeatDto;
import com.example.hh3week.adapter.in.dto.token.TokenDto;
import com.example.hh3week.application.port.out.ReservationMessagingPort;
import com.example.hh3week.application.port.out.ReservationSeatRepositoryPort;
import com.example.hh3week.common.config.exception.CustomException;
import com.example.hh3week.domain.reservation.entity.ReservationSeat;
import com.example.hh3week.domain.reservation.entity.ReservationSeatDetail;

import jakarta.transaction.Transactional;

@Service
public class ReservationService {

	private final ReservationSeatRepositoryPort reservationSeatRepositoryPort;

	private final ReservationMessagingPort reservationMessagingPort;

	public ReservationService(ReservationSeatRepositoryPort reservationSeatRepositoryPort,
		ReservationMessagingPort reservationMessagingPort) {
		this.reservationSeatRepositoryPort = reservationSeatRepositoryPort;
		this.reservationMessagingPort = reservationMessagingPort;
	}


	@Cacheable("RESERVATION_ITEM")
	@Transactional(readOnly = true)
	public List<ReservationSeatDto> getPopularItems() {
		List<ReservationSeat> availableALLReservationSeatList = reservationSeatRepositoryPort.getAvailableALLReservationSeatList();
		return availableALLReservationSeatList;
	}

	@Scheduled(cron = "0 0 0 * * *") // 매일 자정에 캐시 무효화
	@CacheEvict(value = "RESERVATION_ITEM", allEntries = true) // 모든 캐시 항목 무효화
	public void evictPopularItemsCache() {
		System.out.println("Evicted POPULAR_ITEM cache");
	}

	/*
	 * 특적 콘서트의 좌석 마스터정보 가지고 오기
	 * */
	public List<ReservationSeatDto> getAvailableReservationSeatList(long seatId){
		return reservationSeatRepositoryPort.getAvailableReservationSeatList(seatId).stream().map(ReservationSeatDto::ToDto).toList();
	}

	/*
	* 특정 좌석마스터 의 예약가능한 좌석 정보 가지고 오기
	*
	* */
	public List<ReservationSeatDetailDto> getAvailableReservationSeatDetailList(long seatId){
		return reservationSeatRepositoryPort.getAvailableReservationSeatDetailList(seatId).stream().map(ReservationSeatDetailDto::ToDto).toList();
	}

	/**
	 * 좌석 예약 상태를 업데이트하는 메서드
	 * @param seat 예약할 좌석 엔티티
	 */
	public void updateSeatReservation(ReservationSeatDto seat) {

		seat.setCurrentReserved(seat.getCurrentReserved() + 1);

		if(seat.getCurrentReserved() >= seat.getMaxCapacity()){
			CustomException.illegalArgument("이미 최대 예약 수에 도달했습니다.", new IllegalArgumentException(), this.getClass());
		}

		reservationSeatRepositoryPort.updateReservationCurrentReserved(ReservationSeat.ToEntity(seat));
	}

	public ReservationSeatDetailDto getSeatDetailById(long seatDetailId) {
		return ReservationSeatDetailDto.ToDto(reservationSeatRepositoryPort.getSeatDetailById(seatDetailId));
	}

	public ReservationSeatDto getSeatById(long seatId){
		return ReservationSeatDto.ToDto(reservationSeatRepositoryPort.getSeatById(seatId));
	}

	public void updateSeatDetailStatus(ReservationSeatDetailDto seatDetail) {
		reservationSeatRepositoryPort.updateSeatDetailStatus(ReservationSeatDetail.ToEntity(seatDetail));
	}


	public ReservationSeatDetailDto getSeatDetailByIdForUpdate(long seatDetailId) {
		return ReservationSeatDetailDto.ToDto(reservationSeatRepositoryPort.getSeatDetailByIdForUpdate(seatDetailId));
	}

	public CompletableFuture<TokenDto> sendReservationRequest(long userId, long seatDetailId) {
		return reservationMessagingPort.sendReservationRequest(userId, seatDetailId);
	}
}
