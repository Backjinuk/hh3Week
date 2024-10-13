package com.example.hh3week.domain.validation;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


import com.example.hh3week.domain.concert.entity.Concert;
import com.example.hh3week.domain.concert.entity.ConcertSchedule;
import com.example.hh3week.domain.concert.entity.ConcertScheduleStatus;
import com.example.hh3week.domain.payment.entity.PaymentHistory;
import com.example.hh3week.domain.payment.entity.PaymentStatus;
import com.example.hh3week.domain.reservation.entity.ReservationSeatDetail;
import com.example.hh3week.domain.reservation.entity.ReservationStatus;
import com.example.hh3week.domain.token.entity.Token;
import com.example.hh3week.domain.user.entity.PointStatus;
import com.example.hh3week.domain.user.entity.User;
import com.example.hh3week.domain.user.entity.UserPointHistory;
import com.example.hh3week.domain.waitingQueue.entity.WaitingQueue;
import com.example.hh3week.domain.waitingQueue.entity.WaitingStatus;

class EntityValidationTest {
	@Test
	@DisplayName("concert가 validate 통과 성공")
	void concert가_validate_통과_성공() {
		// given
		Concert concert = Concert.builder().build();

		concert.setConcertName("향플 주체자");
		concert.setConcertContent("향해플러스 콘서트");

		// when
		EntityValidation.validateConcert(concert);

		// then
		assertThat(concert.getConcertName()).isEqualTo("향플 주체자");
		assertThat(concert.getConcertContent()).isEqualTo("향해플러스 콘서트");
	}

	@Test
	@DisplayName("conser가 null일떼 오류발생")
	void concert기_null일때_오류발생() {
		// given
		Concert concert = null;

		// then
		assertThatThrownBy(() -> EntityValidation.validateConcert(concert))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("콘서트 Entity가 null일 수 없습니다.");

	}

	@Test
	@DisplayName("콘서트 이름이 없는 경우 오류발생")
	void 콘서트_이름이_없는_경우_오류발생() {
		// given
		Concert concert = Concert.builder().build();
		concert.setConcertContent("향해플러스 콘서트");

		// then
		assertThatThrownBy(() -> EntityValidation.validateConcert(concert))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("콘서트 이름은 비워둘 수 없습니다.");

	}

	@Test
	@DisplayName("콘서트 내용이 없는 경우 오류발생")
	void 콘서트_내용이_없는_경우_오류발생() {
		// given
		Concert concert = Concert.builder().build();
		concert.setConcertName("향플 관리잡");

		// then
		assertThatThrownBy(() -> EntityValidation.validateConcert(concert))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("콘서트 내용은 비워둘 수 없습니다.");

	}

	@Test
	@DisplayName("user가 validation 통과")
	void User가_validation_통과() {
		// given
		User user = User.builder()
			.userId(1)
			.userName("member")
			.pointBalance(100)
			.build();

		// when
		EntityValidation.validateUser(user);

		// then
		assertThat(user.getUserId()).isEqualTo(1);
		assertThat(user.getUserName()).isEqualTo("member");
		assertThat(user.getPointBalance()).isEqualTo(100);
	}

	@Test
	@DisplayName("user에 이름이 비어있는경우 오류발생")
	void user에_이름이_비어있는경우_오류발생() {
		// given
		User user = User.builder()
			.pointBalance(1999)
			.build();

		// then
		assertThatThrownBy(() -> EntityValidation.validateUser(user))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("사용자 이름은 비워둘 수 없습니다.");

	}

	@Test
	@DisplayName("포인트 잔액이 음수일 경우 오류발생")
	void 포은트_잔액이_음수일_경우_오류발생() {
		// given
		User user = User.builder().pointBalance(-1).userName("member").build();

		// then
		assertThatThrownBy(() -> EntityValidation.validateUser(user))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("포인트 잔액은 음수일 수 없습니다.");
	}

