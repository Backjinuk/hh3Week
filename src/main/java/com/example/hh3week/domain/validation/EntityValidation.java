package com.example.hh3week.domain.validation;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.example.hh3week.domain.concert.entity.Concert;
import com.example.hh3week.domain.concert.entity.ConcertSchedule;
import com.example.hh3week.domain.payment.entity.PaymentHistory;
import com.example.hh3week.domain.reservation.entity.ReservationSeatDetail;
import com.example.hh3week.domain.user.entity.User;
import com.example.hh3week.domain.user.entity.UserPointHistory;

@Component
public class EntityValidation {

	public void validateConcert(Concert concert) {
		if (concert == null) {
			throw new IllegalArgumentException("콘서트 엔티티가 null일 수 없습니다.");
		}
		if (!StringUtils.hasText(concert.getConcertName())) {
			throw new IllegalArgumentException("콘서트 이름은 비워둘 수 없습니다.");
		}
		if (!StringUtils.hasText(concert.getConcertContent())) {
			throw new IllegalArgumentException("콘서트 내용은 비워둘 수 없습니다.");
		}
		if (concert.getStartDt() == null) {
			throw new IllegalArgumentException("시작 날짜는 null일 수 없습니다.");
		}
		if (concert.getEndDt() == null) {
			throw new IllegalArgumentException("종료 날짜는 null일 수 없습니다.");
		}
	}

	public void validateUser(User user) {
		if (user == null) {
			throw new IllegalArgumentException("사용자 엔티티가 null일 수 없습니다.");
		}
		if (!StringUtils.hasText(user.getUserName())) {
			throw new IllegalArgumentException("사용자 이름은 비워둘 수 없습니다.");
		}
		if (user.getPointBalance() < 0) {
			throw new IllegalArgumentException("포인트 잔액은 음수일 수 없습니다.");
		}
	}

	public void validatePaymentHistory(PaymentHistory paymentHistory) {
		if (paymentHistory == null) {
			throw new IllegalArgumentException("결제 히스토리 엔티티가 null일 수 없습니다.");
		}
		if (paymentHistory.getPaymentAmount() <= 0) {
			throw new IllegalArgumentException("결제 금액은 0보다 커야 합니다.");
		}
		if (paymentHistory.getUserId() <= 0) {
			throw new IllegalArgumentException("유효한 사용자 ID가 필요합니다.");
		}
		if (paymentHistory.getReservationId() <= 0) {
			throw new IllegalArgumentException("유효한 예약 ID가 필요합니다.");
		}
	}

	public void validateReservationSeatDetail(ReservationSeatDetail reservationSeatDetail) {
		if (reservationSeatDetail == null) {
			throw new IllegalArgumentException("예약 좌석 상세 정보 엔티티가 null일 수 없습니다.");
		}
		if (reservationSeatDetail.getSeatId() <= 0) {
			throw new IllegalArgumentException("유효한 좌석ID가 필요합니다.");
		}
		if (!StringUtils.hasText(reservationSeatDetail.getSeatCode())) {
			throw new IllegalArgumentException("좌석 코드는 비워둘 수 없습니다.");
		}
		if (reservationSeatDetail.getUserId() <= 0) {
			throw new IllegalArgumentException("유효한 사용자 ID가 필요합니다.");
		}
		if (reservationSeatDetail.getSeatPrice() <= 0) {
			throw new IllegalArgumentException("결제 금액은 0보다 커야 합니다.");
		}
		if (reservationSeatDetail.getReservationStatus() == null) {
			throw new IllegalArgumentException("예약 상태는 null일 수 없습니다.");
		}
	}

	public void validateConcertSchedule(ConcertSchedule concertSchedule) {
		if (concertSchedule == null) {
			throw new IllegalArgumentException("콘서트 일정 엔티티가 null일 수 없습니다.");
		}
		if (concertSchedule.getConcertId() <= 0) {
			throw new IllegalArgumentException("유효한 콘서트 ID가 필요합니다.");
		}

		if (concertSchedule.getConcertPrice() < 0) {
			throw new IllegalArgumentException("콘서트 가격은 음수일 수 없습니다.");
		}
		if (concertSchedule.getConcertScheduleStatus() == null) {
			throw new IllegalArgumentException("콘서트 일정 상태는 null일 수 없습니다.");
		}
	}

	public void validatePointHistory(UserPointHistory userPointHistory) {
		if (userPointHistory == null) {
			throw new IllegalArgumentException("포인트 히스토리 엔티티가 null일 수 없습니다.");
		}
		if (userPointHistory.getUserId() <= 0) {
			throw new IllegalArgumentException("유효한 사용자 ID가 필요합니다.");
		}
		if (userPointHistory.getPointAmount() == 0) {
			throw new IllegalArgumentException("포인트 변화 금액은 0일 수 없습니다.");
		}
		if (userPointHistory.getPointStatus() == null) {
			throw new IllegalArgumentException("포인트 변화 유형은 null일 수 없습니다.");
		}
		if (userPointHistory.getPointDt() == null) {
			throw new IllegalArgumentException("포인트 변화 시간이 null일 수 없습니다.");
		}
	}
}
