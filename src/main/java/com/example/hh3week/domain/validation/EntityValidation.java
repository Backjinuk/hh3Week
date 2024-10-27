package com.example.hh3week.domain.validation;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.example.hh3week.common.config.CustomException;
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
			CustomException.illegalArgument("콘서트 Entity가 null일 수 없습니다.", new IllegalArgumentException() ,
				EntityValidation.class);
		}
		if (!StringUtils.hasText(concert.getConcertName())) {
			CustomException.illegalArgument("콘서트 이름은 비워둘 수 없습니다.", new IllegalArgumentException() , EntityValidation.class);
		}
		if (!StringUtils.hasText(concert.getConcertContent())) {
			CustomException.illegalArgument("콘서트 내용은 비워둘 수 없습니다.", new IllegalArgumentException() , EntityValidation.class);
		}
	}

	// User 검증
	public static void validateUser(User user) {
		if (user == null) {
			CustomException.illegalArgument("사용자 Entity가 null일 수 없습니다.", new IllegalArgumentException() ,
				EntityValidation.class);
		}
		if (!StringUtils.hasText(user.getUserName())) {
			CustomException.illegalArgument("사용자 이름은 비워둘 수 없습니다.", new IllegalArgumentException() , EntityValidation.class);
		}
		if (user.getPointBalance() < 0) {
			CustomException.illegalArgument("포인트 잔액은 음수일 수 없습니다.", new IllegalArgumentException() , EntityValidation.class);
		}
	}

	// PaymentHistory 검증
	public static void validatePaymentHistory(PaymentHistory paymentHistory) {
		if (paymentHistory == null) {
			CustomException.illegalArgument("결제 히스토리 Entity가 null일 수 없습니다.", new IllegalArgumentException() ,
				EntityValidation.class);
		}
		if (paymentHistory.getPaymentAmount() <= 0) {
			CustomException.illegalArgument("결제 금액은 0보다 커야 합니다.", new IllegalArgumentException() , EntityValidation.class);
		}
		if (paymentHistory.getUserId() <= 0) {
			CustomException.illegalArgument("유효한 사용자 ID가 필요합니다.", new IllegalArgumentException() , EntityValidation.class);
		}
		if (paymentHistory.getReservationId() <= 0) {
			CustomException.illegalArgument("유효한 예약 ID가 필요합니다.", new IllegalArgumentException() , EntityValidation.class);
		}
	}

	// ReservationSeatDetail 검증
	public static void validateReservationSeatDetail(ReservationSeatDetail reservationSeatDetail) {
		if (reservationSeatDetail == null) {
			CustomException.illegalArgument("예약 좌석 상세 정보 Entity가 null일 수 없습니다.", new IllegalArgumentException() ,
				EntityValidation.class);
		}
		if (reservationSeatDetail.getSeatId() <= 0) {
			CustomException.illegalArgument("유효한 좌석 ID가 필요합니다.", new IllegalArgumentException() , EntityValidation.class);
		}
		if (!StringUtils.hasText(reservationSeatDetail.getSeatCode())) {
			CustomException.illegalArgument("좌석 코드는 비워둘 수 없습니다.", new IllegalArgumentException() , EntityValidation.class);
		}
		if (reservationSeatDetail.getUserId() <= 0) {
			CustomException.illegalArgument("유효한 사용자 ID가 필요합니다.", new IllegalArgumentException() , EntityValidation.class);
		}
		if (reservationSeatDetail.getSeatPrice() <= 0) {
			CustomException.illegalArgument("좌석 가격은 0보다 커야 합니다.", new IllegalArgumentException() , EntityValidation.class);
		}
		if (reservationSeatDetail.getReservationStatus() == null) {
			CustomException.illegalArgument("예약 상태는 비워둘 수 없습니다.", new IllegalArgumentException() , EntityValidation.class);
		}
	}

	// ConcertSchedule 검증
	public static void validateConcertSchedule(ConcertSchedule concertSchedule) {
		if (concertSchedule == null) {
			CustomException.illegalArgument("콘서트 일정 Entity가 null일 수 없습니다.", new IllegalArgumentException() ,
				EntityValidation.class);
		}
		if (concertSchedule.getConcertId() <= 0) {
			CustomException.illegalArgument("유효한 콘서트 ID가 필요합니다.", new IllegalArgumentException() , EntityValidation.class);
		}

		if (concertSchedule.getConcertPrice() < 0) {
			CustomException.illegalArgument("콘서트 가격은 음수일 수 없습니다.", new IllegalArgumentException() , EntityValidation.class);
		}
		if (concertSchedule.getConcertScheduleStatus() == null) {
			CustomException.illegalArgument("콘서트 일정 상태는 비워둘 수 없습니다.", new IllegalArgumentException() , EntityValidation.class);
		}
	}

	// UserPointHistory 검증
	public static void validateUserPointHistory(UserPointHistory userPointHistory) {
		if (userPointHistory == null) {
			CustomException.illegalArgument("포인트 히스토리 Entity가 null일 수 없습니다.", new IllegalArgumentException() ,
				EntityValidation.class);
		}
		if (userPointHistory.getUserId() <= 0) {
			CustomException.illegalArgument("유효한 사용자 ID가 필요합니다.", new IllegalArgumentException() , EntityValidation.class);
		}
		if (userPointHistory.getPointAmount() < 0) {
			CustomException.illegalArgument("포인트 변화 금액은 음수일 수 없습니다.", new IllegalArgumentException() , EntityValidation.class);
		}
		if (userPointHistory.getPointStatus() == null) {
			CustomException.illegalArgument("포인트 변화 유형은 null일 수 없습니다.", new IllegalArgumentException() ,
				EntityValidation.class);
		}
		if (userPointHistory.getPointDt() == null) {
			CustomException.illegalArgument("포인트 변화 시간은 null일 수 없습니다.", new IllegalArgumentException() ,
				EntityValidation.class);
		}
	}

	public static void validateWaitingQueue(WaitingQueue waitingQueue) {

		if (waitingQueue == null) {
			CustomException.illegalArgument("대기열 Queue는 null일 수 없습니다.", new IllegalArgumentException() ,
				EntityValidation.class);
		}

		if (waitingQueue.getUserId() < 0) {
			CustomException.illegalArgument("유효한 사용자 ID가 필요합니다.", new IllegalArgumentException() , EntityValidation.class);
		}

		if (waitingQueue.getSeatDetailId() < 0) {
			CustomException.illegalArgument("유효한 콘서트ScheduleID가 필요합니다.", new IllegalArgumentException() ,
				EntityValidation.class);
		}

		if (waitingQueue.getReservationDt() == null) {
			CustomException.illegalArgument("유효한 예약 날짜가 아닙니다.", new IllegalArgumentException() , EntityValidation.class);
		}

		if (waitingQueue.getWaitingStatus() == null) {
			CustomException.illegalArgument("유효한 대기열 상태가 아닙니다.", new IllegalArgumentException() , EntityValidation.class);
		}

		if (waitingQueue.getPriority() < 0) {
			CustomException.illegalArgument("유효한 대기열 순서가 아닙니다.", new IllegalArgumentException() , EntityValidation.class);
		}

	}

	public static void validateToken(Token token) {
		if (token == null) {
			CustomException.illegalArgument("token는 null일 수 없습니다.", new IllegalArgumentException() , EntityValidation.class);
		}

		if (token.getUserId() < 0) {
			CustomException.illegalArgument("유효한 userID가 아닙니다.", new IllegalArgumentException() , EntityValidation.class);
		}

		if (!StringUtils.hasText(token.getToken())) {
			CustomException.illegalArgument("유효한 Token값이 아닙니다.", new IllegalArgumentException() , EntityValidation.class);
		}

		if (token.getIssuedAt() == null) {
			CustomException.illegalArgument("유효한 발생일이 아닙니다.", new IllegalArgumentException() , EntityValidation.class);
		}

		if (token.getExpiresAt() == null) {
			CustomException.illegalArgument("유효한 만료일이 아닙니다.", new IllegalArgumentException() , EntityValidation.class);
		}

	}
}