	@Test
	@DisplayName("PaymentHistory가 validation 정상 통과")
	void PaymentHistory가_validation_정상_통과() {
		// given
		PaymentHistory paymentHistory = PaymentHistory.builder()
			.paymentAmount(100)
			.userId(1)
			.reservationId(1)
			.paymentStatus(PaymentStatus.CANCELED)
			.build();

		// when
		EntityValidation.validatePaymentHistory(paymentHistory);

		// then
		assertThat(paymentHistory.getPaymentAmount()).isEqualTo(100);
		assertThat(paymentHistory.getReservationId()).isEqualTo(1);
		assertThat(paymentHistory.getPaymentStatus()).isEqualTo(PaymentStatus.CANCELED);
		assertThat(paymentHistory.getUserId()).isEqualTo(1);
	}

	@Test
	@DisplayName("결제금액이 0보다 작을때")
	void 결제금액이_0보다_작을때() {
		// given
		PaymentHistory paymentHistory = PaymentHistory.builder()
			.paymentAmount(-1).build();

		// then
		assertThatThrownBy(() -> EntityValidation.validatePaymentHistory(paymentHistory))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("결제 금액은 0보다 커야 합니다.");
	}

	@Test
	@DisplayName("유효한 사용자가 아닐때 오류발생")
	void 유효한_사용자가_아닐때_오류발생() {
		// given
		PaymentHistory paymentHistory = PaymentHistory.builder()
			.paymentAmount(100)
			.userId(0).build();

		// then
		assertThatThrownBy(() -> EntityValidation.validatePaymentHistory(paymentHistory))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("유효한 사용자 ID가 필요합니다.");
	}

	@Test
	@DisplayName("유효한 예약이 아닐때 오류발생")
	void 유효한_예약이_아닐때_오류발생() {
		// given
		PaymentHistory paymentHistory = PaymentHistory.builder()
			.userId(1)
			.reservationId(-1)
			.paymentAmount(100)
			.build();

		// then
		assertThatThrownBy(() -> EntityValidation.validatePaymentHistory(paymentHistory))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("유효한 예약 ID가 필요합니다.");

	}

	@Test
	@DisplayName("PaymentHistory가 null일때 오류발생")
	void PaymentHistory가_null일때_오류발생() {
		// given
		PaymentHistory paymentHistory = null;

		// when

		// then
		assertThatThrownBy(() -> EntityValidation.validatePaymentHistory(paymentHistory))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("결제 히스토리 Entity가 null일 수 없습니다.");

	}

	@Test
	@DisplayName("좌석 상세정보가 validation 통과")
	void 좌석상제정보가_validation_통과() {
		// given
		ReservationSeatDetail reservationSeatDetail = ReservationSeatDetail.builder()
			.seatDetailId(1)
			.seatId(1)
			.seatCode("A-1")
			.reservationStatus(ReservationStatus.AVAILABLE)
			.seatPrice(100)
			.userId(1)
			.build();

		// when
		EntityValidation.validateReservationSeatDetail(reservationSeatDetail);

		// then
		assertThat(reservationSeatDetail.getSeatId()).isEqualTo(1);
		assertThat(reservationSeatDetail.getSeatCode()).isEqualTo("A-1");
		assertThat(reservationSeatDetail.getReservationStatus()).isEqualTo(ReservationStatus.AVAILABLE);
		assertThat(reservationSeatDetail.getUserId()).isEqualTo(1);
		assertThat(reservationSeatDetail.getSeatPrice()).isEqualTo(100);
	}

	@Test
	@DisplayName("유효한 좌석이 아닐때 오류발생")
	void 유효한_좌석이_아닐때_오류발생() {
		// given
		ReservationSeatDetail reservationSeatDetail = ReservationSeatDetail.builder()
			.seatId(-1).build();

		// when

		// then
		assertThatThrownBy(() -> EntityValidation.validateReservationSeatDetail(reservationSeatDetail))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("유효한 좌석 ID가 필요합니다.");

	}

	@Test
	@DisplayName("좌석 코드가 비어있을때 오류발생")
	void 좌석_코드가_비어있을때_오류발생() {
		// given
		ReservationSeatDetail reservationSeatDetail = ReservationSeatDetail.builder()
			.seatId(1)
			.build();

		// when

		// then
		assertThatThrownBy(() -> EntityValidation.validateReservationSeatDetail(reservationSeatDetail))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("좌석 코드는 비워둘 수 없습니다.");
	}

