package com.example.hh3week.adapter.in.dto.validation;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.example.hh3week.adapter.in.dto.concert.ConcertDto;
import com.example.hh3week.adapter.in.dto.concert.ConcertScheduleDto;
import com.example.hh3week.adapter.in.dto.payment.PaymentHistoryDto;
import com.example.hh3week.adapter.in.dto.reservation.ReservationSeatDetailDto;
import com.example.hh3week.adapter.in.dto.token.TokenDto;
import com.example.hh3week.adapter.in.dto.user.UserDto;
import com.example.hh3week.adapter.in.dto.user.UserPointHistoryDto;
import com.example.hh3week.adapter.in.dto.waitingQueue.WaitingQueueDto;
import com.example.hh3week.common.config.exception.CustomException;

@Component
public class DtoValidation {

	// ConcertDto 검증
	public static void  validateConcertDto(ConcertDto concertDto) {
		if (concertDto == null) {
			CustomException.illegalArgument("콘서트 DTO가 null일 수 없습니다.", new IllegalArgumentException(), DtoValidation.class);
		}
		if (!StringUtils.hasText(concertDto.getConcertName())) {
			CustomException.illegalArgument("콘서트 이름은 비워둘 수 없습니다.", new IllegalArgumentException(), DtoValidation.class);
		}
		if (!StringUtils.hasText(concertDto.getConcertContent())) {
			CustomException.illegalArgument("콘서트 내용은 비워둘 수 없습니다.", new IllegalArgumentException(), DtoValidation.class);
		}
	}

	// UserDto 검증
	public static void  validateUserDto(UserDto userDto) {
		if (userDto == null) {
			CustomException.illegalArgument("사용자 DTO가 null일 수 없습니다.", new IllegalArgumentException(), DtoValidation.class);
		}
		if (!StringUtils.hasText(userDto.getUserName())) {
			CustomException.illegalArgument("사용자 이름은 비워둘 수 없습니다.", new IllegalArgumentException(), DtoValidation.class);
		}
		if (userDto.getPointBalance() < 0) {
			CustomException.illegalArgument("포인트 잔액은 음수일 수 없습니다.", new IllegalArgumentException(), DtoValidation.class);
		}
	}

	// PaymentHistoryDto 검증
	public static void  validatePaymentHistoryDto(PaymentHistoryDto paymentHistoryDto) {
		if (paymentHistoryDto == null) {
			CustomException.illegalArgument("결제 히스토리 DTO가 null일 수 없습니다.", new IllegalArgumentException(), DtoValidation.class);
		}
		if (paymentHistoryDto.getPaymentAmount() <= 0) {
			CustomException.illegalArgument("결제 금액은 0보다 커야 합니다.", new IllegalArgumentException(), DtoValidation.class);
		}
		if ( paymentHistoryDto.getUserId() <= 0) {
			CustomException.illegalArgument("유효한 사용자 ID가 필요합니다.", new IllegalArgumentException(), DtoValidation.class);
		}
		if (paymentHistoryDto.getReservationId() <= 0) {
			CustomException.illegalArgument("유효한 예약 ID가 필요합니다.", new IllegalArgumentException(), DtoValidation.class);
		}
	}

	// ReservationSeatDetailDto 검증
	public static void  validateReservationSeatDetailDto(ReservationSeatDetailDto reservationSeatDetailDto) {
		if (reservationSeatDetailDto == null) {
			CustomException.illegalArgument("예약 좌석 상세 정보 DTO가 null일 수 없습니다.", new IllegalArgumentException(), DtoValidation.class);
		}
		if (reservationSeatDetailDto.getSeatId() <= 0) {
			CustomException.illegalArgument("유효한 좌석 ID가 필요합니다.", new IllegalArgumentException(), DtoValidation.class);
		}
		if (!StringUtils.hasText(reservationSeatDetailDto.getSeatCode())) {
			CustomException.illegalArgument("좌석 코드는 비워둘 수 없습니다.", new IllegalArgumentException(), DtoValidation.class);
		}
		if (reservationSeatDetailDto.getUserId() <= 0) {
			CustomException.illegalArgument("유효한 사용자 ID가 필요합니다.", new IllegalArgumentException(), DtoValidation.class);
		}
		if (reservationSeatDetailDto.getSeatPrice() <= 0) {
			CustomException.illegalArgument("좌석 가격은 0보다 커야 합니다.", new IllegalArgumentException(), DtoValidation.class);
		}
		if (reservationSeatDetailDto.getReservationStatus() == null) {
			CustomException.illegalArgument("예약 상태는 비워둘 수 없습니다.", new IllegalArgumentException(), DtoValidation.class);
		}
	}

