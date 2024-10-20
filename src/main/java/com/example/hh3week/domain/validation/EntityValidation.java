package com.example.hh3week.domain.validation;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.example.hh3week.domain.concert.entity.Concert;
import com.example.hh3week.domain.concert.entity.ConcertSchedule;
import com.example.hh3week.domain.payment.entity.PaymentHistory;
import com.example.hh3week.domain.reservation.entity.ReservationSeatDetail;
import com.example.hh3week.domain.token.entity.Token;
import com.example.hh3week.domain.user.entity.User;
import com.example.hh3week.domain.user.entity.UserPointHistory;
import com.example.hh3week.domain.waitingQueue.entity.WaitingQueue;

@Component
public class EntityValidation {

	// Concert 검증
	public static void validateConcert(Concert concert) {
		if (concert == null) {
			throw new IllegalArgumentException("콘서트 Entity가 null일 수 없습니다.");
		}
		if (!StringUtils.hasText(concert.getConcertName())) {
			throw new IllegalArgumentException("콘서트 이름은 비워둘 수 없습니다.");
		}
		if (!StringUtils.hasText(concert.getConcertContent())) {
			throw new IllegalArgumentException("콘서트 내용은 비워둘 수 없습니다.");
		}
	}

	// User 검증
	public static void validateUser(User user) {
		if (user == null) {
			throw new IllegalArgumentException("사용자 Entity가 null일 수 없습니다.");
		}
		if (!StringUtils.hasText(user.getUserName())) {
			throw new IllegalArgumentException("사용자 이름은 비워둘 수 없습니다.");
		}
		if (user.getPointBalance() < 0) {
			throw new IllegalArgumentException("포인트 잔액은 음수일 수 없습니다.");
		}
	}

	// PaymentHistory 검증
	public static void validatePaymentHistory(PaymentHistory paymentHistory) {
		if (paymentHistory == null) {
			throw new IllegalArgumentException("결제 히스토리 Entity가 null일 수 없습니다.");
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

	// ReservationSeatDetail 검증
	public static void validateReservationSeatDetail(ReservationSeatDetail reservationSeatDetail) {
		if (reservationSeatDetail == null) {
			throw new IllegalArgumentException("예약 좌석 상세 정보 Entity가 null일 수 없습니다.");
		}
		if (reservationSeatDetail.getSeatId() <= 0) {
			throw new IllegalArgumentException("유효한 좌석 ID가 필요합니다.");
		}
		if (!StringUtils.hasText(reservationSeatDetail.getSeatCode())) {
			throw new IllegalArgumentException("좌석 코드는 비워둘 수 없습니다.");
		}
		if (reservationSeatDetail.getUserId() <= 0) {
			throw new IllegalArgumentException("유효한 사용자 ID가 필요합니다.");
		}
		if (reservationSeatDetail.getSeatPrice() <= 0) {
			throw new IllegalArgumentException("좌석 가격은 0보다 커야 합니다.");
		}
		if (reservationSeatDetail.getReservationStatus() == null) {
			throw new IllegalArgumentException("예약 상태는 비워둘 수 없습니다.");
		}
	}

	// ConcertSchedule 검증
	public static void validateConcertSchedule(ConcertSchedule concertSchedule) {
		if (concertSchedule == null) {
			throw new IllegalArgumentException("콘서트 일정 Entity가 null일 수 없습니다.");
		}
		if (concertSchedule.getConcertId() <= 0) {
			throw new IllegalArgumentException("유효한 콘서트 ID가 필요합니다.");
		}

		if (concertSchedule.getConcertPrice() < 0) {
			throw new IllegalArgumentException("콘서트 가격은 음수일 수 없습니다.");
		}
		if (concertSchedule.getConcertScheduleStatus() == null) {
			throw new IllegalArgumentException("콘서트 일정 상태는 비워둘 수 없습니다.");
		}
	}

	// UserPointHistory 검증
	public static void validateUserPointHistory(UserPointHistory userPointHistory) {
		if (userPointHistory == null) {
			throw new IllegalArgumentException("포인트 히스토리 Entity가 null일 수 없습니다.");
		}
		if (userPointHistory.getUserId() <= 0) {
			throw new IllegalArgumentException("유효한 사용자 ID가 필요합니다.");
		}
		if (userPointHistory.getPointAmount() < 0) {
			throw new IllegalArgumentException("포인트 변화 금액은 음수일 수 없습니다.");
		}
		if (userPointHistory.getPointStatus() == null) {
			throw new IllegalArgumentException("포인트 변화 유형은 null일 수 없습니다.");
		}
		if (userPointHistory.getPointDt() == null) {
			throw new IllegalArgumentException("포인트 변화 시간은 null일 수 없습니다.");
		}
	}

	public static void validateWaitingQueue(WaitingQueue waitingQueue) {

		if (waitingQueue == null) {
			throw new IllegalArgumentException("대기열 Queue는 null일 수 없습니다.");
		}

		if (waitingQueue.getUserId() < 0) {
			throw new IllegalArgumentException("유효한 사용자 ID가 필요합니다.");
		}

		if (waitingQueue.getSeatDetailId() < 0) {
			throw new IllegalArgumentException("유효한 콘서트ScheduleID가 필요합니다.");
		}

		if (waitingQueue.getReservationDt() == null) {
			throw new IllegalArgumentException("유효한 예약 날짜가 아닙니다.");
		}

		if (waitingQueue.getWaitingStatus() == null) {
			throw new IllegalArgumentException("유효한 대기열 상태가 아닙니다.");
		}

		if (waitingQueue.getPriority() < 0) {
			throw new IllegalArgumentException("유효한 대기열 순서가 아닙니다.");
		}

	}

	public static void validateToken(Token token) {
		if (token == null) {
			throw new IllegalArgumentException("token는 null일 수 없습니다.");
		}

		if (token.getUserId() < 0) {
			throw new IllegalArgumentException("유효한 userID가 아닙니다.");
		}

		if (!StringUtils.hasText(token.getToken())) {
			throw new IllegalArgumentException("유효한 Token값이 아닙니다.");
		}

		if (token.getIssuedAt() == null) {
			throw new IllegalArgumentException("유효한 발생일이 아닙니다.");
		}

		if (token.getExpiresAt() == null) {
			throw new IllegalArgumentException("유효한 만료일이 아닙니다.");
		}

	}
}
