package com.example.hh3week.adapter.in.dto.validation;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.example.hh3week.adapter.in.dto.concert.ConcertDto;
import com.example.hh3week.adapter.in.dto.concert.ConcertScheduleDto;
import com.example.hh3week.adapter.in.dto.payment.PaymentHistoryDto;
import com.example.hh3week.adapter.in.dto.reservation.ReservationSeatDetailDto;
import com.example.hh3week.adapter.in.dto.token.TokenDto;
import com.example.hh3week.adapter.in.dto.user.UserDto;
import com.example.hh3week.adapter.in.dto.user.UserPointHistoryDto;
import com.example.hh3week.adapter.in.dto.waitingQueue.WaitingQueueDto;
import com.example.hh3week.domain.concert.entity.ConcertScheduleStatus;
import com.example.hh3week.domain.payment.entity.PaymentStatus;
import com.example.hh3week.domain.reservation.entity.ReservationStatus;
import com.example.hh3week.domain.user.entity.PointStatus;
import com.example.hh3week.domain.user.entity.User;
import com.example.hh3week.domain.waitingQueue.entity.WaitingQueue;
import com.example.hh3week.domain.waitingQueue.entity.WaitingStatus;

class DtoValidationTest {

	@Test
	@DisplayName("concertDto가 validate 통과 성공")
	void concertDto가_validate_통과_성공() {
		// given
		ConcertDto concertDto = new ConcertDto();
		concertDto.setConcertName("향플 주체자");
		concertDto.setConcertContent("향해플러스 콘서트");

		// when
		DtoValidation.validateConcertDto(concertDto);

		// then
		assertThat(concertDto.getConcertName()).isEqualTo("향플 주체자");
		assertThat(concertDto.getConcertContent()).isEqualTo("향해플러스 콘서트");
	}

	@Test
	@DisplayName("conserDto가 null일떼 오류발생")
	void concertDto기_null일때_오류발생() {
		// given
		ConcertDto concertDto = null;

		// then
		assertThatThrownBy(() -> DtoValidation.validateConcertDto(concertDto))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("콘서트 DTO가 null일 수 없습니다.");

	}

	@Test
	@DisplayName("콘서트 이름이 없는 경우 오류발생")
	void 콘서트_이름이_없는_경우_오류발생() {
		// given
		ConcertDto concertDto = new ConcertDto();
		concertDto.setConcertContent("향해플러스 콘서트");

		// then
		assertThatThrownBy(() -> DtoValidation.validateConcertDto(concertDto))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("콘서트 이름은 비워둘 수 없습니다.");

	}

	@Test
	@DisplayName("콘서트 내용이 없는 경우 오류발생")
	void 콘서트_내용이_없는_경우_오류발생() {
		// given
		ConcertDto concertDto = new ConcertDto();
		concertDto.setConcertName("향플 관리잡");

		// then
		assertThatThrownBy(() -> DtoValidation.validateConcertDto(concertDto))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("콘서트 내용은 비워둘 수 없습니다.");

	}

	@Test
	@DisplayName("userDto가 validation 통과")
	void UserDto가_validation_통과() {
		// given
		UserDto userDto = UserDto.builder()
			.userId(1)
			.userName("member")
			.pointBalance(100)
			.build();

		// when
		DtoValidation.validateUserDto(userDto);

		// then
		assertThat(userDto.getUserId()).isEqualTo(1);
		assertThat(userDto.getUserName()).isEqualTo("member");
		assertThat(userDto.getPointBalance()).isEqualTo(100);
	}

	@Test
	@DisplayName("userDto에 이름이 비어있는경우 오류발생")
	void userDto에_이름이_비어있는경우_오류발생() {
		// given
		UserDto userDto = UserDto.builder()
			.pointBalance(1999)
			.build();

		// then
		assertThatThrownBy(() -> DtoValidation.validateUserDto(userDto))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("사용자 이름은 비워둘 수 없습니다.");

	}

	@Test
	@DisplayName("포인트 잔액이 음수일 경우 오류발생")
	void 포은트_잔액이_음수일_경우_오류발생() {
		// given
		UserDto userDto = UserDto.builder().pointBalance(-1).userName("member").build();

		// then
		assertThatThrownBy(() -> DtoValidation.validateUserDto(userDto))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("포인트 잔액은 음수일 수 없습니다.");
	}

