package com.example.hh3week.application.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import com.example.hh3week.adapter.in.dto.reservation.ReservationSeatDetailDto;
import com.example.hh3week.adapter.in.dto.reservation.ReservationSeatDto;
import com.example.hh3week.adapter.in.dto.token.TokenDto;
import com.example.hh3week.adapter.out.messaging.kafka.dto.ReleaseSeat;
import com.example.hh3week.application.port.out.ReservationMessagingPort;
import com.example.hh3week.application.port.out.ReservationSeatRepositoryPort;
import com.example.hh3week.common.config.exception.CustomException;
import com.example.hh3week.domain.reservation.entity.ReservationSeat;
import com.example.hh3week.domain.reservation.entity.ReservationSeatDetail;
import com.example.hh3week.domain.reservation.entity.ReservationStatus;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ReservationService {

	private final ReservationSeatRepositoryPort reservationSeatRepositoryPort;

	private final ReservationMessagingPort reservationMessagingPort;
	private final RedisTemplate<String, Object> redisTemplate;

	public ReservationService(ReservationSeatRepositoryPort reservationSeatRepositoryPort,
		ReservationMessagingPort reservationMessagingPort, RedisTemplate<String, Object> redisTemplate) {
		this.reservationSeatRepositoryPort = reservationSeatRepositoryPort;
		this.reservationMessagingPort = reservationMessagingPort;
		this.redisTemplate = redisTemplate;
	}

	/*
	 * @Cacheable은 메서드 실행결과를 저장하거나 이미 캐싱된 데이터를 반환하는 역활
	 * 이때 DB조회를 하게 되는데 Hibernate는 트랜잭션이 열려 있는 상태에서만 DB와의 연결을 유지 하기 때문에
	 * @Transactionl이 필요함
	 * */
	@Cacheable(value = "RESERVATION_ITEM", key = "'reservationKey'")
	public List<ReservationSeatDto> getReservationItem() {
		return reservationSeatRepositoryPort.getAvailableALLReservationSeatList()
			.stream()
			.map(ReservationSeatDto::ToDto)
			.toList();
	}

	@CacheEvict(value = "RESERVATION_ITEM", allEntries = true)
	public void clearReservationCache() {
		System.out.println("Existing reservation cache cleared.");
	}

	public void initializeAndRefreshCache() {
		clearReservationCache();      // 기존 캐시 삭제
		getReservationItem(); // 새 데이터로 캐시 갱신
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

	/*
	 * Redis를 사용할려고 했으나 Update가 자주 일어나서 성능의 저하를 가지고옴 기존 DB를 사용
	 * */
	public ReservationSeatDetailDto getSeatDetailById(long seatDetailId) {
		//기존 DB사용 로직
		// Redis에서 예약 세부 정보 리스트 가져오기
		return ReservationSeatDetailDto.ToDto(reservationSeatRepositoryPort.getSeatDetailById(seatDetailId));


	/*
			String seatDetailKey = "reservationDetailKey:" + seatDetailId; // Redis 키

			// Redis에서 해당 좌석 세부 정보를 가져오기
			ReservationSeatDetailDto foundDetail = (ReservationSeatDetailDto) redisTemplate.opsForValue().get(seatDetailKey);

			if (foundDetail == null) {
				throw new IllegalArgumentException("해당 좌석 세부 정보가 Redis에 존재하지 않습니다.");
			}
	*/

	}

	public ReservationSeatDto getSeatById(long seatId) {
		return ReservationSeatDto.ToDto(reservationSeatRepositoryPort.getSeatById(seatId));
	}

	public void updateSeatDetailStatus(ReservationSeatDetailDto seatDetail) {
		reservationSeatRepositoryPort.updateSeatDetailStatus(ReservationSeatDetail.ToEntity(seatDetail));

		// Redis에서도 상태 업데이트
		// ReservationSeatDetailDto foundDetail = (ReservationSeatDetailDto)redisTemplate.opsForValue().get("reservationDetailKey:" + seatDetail.getSeatDetailId());
		//
		// if (foundDetail != null) {
		// 	// 상태 업데이트
		// 	foundDetail.setReservationStatus(seatDetail.getReservationStatus());
		//
		// 	// Redis에 업데이트된 객체 저장
		// 	redisTemplate.opsForValue().set("reservationDetailKey:" + seatDetail.getSeatDetailId(), foundDetail);
		// } else {
		// 	throw new IllegalArgumentException("해당 좌석 세부 정보가 Redis에 존재하지 않습니다.");
		// }
		//

	}

	public ReservationSeatDetailDto getSeatDetailByIdForUpdate(long seatDetailId) {
		return ReservationSeatDetailDto.ToDto(reservationSeatRepositoryPort.getSeatDetailByIdForUpdate(seatDetailId));
	}

	public CompletableFuture<TokenDto> sendReservationRequest(long userId, long seatDetailId) {
		return reservationMessagingPort.sendReservationRequest(userId, seatDetailId);
	}



}