	@Test
	@DisplayName("유효한 사용자가 아닐때 오류발생2")
	void 유효한_사용자가_아닐때_오류발생2() {
		// given
		ReservationSeatDetail reservationSeatDetail = ReservationSeatDetail.builder()
			.seatId(1)
			.seatCode("A-1")
			.userId(-1)
			.build();
		// when

		// then
		assertThatThrownBy(() -> EntityValidation.validateReservationSeatDetail(reservationSeatDetail))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("유효한 사용자 ID가 필요합니다.");
	}

	@Test
	@DisplayName("유효한 좌석 가격이 아닐때 오류발생")
	void 유효한_좌석_가격이_아닐떄_오류발생() {
		// given
		ReservationSeatDetail reservationSeatDetail = ReservationSeatDetail.builder()
			.seatId(1)
			.userId(1)
			.seatCode("A-1")
			.seatPrice(-1).build();

		// when

		// then
		assertThatThrownBy(() -> EntityValidation.validateReservationSeatDetail(reservationSeatDetail))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("좌석 가격은 0보다 커야 합니다.");

	}

	@Test
	@DisplayName("유효한 예약 상태가 아닐때 오류발생")
	void 유효한_예약_상태가_아닐때_오류발생() {
		// given
		ReservationSeatDetail reservationSeatDetail = ReservationSeatDetail.builder()
			.seatId(1)
			.userId(1)
			.seatPrice(100)
			.seatCode("A-1").build();

		// when

		// then
		assertThatThrownBy(() -> EntityValidation.validateReservationSeatDetail(reservationSeatDetail))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("예약 상태는 비워둘 수 없습니다.");

	}

	@Test
	@DisplayName("concertSchedule이 validation 정상 통과")
	void concertSchedule이_validation_정상_통과() {
		// given
		ConcertSchedule concertSchedule = ConcertSchedule.builder()
			.concertId(1)
			.concertPrice(100)
			.concertScheduleStatus(ConcertScheduleStatus.SCHEDULED)
			.build();

		// when
		EntityValidation.validateConcertSchedule(concertSchedule);

		// then
		assertThat(concertSchedule.getConcertId()).isEqualTo(1);
		assertThat(concertSchedule.getConcertPrice()).isEqualTo(100);
		assertThat(concertSchedule.getConcertScheduleStatus()).isEqualTo(ConcertScheduleStatus.SCHEDULED);
	}

	@Test
	@DisplayName("유효한 콘서트ID가 아닐때 오류발생")
	void 유효한_콘서트ID가_아닐때_오류발생() {
		// given
		ConcertSchedule concertSchedule = ConcertSchedule.builder()
			.concertId(-1).build();

		// then
		assertThatThrownBy(() -> EntityValidation.validateConcertSchedule(concertSchedule))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("유효한 콘서트 ID가 필요합니다.");

	}

	@Test
	@DisplayName("유효한 콘서트 가격이 아닐때 오류발생")
	void 유효한_콘서트_가격이_아닐때_오류발생() {
		// given
		ConcertSchedule concertSchedule = ConcertSchedule.builder()
			.concertId(1)
			.concertPrice(-1).build();

		// then
		assertThatThrownBy(() -> EntityValidation.validateConcertSchedule(concertSchedule))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("콘서트 가격은 음수일 수 없습니다.");

	}

	@Test
	@DisplayName("유효한 콘서트 일정이 아닐때 오류발생")
	void 유효한_콘서트_일정이_아닐때_오류발생() {
		// given
		ConcertSchedule concertSchedule = ConcertSchedule.builder()
			.concertId(1)
			.concertPrice(100)
			.build();

		// then
		assertThatThrownBy(() -> EntityValidation.validateConcertSchedule(concertSchedule))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("콘서트 일정 상태는 비워둘 수 없습니다.");
	}

	@Test
	@DisplayName("UserPointHistory가 validation을 무사히 통과")
	void UserPointHistory가_validation을_무사히_통과() {
		// given
		UserPointHistory userPointHistory = UserPointHistory.builder()
			.userId(1)
			.pointAmount(10)
			.pointStatus(PointStatus.EARN)
			.pointDt(LocalDateTime.now())
			.build();

		// when
		EntityValidation.validateUserPointHistory(userPointHistory);

		// then
		assertThat(userPointHistory.getUserId()).isEqualTo(1);
		assertThat(userPointHistory.getPointAmount()).isEqualTo(10);
		assertThat(userPointHistory.getPointStatus()).isEqualTo(PointStatus.EARN);
	}

