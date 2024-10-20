package com.example.hh3week.adapter.out.persistence;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.hh3week.adapter.in.dto.reservation.ReservationSeatDetailDto;
import com.example.hh3week.application.port.out.ReservationSeatRepositoryPort;
import com.example.hh3week.domain.concert.entity.Concert;
import com.example.hh3week.domain.reservation.entity.QReservationSeat;
import com.example.hh3week.domain.reservation.entity.QReservationSeatDetail;
import com.example.hh3week.domain.reservation.entity.ReservationSeat;
import com.example.hh3week.domain.reservation.entity.ReservationSeatDetail;
import com.example.hh3week.domain.reservation.entity.ReservationStatus;
import com.querydsl.jpa.impl.JPAQueryFactory;

@Repository
public class ReservationSeatRepositoryImpl implements ReservationSeatRepositoryPort {

	private final JPAQueryFactory queryFactory;

	private final QReservationSeat qReservationSeat = QReservationSeat.reservationSeat;
	private final QReservationSeatDetail qReservationSeatDetail = QReservationSeatDetail.reservationSeatDetail;

	public ReservationSeatRepositoryImpl(JPAQueryFactory queryFactory) {
		this.queryFactory = queryFactory;
	}

	/**
	 * 사용 가능한 모든 예약 좌석 상세 목록을 반환합니다.
	 *
	 * @param seatId 좌석 ID
	 * @return List of available ReservationSeatDetail
	 */
	@Override
	@Transactional(readOnly = true)
	public List<ReservationSeatDetail> getAvailableReservationSeatDetailList(long seatId) {
		return queryFactory.selectFrom(qReservationSeatDetail)
			.where(qReservationSeatDetail.seatId.eq(seatId)
				.and(qReservationSeatDetail.reservationStatus.eq(ReservationStatus.AVAILABLE))) // 'AVAILABLE' 상태 필터링
			.fetch();
	}


	/**
	 * 특정 콘서트 스케줄에 대한 사용 가능한 예약 좌석 목록을 반환합니다.
	 *
	 * @param concertScheduleId 콘서트 스케줄 ID
	 * @return List of available ReservationSeat
	 */
	@Override
	@Transactional(readOnly = true)
	public List<ReservationSeat> getAvailableReservationSeatList(long concertScheduleId) {
		return queryFactory.selectFrom(qReservationSeat)
			.where(qReservationSeat.concertId.eq(concertScheduleId)) // 'AVAILABLE' 상태 필터링
			.fetch();
	}

	/**
	 * 예약 좌석의 현재 예약 상태를 업데이트합니다.
	 *
	 * @param reservationSeat 업데이트할 ReservationSeat
	 */
	@Override
	@Transactional
	public void updateReservationCurrentReserved(ReservationSeat reservationSeat) {
		queryFactory.update(qReservationSeat)
			.set(qReservationSeat.currentReserved, reservationSeat.getCurrentReserved())
			.where(qReservationSeat.seatId.eq(reservationSeat.getSeatId()))
			.execute();
	}


	/**
	 * 주어진 seatId로 예약 좌석 상세 정보를 조회합니다.
	 *
	 * @param seatDetailId 좌석 ID
	 * @return ReservationSeatDetail 객체
	 */
	@Override
	@Transactional(readOnly = true)
	public ReservationSeatDetail getSeatDetailById(long seatDetailId) {
		ReservationSeatDetail reservationSeatDetail = queryFactory.selectFrom(qReservationSeatDetail)
			.where(qReservationSeatDetail.seatDetailId.eq(seatDetailId))
			.fetchOne();

		if(reservationSeatDetail == null){
			throw new NullPointerException("해당 좌석을 찾을수 없습니다.");
		}

		return reservationSeatDetail;

	}



	/**
	 * 예약 좌석 상세 상태를 업데이트합니다.
	 *
	 * @param seatDetail 업데이트할 ReservationSeatDetailDto
	 */
	@Override
	@Transactional
	public void updateSeatDetailStatus(ReservationSeatDetail seatDetail) {
		 queryFactory.update(qReservationSeatDetail)
			.set(qReservationSeatDetail.reservationStatus, seatDetail.getReservationStatus())
			.where(qReservationSeatDetail.seatId.eq(seatDetail.getSeatDetailId()))
			.execute();

	}



	/**
	 * 주어진 seatId로 예약 좌석을 조회합니다.
	 *
	 * @param seatId 좌석 ID
	 * @return ReservationSeat 객체
	 */
	@Override
	@Transactional(readOnly = true)
	public ReservationSeat getSeatById(long seatId) {

		return queryFactory.selectFrom(qReservationSeat)
			.where(qReservationSeat.seatId.eq(seatId))
			.fetchOne();
	}

}