	@Test
	@DisplayName("PaymentHistoryDto가 validation 정상 통과")
	void PaymentHistoryDto가_validation_정상_통과() {
		// given
		PaymentHistoryDto paymentHistoryDto = PaymentHistoryDto.builder()
			.paymentAmount(100)
			.userId(1)
			.reservationId(1)
			.paymentStatus(PaymentStatus.CANCELED)
			.build();


		// when
		DtoValidation.validatePaymentHistoryDto(paymentHistoryDto);

		// then
		assertThat(paymentHistoryDto.getPaymentAmount()).isEqualTo(100);
		assertThat(paymentHistoryDto.getReservationId()).isEqualTo(1);
		assertThat(paymentHistoryDto.getPaymentStatus()).isEqualTo(PaymentStatus.CANCELED);
		assertThat(paymentHistoryDto.getUserId()).isEqualTo(1);
	}

	@Test
	@DisplayName("결제금액이 0보다 작을때")
	void 결제금액이_0보다_작을때() {
		// given
		PaymentHistoryDto paymentHistoryDto = PaymentHistoryDto.builder()
			.paymentAmount(-1).build();


		// then
		assertThatThrownBy(() -> DtoValidation.validatePaymentHistoryDto(paymentHistoryDto))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("결제 금액은 0보다 커야 합니다.");
	}

	@Test
	@DisplayName("유효한 사용자가 아닐때 오류발생")
	void 유효한_사용자가_아닐때_오류발생() {
		// given
		PaymentHistoryDto paymentHistoryDto = PaymentHistoryDto.builder()
			.paymentAmount(100)
			.userId(0).build();


		// then
		assertThatThrownBy(() -> DtoValidation.validatePaymentHistoryDto(paymentHistoryDto))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("유효한 사용자 ID가 필요합니다.");
	}

	@Test
	@DisplayName("유효한 예약이 아닐때 오류발생")
	void 유효한_예약이_아닐때_오류발생() {
		// given
		PaymentHistoryDto paymentHistoryDto = PaymentHistoryDto.builder()
			.userId(1)
			.reservationId(-1)
			.paymentAmount(100)
			.build();


		// then
		assertThatThrownBy(() -> DtoValidation.validatePaymentHistoryDto(paymentHistoryDto))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("유효한 예약 ID가 필요합니다.");

	}

	@Test
	@DisplayName("PaymentHistoryDto가 null일때 오류발생")
	void PaymentHistoryDto가_null일때_오류발생() {
		// given
		PaymentHistoryDto paymentHistoryDto = null;

		// when

		// then
		assertThatThrownBy(() -> DtoValidation.validatePaymentHistoryDto(paymentHistoryDto))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("결제 히스토리 DTO가 null일 수 없습니다.");


	}

	@Test
	@DisplayName("좌석 상세정보가 validation 통과")
	void 좌석상제정보가_validation_통과() {
		// given
		ReservationSeatDetailDto reservationSeatDetailDto = ReservationSeatDetailDto.builder()
			.seatDetailId(1)
			.seatId(1)
			.seatCode("A-1")
			.reservationStatus(ReservationStatus.AVAILABLE)
			.seatPrice(100)
			.userId(1)
			.build();

		// when
		DtoValidation.validateReservationSeatDetailDto(reservationSeatDetailDto);

		// then
		assertThat(reservationSeatDetailDto.getSeatId()).isEqualTo(1);
		assertThat(reservationSeatDetailDto.getSeatCode()).isEqualTo("A-1");
		assertThat(reservationSeatDetailDto.getReservationStatus()).isEqualTo(ReservationStatus.AVAILABLE);
		assertThat(reservationSeatDetailDto.getUserId()).isEqualTo(1);
		assertThat(reservationSeatDetailDto.getSeatPrice()).isEqualTo(100);
	}

	@Test
	@DisplayName("유효한 좌석이 아닐때 오류발생")
	void 유효한_좌석이_아닐때_오류발생() {
		// given
		ReservationSeatDetailDto reservationSeatDetailDto = ReservationSeatDetailDto.builder()
			.seatId(-1).build();


		// when

		// then
		assertThatThrownBy(() -> DtoValidation.validateReservationSeatDetailDto(reservationSeatDetailDto))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("유효한 좌석 ID가 필요합니다.");

	}

	@Test
	@DisplayName("좌석 코드가 비어있을때 오류발생")
	void 좌석_코드가_비어있을때_오류발생() {
		// given
		ReservationSeatDetailDto reservationSeatDetailDto = ReservationSeatDetailDto.builder()
			.seatId(1)
			.build();

		// when

		// then
		assertThatThrownBy(() -> DtoValidation.validateReservationSeatDetailDto(reservationSeatDetailDto))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("좌석 코드는 비워둘 수 없습니다.");
	}

