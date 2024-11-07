package com.example.hh3week.application.service;

import java.time.Duration;
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
import com.fasterxml.jackson.core.JsonProcessingException;

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

	@CacheEvict(value = "RESERVATION_ITEM", allEntries = true) // 모든 캐시 항목 무효화
	public void invalidateReservationCache() {
		// 캐시를 무효화하는 것 외에 추가 작업이 필요 없다면 비어있는 메서드로 남김
	}

	public void reloadReservationItem() {
		invalidateReservationCache(); // 캐시 무효화

		// 모든 예약 좌석 정보를 Redis에 저장
		getReservationItem(); // 데이터를 다시 로드
	}

	@Cacheable("RESERVATION_ITEM")
	@Transactional
	public void getReservationItem() {
		String reservationKey = "reservationKey";

		// 모든 예약 좌석 정보를 Redis에 저장
		List<ReservationSeatDto> reservationSeatList = reservationSeatRepositoryPort.getAvailableALLReservationSeatList()
			.stream()
			.map(reservationSeat -> {
				ReservationSeatDto reservationSeatDto = ReservationSeatDto.ToDto(reservationSeat);
				redisTemplate.opsForHash()
					.put(reservationKey, String.valueOf(reservationSeatDto.getSeatId()), reservationSeatDto);
				redisTemplate.expire("reservationKey", Duration.ofDays(1));
				return reservationSeatDto;
			})
			.toList();

		// 예약 세부 정보 리스트 초기화
		for (ReservationSeatDto reservationSeatDto : reservationSeatList) {
			long seatId = reservationSeatDto.getSeatId();

			// 예약 좌석 세부 정보를 가져오기
			reservationSeatRepositoryPort.getAvailableReservationSeatDetailList(seatId)
				.forEach(reservationSeatDetail -> {
					ReservationSeatDetailDto reservationSeatDetailDto = ReservationSeatDetailDto.ToDto(
						reservationSeatDetail);
					String seatDetailKey =
						"reservationDetailKey:" + reservationSeatDetailDto.getSeatDetailId(); // Redis 키

					// 객체를 Value로 저장 (직렬화하여 저장)
					redisTemplate.opsForValue().set(seatDetailKey, reservationSeatDetailDto);
					redisTemplate.expire(seatDetailKey, Duration.ofDays(1)); // 세부 정보 만료 시간 설정
				});
		}

	}

	@Scheduled(cron = "0 0 0 * * *") // 매일 자정에 캐시 무효화
	@CacheEvict(value = "RESERVATION_ITEM", allEntries = true) // 모든 캐시 항목 무효화
	public void evictPopularItemsCache() {
		getReservationItem();
		System.out.println("Evicted POPULAR_ITEM cache");
	}

	/*
	 * 특적 콘서트의 좌석 마스터정보 가지고 오기
	 * */
	public List<ReservationSeatDto> getAvailableReservationSeatList(long seatId) {
		return reservationSeatRepositoryPort.getAvailableReservationSeatList(seatId)
			.stream()
			.map(ReservationSeatDto::ToDto)
			.toList();
	}

	/*
	 * 특정 좌석마스터 의 예약가능한 좌석 정보 가지고 오기
	 *
	 * */
	public List<ReservationSeatDetailDto> getAvailableReservationSeatDetailList(long seatId) {
		return reservationSeatRepositoryPort.getAvailableReservationSeatDetailList(seatId)
			.stream()
			.map(ReservationSeatDetailDto::ToDto)
			.toList();
	}

	/**
	 * 좌석 예약 상태를 업데이트하는 메서드
	 * @param seat 예약할 좌석 엔티티
	 */
	public void updateSeatReservation(ReservationSeatDto seat) {

		seat.setCurrentReserved(seat.getCurrentReserved() + 1);

		if (seat.getCurrentReserved() >= seat.getMaxCapacity()) {
			CustomException.illegalArgument("이미 최대 예약 수에 도달했습니다.", new IllegalArgumentException(), this.getClass());
		}

		reservationSeatRepositoryPort.updateReservationCurrentReserved(ReservationSeat.ToEntity(seat));
	}

	public ReservationSeatDetailDto getSeatDetailById(long seatDetailId) {
		// Redis에서 예약 세부 정보 리스트 가져오기
		String seatDetailKey = "reservationDetailKey:" + seatDetailId; // Redis 키

		// Redis에서 해당 좌석 세부 정보를 가져오기
		ReservationSeatDetailDto foundDetail = (ReservationSeatDetailDto) redisTemplate.opsForValue().get(seatDetailKey);

		if (foundDetail == null) {
			throw new IllegalArgumentException("해당 좌석 세부 정보가 Redis에 존재하지 않습니다.");
		}

		//기존 DB사용 로직
		// reservationSeatRepositoryPort.getSeatDetailById(seatDetailId);
		return foundDetail;

	}

	public ReservationSeatDto getSeatById(long seatId) {
		return ReservationSeatDto.ToDto(reservationSeatRepositoryPort.getSeatById(seatId));
	}

	public void updateSeatDetailStatus(ReservationSeatDetailDto seatDetail) {
		reservationSeatRepositoryPort.updateSeatDetailStatus(ReservationSeatDetail.ToEntity(seatDetail));

		// Redis에서도 상태 업데이트
		ReservationSeatDetailDto foundDetail = (ReservationSeatDetailDto)redisTemplate.opsForValue().get("reservationDetailKey:" + seatDetail.getSeatDetailId());

		if (foundDetail != null) {
			// 상태 업데이트
			foundDetail.setReservationStatus(seatDetail.getReservationStatus());

			// Redis에 업데이트된 객체 저장
			redisTemplate.opsForValue().set("reservationDetailKey:" + seatDetail.getSeatDetailId(), foundDetail);
		} else {
			throw new IllegalArgumentException("해당 좌석 세부 정보가 Redis에 존재하지 않습니다.");
		}


	}

	public ReservationSeatDetailDto getSeatDetailByIdForUpdate(long seatDetailId) {
		return ReservationSeatDetailDto.ToDto(reservationSeatRepositoryPort.getSeatDetailByIdForUpdate(seatDetailId));
	}

	public CompletableFuture<TokenDto> sendReservationRequest(long userId, long seatDetailId) {
		return reservationMessagingPort.sendReservationRequest(userId, seatDetailId);
	}
}

