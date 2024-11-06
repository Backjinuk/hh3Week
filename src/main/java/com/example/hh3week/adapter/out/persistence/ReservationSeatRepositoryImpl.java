package com.example.hh3week.adapter.out.persistence;

import static jakarta.persistence.LockModeType.*;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import com.example.hh3week.application.port.out.ReservationSeatRepositoryPort;
import com.example.hh3week.common.config.exception.CustomException;
import com.example.hh3week.domain.reservation.entity.QReservationSeat;
import com.example.hh3week.domain.reservation.entity.QReservationSeatDetail;
import com.example.hh3week.domain.reservation.entity.ReservationSeat;
import com.example.hh3week.domain.reservation.entity.ReservationSeatDetail;
import com.example.hh3week.domain.reservation.entity.ReservationStatus;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;

@Repository
public class ReservationSeatRepositoryImpl implements ReservationSeatRepositoryPort {

	private final JPAQueryFactory queryFactory;
	private final EntityManager entityManager;

	private final QReservationSeat qReservationSeat = QReservationSeat.reservationSeat;
	private final QReservationSeatDetail qReservationSeatDetail = QReservationSeatDetail.reservationSeatDetail;
	private final RedisTemplate<String, Object> redisTemplate;

	public ReservationSeatRepositoryImpl(JPAQueryFactory queryFactory, EntityManager entityManager,
		@Qualifier("redisTemplate") RedisTemplate<String, Object> redisTemplate) {
		this.queryFactory = queryFactory;
		this.entityManager = entityManager;
		this.redisTemplate = redisTemplate;
	}

	@Override
	public List<ReservationSeatDetail> getAvailableReservationSeatDetailList(long seatId) {
		List<ReservationSeatDetail> seatDetails = queryFactory.selectFrom(qReservationSeatDetail)
			.where(qReservationSeatDetail.seatId.eq(seatId)
				.and(qReservationSeatDetail.reservationStatus.eq(ReservationStatus.AVAILABLE)))
			.fetch();

		if (seatDetails.isEmpty()) {
			CustomException.nullPointer("해당 좌석을 찾을수 없습니다.", this.getClass());
		}

		return seatDetails;
	}

	@Override
	public List<ReservationSeat> getAvailableReservationSeatList(long concertScheduleId) {
		List<ReservationSeat> seats = queryFactory.selectFrom(qReservationSeat)
			.where(qReservationSeat.concertId.eq(concertScheduleId))
			.fetch();

		if (seats.isEmpty()) {
			CustomException.nullPointer("해당 콘서트 스케줄에 사용 가능한 좌석이 없습니다.", this.getClass());
		}

		return seats;
	}

	@Override
	public List<ReservationSeat> getAvailableALLReservationSeatList() {
		return queryFactory.selectFrom(qReservationSeat)
			.stream()
			.toList();
	}


	@Override
	public void updateReservationCurrentReserved(ReservationSeat reservationSeat) {
		queryFactory.update(qReservationSeat)
			.set(qReservationSeat.currentReserved, reservationSeat.getCurrentReserved())
			.where(qReservationSeat.seatId.eq(reservationSeat.getSeatId()))
			.execute();
	}

	@Override
	public ReservationSeatDetail getSeatDetailById(long seatDetailId) {
		// redisTemplate


		ReservationSeatDetail seatDetail = queryFactory.selectFrom(qReservationSeatDetail)
			.where(qReservationSeatDetail.seatDetailId.eq(seatDetailId))
			.fetchOne();

		if (seatDetail == null) {
			CustomException.nullPointer("해당 좌석을 찾을수 없습니다.", this.getClass());
		}

		return seatDetail;
	}

	@Override
	public void updateSeatDetailStatus(ReservationSeatDetail seatDetail) {
		queryFactory.update(qReservationSeatDetail)
			.set(qReservationSeatDetail.reservationStatus, seatDetail.getReservationStatus())
			.where(qReservationSeatDetail.seatDetailId.eq(seatDetail.getSeatDetailId()))
			.execute();
	}

	@Override
	public ReservationSeat getSeatById(long seatId) {
		ReservationSeat seat = queryFactory.selectFrom(qReservationSeat)
			.where(qReservationSeat.seatId.eq(seatId))
			.fetchOne();

		if (seat == null) {
			CustomException.nullPointer("좌석을 찾을 수 없습니다.", this.getClass());
		}

		return seat;
	}

    @Override
    public ReservationSeatDetail getSeatDetailByIdForUpdate(long seatDetailId) {
        ReservationSeatDetail seatDetail = queryFactory.selectFrom(qReservationSeatDetail)
            .where(qReservationSeatDetail.seatDetailId.eq(seatDetailId))
            .setLockMode(OPTIMISTIC) // 비관적 잠금 설정
            .fetchOne();

        if (seatDetail == null) {
            CustomException.nullPointer("해당 좌석을 찾을 수 없습니다.", this.getClass());
        }



        return seatDetail;
    }
}