	// ConcertScheduleDto 검증
	public static void  validateConcertScheduleDto(ConcertScheduleDto concertScheduleDto) {
		if (concertScheduleDto == null) {
			CustomException.illegalArgument("콘서트 일정 DTO가 null일 수 없습니다.", new IllegalArgumentException(), DtoValidation.class);
		}
		if (concertScheduleDto.getConcertId() <= 0) {
			CustomException.illegalArgument("유효한 콘서트 ID가 필요합니다.", new IllegalArgumentException(), DtoValidation.class);
		}

		if (concertScheduleDto.getConcertPrice() < 0) {
			CustomException.illegalArgument("콘서트 가격은 음수일 수 없습니다.", new IllegalArgumentException(), DtoValidation.class);
		}
		if (concertScheduleDto.getConcertScheduleStatus() == null) {
			CustomException.illegalArgument("콘서트 일정 상태는 비워둘 수 없습니다.", new IllegalArgumentException(), DtoValidation.class);
		}
	}

	// UserPointHistoryDto 검증
	public static void  validateUserPointHistoryDto(UserPointHistoryDto userPointHistoryDto) {
		if (userPointHistoryDto == null) {
			CustomException.illegalArgument("포인트 히스토리 DTO가 null일 수 없습니다.", new IllegalArgumentException(), DtoValidation.class);
		}
		if (userPointHistoryDto.getUserId() <= 0) {
			CustomException.illegalArgument("유효한 사용자 ID가 필요합니다.", new IllegalArgumentException(), DtoValidation.class);
		}
		if (userPointHistoryDto.getPointAmount() < 0) {
			CustomException.illegalArgument("포인트 변화 금액은 음수일 수 없습니다.", new IllegalArgumentException(), DtoValidation.class);
		}
		if (userPointHistoryDto.getPointStatus() == null) {
			CustomException.illegalArgument("포인트 변화 유형은 null일 수 없습니다.", new IllegalArgumentException(), DtoValidation.class);
		}
		if (userPointHistoryDto.getPointDt() == null) {
			CustomException.illegalArgument("포인트 변화 시간은 null일 수 없습니다.", new IllegalArgumentException(), DtoValidation.class);
		}
	}

	public static void validateWaitingQueueDto(WaitingQueueDto waitingQueueDto){

		if(waitingQueueDto == null){
			CustomException.illegalArgument("대기열 Queue는 null일 수 없습니다.", new IllegalArgumentException(), DtoValidation.class);
		}


		if(waitingQueueDto.getUserId() < 0){
			CustomException.illegalArgument("유효한 사용자 ID가 필요합니다.", new IllegalArgumentException(), DtoValidation.class);
		}


		if(waitingQueueDto.getSeatDetailId() < 0){
			CustomException.illegalArgument("유효한 콘서트ScheduleID가 필요합니다.", new IllegalArgumentException(), DtoValidation.class);
		}

		if(waitingQueueDto.getReservationDt() == null){
			CustomException.illegalArgument("유효한 예약 날짜가 아닙니다.", new IllegalArgumentException(), DtoValidation.class);
		}

		if(waitingQueueDto.getWaitingStatus() == null){
			CustomException.illegalArgument("유효한 대기열 상태가 아닙니다.", new IllegalArgumentException(), DtoValidation.class);
		}

		if(waitingQueueDto.getPriority() < 0){
			CustomException.illegalArgument("유효한 대기열 순서가 아닙니다.", new IllegalArgumentException(), DtoValidation.class);
		}

	}

	public static void validateTokenDto(TokenDto tokenDto){
		if(tokenDto == null){
			CustomException.illegalArgument("tokenDto는 null일 수 없습니다.", new IllegalArgumentException(), DtoValidation.class);
		}

		if(tokenDto.getUserId() < 0){
			CustomException.illegalArgument("유효한 userID가 아닙니다.", new IllegalArgumentException(), DtoValidation.class);
		}

		if(!StringUtils.hasText(tokenDto.getToken())){
			CustomException.illegalArgument("유효한 Token값이 아닙니다.", new IllegalArgumentException(), DtoValidation.class);
		}

		if(tokenDto.getIssuedAt() == null){
			CustomException.illegalArgument("유효한 발생일이 아닙니다.", new IllegalArgumentException(), DtoValidation.class);
		}

		if(tokenDto.getExpiresAt() == null){
			CustomException.illegalArgument("유효한 만료일이 아닙니다.", new IllegalArgumentException(), DtoValidation.class);
		}

	}
}