	@Test
	@DisplayName("유효한 사용자가 아닐때 오류발생2")
	void 유효한_사용자가_아닐때_오류발생2() {
		// given
	ReservationSeatDetailDto reservationSeatDetailDto = ReservationSeatDetailDto.builder()
		.seatId(1)
		.seatCode("A-1")
		.userId(-1)
		.build();
		// when

		// then
		assertThatThrownBy(() -> DtoValidation.validateReservationSeatDetailDto(reservationSeatDetailDto))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("유효한 사용자 ID가 필요합니다.");
	}

	@Test
	@DisplayName("유효한 좌석 가격이 아닐때 오류발생")
	void 유효한_좌석_가격이_아닐떄_오류발생() {
		// given
		ReservationSeatDetailDto reservationSeatDetailDto = ReservationSeatDetailDto.builder()
			.seatId(1)
			.userId(1)
			.seatCode("A-1")
			.seatPrice(-1).build();

		// when

		// then
		assertThatThrownBy(() -> DtoValidation.validateReservationSeatDetailDto(reservationSeatDetailDto))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("좌석 가격은 0보다 커야 합니다.");

	}

	@Test
	@DisplayName("유효한 예약 상태가 아닐때 오류발생")
	void 유효한_예약_상태가_아닐때_오류발생() {
		// given
		ReservationSeatDetailDto reservationSeatDetailDto = ReservationSeatDetailDto.builder()
			.seatId(1)
			.userId(1)
			.seatPrice(100)
			.seatCode("A-1").build();

		// when

		// then
		assertThatThrownBy(() -> DtoValidation.validateReservationSeatDetailDto(reservationSeatDetailDto))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("예약 상태는 비워둘 수 없습니다.");

	}

	@Test
	@DisplayName("concertSchedule이 validation 정상 통과")
	void concertSchedule이_validation_정상_통과() {
		// given
		ConcertScheduleDto concertScheduleDto = ConcertScheduleDto.builder()
			.concertId(1)
			.concertPrice(100)
			.concertScheduleStatus(ConcertScheduleStatus.SCHEDULED)
			.build();

		// when
		DtoValidation.validateConcertScheduleDto(concertScheduleDto);

		// then
		assertThat(concertScheduleDto.getConcertId()).isEqualTo(1);
		assertThat(concertScheduleDto.getConcertPrice()).isEqualTo(100);
		assertThat(concertScheduleDto.getConcertScheduleStatus()).isEqualTo(ConcertScheduleStatus.SCHEDULED);
	}

	@Test
	@DisplayName("유효한 콘서트ID가 아닐때 오류발생")
	void 유효한_콘서트ID가_아닐때_오류발생() {
		// given
		ConcertScheduleDto concertScheduleDto = ConcertScheduleDto.builder()
			.concertId(-1).build();

		// then
		assertThatThrownBy(() -> DtoValidation.validateConcertScheduleDto(concertScheduleDto))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("유효한 콘서트 ID가 필요합니다.");

	}

	@Test
	@DisplayName("유효한 콘서트 가격이 아닐때 오류발생")
	void 유효한_콘서트_가격이_아닐때_오류발생() {
		// given
		ConcertScheduleDto concertScheduleDto = ConcertScheduleDto.builder()
			.concertId(1)
			.concertPrice(-1).build();

		// then
		assertThatThrownBy(() -> DtoValidation.validateConcertScheduleDto(concertScheduleDto))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("콘서트 가격은 음수일 수 없습니다.");

	}

	@Test
	@DisplayName("유효한 콘서트 일정이 아닐때 오류발생")
	void 유효한_콘서트_일정이_아닐때_오류발생() {
		// given
		ConcertScheduleDto concertScheduleDto = ConcertScheduleDto.builder()
			.concertId(1)
			.concertPrice(100)
			.build();

		// then
		assertThatThrownBy(() -> DtoValidation.validateConcertScheduleDto(concertScheduleDto))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("콘서트 일정 상태는 비워둘 수 없습니다.");
	}

	@Test
	@DisplayName("UserPointHistoryDto가 validation을 무사히 통과")
	void UserPointHistoryDto가_validation을_무사히_통과() {
		// given
		UserPointHistoryDto userPointHistoryDto = UserPointHistoryDto.builder()
			.userId(1)
			.pointAmount(10)
			.pointStatus(PointStatus.EARN)
			.pointDt(LocalDateTime.now())
			.build();

		// when
		DtoValidation.validateUserPointHistoryDto(userPointHistoryDto);

		// then
		assertThat(userPointHistoryDto.getUserId()).isEqualTo(1);
		assertThat(userPointHistoryDto.getPointAmount()).isEqualTo(10);
		assertThat(userPointHistoryDto.getPointStatus()).isEqualTo(PointStatus.EARN);
	}