	@Test
	@DisplayName("유효한 사용자가 아닐때 오류발생3")
	void 유효한_사용자가_아닌때_오류발생3() {
		// given
		UserPointHistory userPointHistory = UserPointHistory.builder()
			.userId(-1)
			.build();

		// when

		// then
		assertThatThrownBy(() -> EntityValidation.validateUserPointHistory(userPointHistory))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("유효한 사용자 ID가 필요합니다.");
	}

	@Test
	@DisplayName("유효한 포인트 충전 금액이 아닐때 오류발생")
	void 유효한_포인트_충전_금액이_아닐때_오류발생() {
		// given
		UserPointHistory userPointHistory = UserPointHistory.builder()
			.userId(1)
			.pointAmount(-1)
			.build();
		// then
		assertThatThrownBy(() -> EntityValidation.validateUserPointHistory(userPointHistory))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("포인트 변화 금액은 음수일 수 없습니다.");
	}

	@Test
	@DisplayName("유효안 Pointstatus가 아닐시 오류발생")
	void 유효한_PoinstSatus가_아닐시_오류발생() {
		// given
		UserPointHistory userPointHistory = UserPointHistory.builder()
			.userId(1)
			.pointAmount(100)
			.build();

		// when

		// then
		assertThatThrownBy(() -> EntityValidation.validateUserPointHistory(userPointHistory))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("포인트 변화 유형은 null일 수 없습니다.");

	}

	@Test
	@DisplayName("유효안 PointDt가 아닐시 오류발생")
	void 유효한_PoinstDt가_아닐시_오류발생() {
		// given
		UserPointHistory userPointHistory = UserPointHistory.builder()
			.userId(1)
			.pointAmount(100)
			.pointStatus(PointStatus.EARN)
			.build();

		// when

		// then
		assertThatThrownBy(() -> EntityValidation.validateUserPointHistory(userPointHistory))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("포인트 변화 시간은 null일 수 없습니다.");

	}

	@Test
	@DisplayName("대기열 queue가 validation 정상 통과")
	void 대기열_queue가_vadlidation_정상_토과() {
		// given
		WaitingQueue waitingQueue = WaitingQueue.builder()
			.userId(1)
			.concertScheduleId(1)
			.waitingStatus(WaitingStatus.WAITING)
			.reservationDt(LocalDateTime.now())
			.priority(1)
			.build();

		// when
		EntityValidation.validateWaitingQueue(waitingQueue);

		// then
		assertThat(waitingQueue.getUserId()).isEqualTo(1);
		assertThat(waitingQueue.getConcertScheduleId()).isEqualTo(1);
		assertThat(waitingQueue.getWaitingStatus()).isEqualTo(WaitingStatus.WAITING);
		assertThat(waitingQueue.getPriority()).isEqualTo(1);
	}

	@Test
	@DisplayName("유효한 사용자 ID가 아닐시 오류발생")
	void 유효한_사용자_ID가_아닐시_오류발생() {
		// given
		WaitingQueue waitingQueue = WaitingQueue.builder()
			.userId(-1)
			.build();

		// when

		// then
		assertThatThrownBy(() -> EntityValidation.validateWaitingQueue(waitingQueue))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("유효한 사용자 ID가 필요합니다.");
	}

	@Test
	@DisplayName("유효한 ConcertScheduleId값이 아닐시 오류발생")
	void 유효한_ConcertScheduleID값이_아닐시_오류발생() {
		// given
		WaitingQueue waitingQueue = WaitingQueue.builder()
			.userId(1)
			.concertScheduleId(-1)
			.build();

		// when

		// then
		assertThatThrownBy(() -> EntityValidation.validateWaitingQueue(waitingQueue))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("유효한 콘서트ScheduleID가 필요합니다.");
	}

	@Test
	@DisplayName("유효한 예약 날짜가 아닐때 오류발생")
	void 유효한_예약_날짜가_아닐때_오류발생() {
		// given
		WaitingQueue waitingQueue = WaitingQueue.builder()
			.userId(1)
			.concertScheduleId(1)
			.build();
		// when

		// then
		assertThatThrownBy(() -> EntityValidation.validateWaitingQueue(waitingQueue))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("유효한 예약 날짜가 아닙니다.");
	}

