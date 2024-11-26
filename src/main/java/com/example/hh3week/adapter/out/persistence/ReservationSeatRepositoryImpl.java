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
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class ReservationSeatRepositoryImpl implements ReservationSeatRepositoryPort {

	private final JPAQueryFactory queryFactory;
	private final EntityManager entityManager;

	private final QReservationSeat qReservationSeat = QReservationSeat.reservationSeat;
	private final QReservationSeatDetail qReservationSeatDetail = QReservationSeatDetail.reservationSeatDetail;

	public ReservationSeatRepositoryImpl(JPAQueryFactory queryFactory, EntityManager entityManager) {
		this.queryFactory = queryFactory;
		this.entityManager = entityManager;
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
	@Transactional
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

		log.info("getSeatDetailById 조회전");

		ReservationSeatDetail seatDetail = queryFactory.selectFrom(qReservationSeatDetail)
			.where(qReservationSeatDetail.seatDetailId.eq(seatDetailId))
			.fetchOne();

		if (seatDetail == null) {
			CustomException.nullPointer("해당 좌석을 찾을수 없습니다.", this.getClass());
		}

		log.info("getSeatDetailById 조회후");
		return seatDetail;
	}

	@Override
	public void updateSeatDetailStatus(ReservationSeatDetail seatDetail) {

		try {
			long affectedRows = queryFactory.update(qReservationSeatDetail)
				.set(qReservationSeatDetail.reservationStatus, seatDetail.getReservationStatus())
				.where(qReservationSeatDetail.seatDetailId.eq(seatDetail.getSeatDetailId()))
				.execute();

			if (affectedRows == 0) {
				log.warn("updateSeatDetailStatus 실패: seatDetailId={}에 해당하는 데이터가 없습니다.", seatDetail.getSeatDetailId());
			}

		} catch (Exception e) {
			// 예외가 발생할 경우 로그를 남기고 예외를 다시 던지기
			log.error("updateSeatDetailStatus 중 예외 발생: {}", e.getMessage(), e);
			throw e;
		}
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