	@Test
	@DisplayName("유효한 사용자가 아닐때 오류발생3")
	void 유효한_사용자가_아닌때_오류발생3() {
		// given
		UserPointHistoryDto userPointHistoryDto = UserPointHistoryDto.builder()
			.userId(-1)
			.build();

		// when

		// then
		assertThatThrownBy(() -> DtoValidation.validateUserPointHistoryDto(userPointHistoryDto))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("유효한 사용자 ID가 필요합니다.");
	}

	@Test
	@DisplayName("유효한 포인트 충전 금액이 아닐때 오류발생")
	void 유효한_포인트_충전_금액이_아닐때_오류발생() {
		// given
		UserPointHistoryDto userPointHistoryDto = UserPointHistoryDto.builder()
			.userId(1)
			.pointAmount(-1)
			.build();
		// then
		assertThatThrownBy(() -> DtoValidation.validateUserPointHistoryDto(userPointHistoryDto))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("포인트 변화 금액은 음수일 수 없습니다.");
	}

	@Test
	@DisplayName("유효안 Pointstatus가 아닐시 오류발생")
	void 유효한_PoinstSatus가_아닐시_오류발생() {
		// given
		UserPointHistoryDto userPointHistoryDto = UserPointHistoryDto.builder()
			.userId(1)
			.pointAmount(100)
			.build();

		// when

		// then
		assertThatThrownBy(() -> DtoValidation.validateUserPointHistoryDto(userPointHistoryDto))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("포인트 변화 유형은 null일 수 없습니다.");

	}


	@Test
	@DisplayName("유효안 PointDt가 아닐시 오류발생")
	void 유효한_PoinstDt가_아닐시_오류발생() {
		// given
		UserPointHistoryDto userPointHistoryDto = UserPointHistoryDto.builder()
			.userId(1)
			.pointAmount(100)
			.pointStatus(PointStatus.EARN)
			.build();

		// when

		// then
		assertThatThrownBy(() -> DtoValidation.validateUserPointHistoryDto(userPointHistoryDto))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("포인트 변화 시간은 null일 수 없습니다.");

	}

	@Test
	@DisplayName("대기열 queue가 validation 정상 통과")
	void 대기열_queue가_vadlidation_정상_토과() {
		// given
		WaitingQueueDto waitingQueueDto = WaitingQueueDto.builder()
			.userId(1)
			.concertScheduleId(1)
			.waitingStatus(WaitingStatus.WAITING)
			.reservationDt(LocalDateTime.now())
			.priority(1)
			.build();

		// when
		DtoValidation.validateWaitingQueueDto(waitingQueueDto);

		// then
		assertThat(waitingQueueDto.getUserId()).isEqualTo(1);
		assertThat(waitingQueueDto.getConcertScheduleId()).isEqualTo(1);
		assertThat(waitingQueueDto.getWaitingStatus()).isEqualTo(WaitingStatus.WAITING);
		assertThat(waitingQueueDto.getPriority()).isEqualTo(1);
	}

	@Test
	@DisplayName("유효한 사용자 ID가 아닐시 오류발생")
	void 유효한_사용자_ID가_아닐시_오류발생() {
		// given
		WaitingQueueDto waitingQueueDto = WaitingQueueDto.builder()
			.userId(-1)
			.build();

		// when

		// then
		assertThatThrownBy(() -> DtoValidation.validateWaitingQueueDto(waitingQueueDto))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("유효한 사용자 ID가 필요합니다."	);
	}

	@Test
	@DisplayName("유효한 ConcertScheduleId값이 아닐시 오류발생")
	void 유효한_ConcertScheduleID값이_아닐시_오류발생() {
		// given
		WaitingQueueDto waitingQueueDto = WaitingQueueDto.builder()
			.userId(1)
			.concertScheduleId(-1)
			.build();

		// when

		// then
		assertThatThrownBy(() -> DtoValidation.validateWaitingQueueDto(waitingQueueDto))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("유효한 콘서트ScheduleID가 필요합니다.");
	}

	@Test
	@DisplayName("유효한 예약 날짜가 아닐때 오류발생")
	void 유효한_예약_날짜가_아닐때_오류발생() {
		// given
		WaitingQueueDto waitingQueueDto = WaitingQueueDto.builder()
			.userId(1)
			.concertScheduleId(1)
			.build();
		// when

		// then
		assertThatThrownBy(() -> DtoValidation.validateWaitingQueueDto(waitingQueueDto))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("유효한 예약 날짜가 아닙니다.");
	}

