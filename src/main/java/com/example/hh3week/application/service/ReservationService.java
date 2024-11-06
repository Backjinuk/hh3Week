package com.example.hh3week.application.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
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
	private final RedisTemplate<String, Object> redisTemplate;

	public ReservationService(ReservationSeatRepositoryPort reservationSeatRepositoryPort,
		ReservationMessagingPort reservationMessagingPort,
		@Qualifier("redisTemplate") RedisTemplate<String, Object> redisTemplate) {
		this.reservationSeatRepositoryPort = reservationSeatRepositoryPort;
		this.reservationMessagingPort = reservationMessagingPort;
		this.redisTemplate = redisTemplate;
	}


	@Cacheable("RESERVATION_ITEM")
	@Transactional
	public void getReservationItem() {
		String reservationKey = "reservationKey";

		List<ReservationSeatDto> reservationSeatList = reservationSeatRepositoryPort.getAvailableALLReservationSeatList()
			.stream().map(ReservationSeatDto::ToDto).toList();

		redisTemplate.opsForHash().put(reservationKey, reservationKey, reservationSeatList);

		// 예약 세부 정보 리스트 초기화
		List<ReservationSeatDetail> allReservationDetails = new ArrayList<>();

		for (ReservationSeatDto reservationSeatDto : reservationSeatList) {
			long seatId = reservationSeatDto.getSeatId();

			// 예약 좌석 세부 정보를 가져오기
			List<ReservationSeatDetail> reservationSeatDetailList = reservationSeatRepositoryPort.getAvailableReservationSeatDetailList(seatId);

			// 예약 세부 정보 리스트에 추가
			allReservationDetails.addAll(reservationSeatDetailList);
		}

		// 모든 예약 세부 정보를 reservationDetailKey에 저장
		redisTemplate.opsForHash().put("reservationDetailKey", "reservationDetailKey", allReservationDetails);

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
		// Redis에서 예약 세부 정보 리스트 가져오기
		List<ReservationSeatDetail> allDetails = (List<ReservationSeatDetail>) redisTemplate.opsForHash().get("reservationDetailKey", "reservationDetailKey");

		if (allDetails == null) {
			throw new IllegalArgumentException("해당 좌석 세부 정보가 Redis에 존재하지 않습니다.");
		}

		System.out.println("allDetails : " + allDetails.get(0));
		// seatDetailId로 필터링하여 찾기
		ReservationSeatDetail foundDetail = allDetails.stream()
			.filter(reservationSeatDetail -> reservationSeatDetail.getSeatDetailId() == seatDetailId)
			.findFirst()
			.orElse(null); // 존재하지 않을 경우 null 반환

		// return ReservationSeatDetailDto.ToDto(reservationSeatRepositoryPort.getSeatDetailById(seatDetailId));

		return ReservationSeatDetailDto.ToDto(foundDetail);
	}


	public ReservationSeatDto getSeatById(long seatId){
		return ReservationSeatDto.ToDto(reservationSeatRepositoryPort.getSeatById(seatId));
	}

	public void updateSeatDetailStatus(ReservationSeatDetailDto seatDetail) {
		reservationSeatRepositoryPort.updateSeatDetailStatus(ReservationSeatDetail.ToEntity(seatDetail));


		// Redis에서도 상태 업데이트
		String reservationDetailKey = "reservationDetailKey"; // Redis 키
		List<ReservationSeatDetail> allDetails = (List<ReservationSeatDetail>) redisTemplate.opsForHash().get(reservationDetailKey, reservationDetailKey);

		if (allDetails != null) {
			// 변경된 좌석 세부 정보를 Redis에 반영
			allDetails.stream()
				.filter(detail -> detail.getSeatDetailId() == seatDetail.getSeatDetailId())
				.findFirst()
				.ifPresent(detail -> {
					detail.setReservationStatus(seatDetail.getReservationStatus()); // 상태 업데이트
					detail.setSeatPrice(seatDetail.getSeatPrice()); // 필요 시 가격도 업데이트
				});

			// 업데이트된 리스트를 Redis에 다시 저장
			redisTemplate.opsForHash().put(reservationDetailKey, reservationDetailKey, allDetails);
		}
	}



	public ReservationSeatDetailDto getSeatDetailByIdForUpdate(long seatDetailId) {
		return ReservationSeatDetailDto.ToDto(reservationSeatRepositoryPort.getSeatDetailByIdForUpdate(seatDetailId));
	}

	public CompletableFuture<TokenDto> sendReservationRequest(long userId, long seatDetailId) {
		return reservationMessagingPort.sendReservationRequest(userId, seatDetailId);
	}
}