	@Test
	@DisplayName("유효한 대기 상태가 아닐시 오류발생")
	void 유효한_대기_상태가_아닐시_오류발생() {
		// given
		WaitingQueue waitingQueue = WaitingQueue.builder()
			.userId(1)
			.concertScheduleId(1)
			.reservationDt(LocalDateTime.now())
			.build();

		// when

		// then
		assertThatThrownBy(() -> EntityValidation.validateWaitingQueue(waitingQueue))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("유효한 대기열 상태가 아닙니다.");
	}

	@Test
	@DisplayName("유효한 대기열 순서가 아닐때 오류발생")
	void 유효한_대기열_순서가_아닐때_오류발생() {
		// given
		WaitingQueue waitingQueue = WaitingQueue.builder()
			.userId(1)
			.concertScheduleId(1)
			.reservationDt(LocalDateTime.now())
			.waitingStatus(WaitingStatus.WAITING)
			.priority(-1)
			.build();

		// when

		// then
		assertThatThrownBy(() -> EntityValidation.validateWaitingQueue(waitingQueue))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("유효한 대기열 순서가 아닙니다.");
	}

	// Token 검증 테스트 추가
	@Test
	@DisplayName("Token가 validation 통과 성공")
	void Token가_validation_통과_성공() {
		// given
		Token token = Token.builder()
			.userId(1)
			.token("validToken123")
			.issuedAt(LocalDateTime.now())
			.expiresAt(LocalDateTime.now().plusHours(1))
			.build();

		// when
		EntityValidation.validateToken(token);

		// then
		assertThat(token.getUserId()).isEqualTo(1);
		assertThat(token.getToken()).isEqualTo("validToken123");
		assertThat(token.getIssuedAt()).isNotNull();
		assertThat(token.getExpiresAt()).isNotNull();
	}

	@Test
	@DisplayName("Token가 null일 때 오류 발생")
	void Token가_null일_때_오류발생() {
		// given
		Token token = null;

		// when & then
		assertThatThrownBy(() -> EntityValidation.validateToken(token))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("token는 null일 수 없습니다.");
	}

	@Test
	@DisplayName("비어있는 token 값일 때 오류 발생")
	void 비어있는_token_값일_때_오류발생() {
		// given
		Token token = Token.builder()
			.userId(1)
			.token(" ") // 비어있는 token
			.issuedAt(LocalDateTime.now())
			.expiresAt(LocalDateTime.now().plusHours(1))
			.build();

		// when & then
		assertThatThrownBy(() -> EntityValidation.validateToken(token))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("유효한 Token값이 아닙니다.");
	}

	@Test
	@DisplayName("null token 값일 때 오류 발생")
	void null_token_값일_때_오류발생() {
		// given
		Token token = Token.builder()
			.userId(1)
			.token(null) // null token
			.issuedAt(LocalDateTime.now())
			.expiresAt(LocalDateTime.now().plusHours(1))
			.build();

		// when & then
		assertThatThrownBy(() -> EntityValidation.validateToken(token))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("유효한 Token값이 아닙니다.");
	}

	@Test
	@DisplayName("null issuedAt 값일 때 오류 발생")
	void null_issuedAt_값일_때_오류발생() {
		// given
		Token token = Token.builder()
			.userId(1)
			.token("validToken123")
			.issuedAt(null) // null issuedAt
			.expiresAt(LocalDateTime.now().plusHours(1))
			.build();

		// when & then
		assertThatThrownBy(() -> EntityValidation.validateToken(token))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("유효한 발생일이 아닙니다.");
	}

	@Test
	@DisplayName("null expiresAt 값일 때 오류 발생")
	void null_expiresAt_값일_때_오류발생() {
		// given
		Token token = Token.builder()
			.userId(1)
			.token("validToken123")
			.issuedAt(LocalDateTime.now())
			.expiresAt(null) // null expiresAt
			.build();

		// when & then
		assertThatThrownBy(() -> EntityValidation.validateToken(token))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("유효한 만료일이 아닙니다.");

	}
}