	@Test
	@DisplayName("유효한 대기 상태가 아닐시 오류발생")
	void 유효한_대기_상태가_아닐시_오류발생() {
		// given
		WaitingQueueDto waitingQueueDto = WaitingQueueDto.builder()
			.userId(1)
			.concertScheduleId(1)
			.reservationDt(LocalDateTime.now())
			.build();

		// when

		// then
		assertThatThrownBy(() -> DtoValidation.validateWaitingQueueDto(waitingQueueDto))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("유효한 대기열 상태가 아닙니다.");
	}

	@Test
	@DisplayName("유효한 대기열 순서가 아닐때 오류발생")
	void 유효한_대기열_순서가_아닐때_오류발생() {
		// given
		WaitingQueueDto waitingQueueDto = WaitingQueueDto.builder()
			.userId(1)
			.concertScheduleId(1)
			.reservationDt(LocalDateTime.now())
			.waitingStatus(WaitingStatus.WAITING)
			.priority(-1)
			.build();

		// when

		// then
		assertThatThrownBy(() -> DtoValidation.validateWaitingQueueDto(waitingQueueDto))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("유효한 대기열 순서가 아닙니다.");
	}

	// TokenDto 검증 테스트 추가
	@Test
	@DisplayName("TokenDto가 validation 통과 성공")
	void TokenDto가_validation_통과_성공() {
		// given
		TokenDto tokenDto = TokenDto.builder()
			.userId(1)
			.token("validToken123")
			.issuedAt(LocalDateTime.now())
			.expiresAt(LocalDateTime.now().plusHours(1))
			.build();

		// when
		DtoValidation.validateTokenDto(tokenDto);

		// then
		assertThat(tokenDto.getUserId()).isEqualTo(1);
		assertThat(tokenDto.getToken()).isEqualTo("validToken123");
		assertThat(tokenDto.getIssuedAt()).isNotNull();
		assertThat(tokenDto.getExpiresAt()).isNotNull();
	}

	@Test
	@DisplayName("TokenDto가 null일 때 오류 발생")
	void TokenDto가_null일_때_오류발생() {
		// given
		TokenDto tokenDto = null;

		// when & then
		assertThatThrownBy(() -> DtoValidation.validateTokenDto(tokenDto))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("tokenDto는 null일 수 없습니다.");
	}


	@Test
	@DisplayName("비어있는 token 값일 때 오류 발생")
	void 비어있는_token_값일_때_오류발생() {
		// given
		TokenDto tokenDto = TokenDto.builder()
			.userId(1)
			.token(" ") // 비어있는 token
			.issuedAt(LocalDateTime.now())
			.expiresAt(LocalDateTime.now().plusHours(1))
			.build();

		// when & then
		assertThatThrownBy(() -> DtoValidation.validateTokenDto(tokenDto))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("유효한 Token값이 아닙니다.");
	}

	@Test
	@DisplayName("null token 값일 때 오류 발생")
	void null_token_값일_때_오류발생() {
		// given
		TokenDto tokenDto = TokenDto.builder()
			.userId(1)
			.token(null) // null token
			.issuedAt(LocalDateTime.now())
			.expiresAt(LocalDateTime.now().plusHours(1))
			.build();

		// when & then
		assertThatThrownBy(() -> DtoValidation.validateTokenDto(tokenDto))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("유효한 Token값이 아닙니다.");
	}

	@Test
	@DisplayName("null issuedAt 값일 때 오류 발생")
	void null_issuedAt_값일_때_오류발생() {
		// given
		TokenDto tokenDto = TokenDto.builder()
			.userId(1)
			.token("validToken123")
			.issuedAt(null) // null issuedAt
			.expiresAt(LocalDateTime.now().plusHours(1))
			.build();

		// when & then
		assertThatThrownBy(() -> DtoValidation.validateTokenDto(tokenDto))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("유효한 발생일이 아닙니다.");
	}

	@Test
	@DisplayName("null expiresAt 값일 때 오류 발생")
	void null_expiresAt_값일_때_오류발생() {
		// given
		TokenDto tokenDto = TokenDto.builder()
			.userId(1)
			.token("validToken123")
			.issuedAt(LocalDateTime.now())
			.expiresAt(null) // null expiresAt
			.build();

		// when & then
		assertThatThrownBy(() -> DtoValidation.validateTokenDto(tokenDto))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("유효한 만료일이 아닙니다.");
	}
}