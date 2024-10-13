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

@Component
public class DtoValidation {

	// ConcertDto 검증
	public static void  validateConcertDto(ConcertDto concertDto) {
		if (concertDto == null) {
			throw new IllegalArgumentException("콘서트 DTO가 null일 수 없습니다.");
		}
		if (!StringUtils.hasText(concertDto.getConcertName())) {
			throw new IllegalArgumentException("콘서트 이름은 비워둘 수 없습니다.");
		}
		if (!StringUtils.hasText(concertDto.getConcertContent())) {
			throw new IllegalArgumentException("콘서트 내용은 비워둘 수 없습니다.");
		}
	}

	// UserDto 검증
	public static void  validateUserDto(UserDto userDto) {
		if (userDto == null) {
			throw new IllegalArgumentException("사용자 DTO가 null일 수 없습니다.");
		}
		if (!StringUtils.hasText(userDto.getUserName())) {
			throw new IllegalArgumentException("사용자 이름은 비워둘 수 없습니다.");
		}
		if (userDto.getPointBalance() < 0) {
			throw new IllegalArgumentException("포인트 잔액은 음수일 수 없습니다.");
		}
	}

	// PaymentHistoryDto 검증
	public static void  validatePaymentHistoryDto(PaymentHistoryDto paymentHistoryDto) {
		if (paymentHistoryDto == null) {
			throw new IllegalArgumentException("결제 히스토리 DTO가 null일 수 없습니다.");
		}
		if (paymentHistoryDto.getPaymentAmount() <= 0) {
			throw new IllegalArgumentException("결제 금액은 0보다 커야 합니다.");
		}
		if ( paymentHistoryDto.getUserId() <= 0) {
			throw new IllegalArgumentException("유효한 사용자 ID가 필요합니다.");
		}
		if (paymentHistoryDto.getReservationId() <= 0) {
			throw new IllegalArgumentException("유효한 예약 ID가 필요합니다.");
		}
	}

	// ReservationSeatDetailDto 검증
	public static void  validateReservationSeatDetailDto(ReservationSeatDetailDto reservationSeatDetailDto) {
		if (reservationSeatDetailDto == null) {
			throw new IllegalArgumentException("예약 좌석 상세 정보 DTO가 null일 수 없습니다.");
		}
		if (reservationSeatDetailDto.getSeatId() <= 0) {
			throw new IllegalArgumentException("유효한 좌석 ID가 필요합니다.");
		}
		if (!StringUtils.hasText(reservationSeatDetailDto.getSeatCode())) {
			throw new IllegalArgumentException("좌석 코드는 비워둘 수 없습니다.");
		}
		if (reservationSeatDetailDto.getUserId() <= 0) {
			throw new IllegalArgumentException("유효한 사용자 ID가 필요합니다.");
		}
		if (reservationSeatDetailDto.getSeatPrice() <= 0) {
			throw new IllegalArgumentException("좌석 가격은 0보다 커야 합니다.");
		}
		if (reservationSeatDetailDto.getReservationStatus() == null) {
			throw new IllegalArgumentException("예약 상태는 비워둘 수 없습니다.");
		}
	}

	// ConcertScheduleDto 검증
	public static void  validateConcertScheduleDto(ConcertScheduleDto concertScheduleDto) {
		if (concertScheduleDto == null) {
			throw new IllegalArgumentException("콘서트 일정 DTO가 null일 수 없습니다.");
		}
		if (concertScheduleDto.getConcertId() <= 0) {
			throw new IllegalArgumentException("유효한 콘서트 ID가 필요합니다.");
		}

		if (concertScheduleDto.getConcertPrice() < 0) {
			throw new IllegalArgumentException("콘서트 가격은 음수일 수 없습니다.");
		}
		if (concertScheduleDto.getConcertScheduleStatus() == null) {
			throw new IllegalArgumentException("콘서트 일정 상태는 비워둘 수 없습니다.");
		}
	}

	// UserPointHistoryDto 검증
	public static void  validateUserPointHistoryDto(UserPointHistoryDto userPointHistoryDto) {
		if (userPointHistoryDto == null) {
			throw new IllegalArgumentException("포인트 히스토리 DTO가 null일 수 없습니다.");
		}
		if (userPointHistoryDto.getUserId() <= 0) {
			throw new IllegalArgumentException("유효한 사용자 ID가 필요합니다.");
		}
		if (userPointHistoryDto.getPointAmount() < 0) {
			throw new IllegalArgumentException("포인트 변화 금액은 음수일 수 없습니다.");
		}
		if (userPointHistoryDto.getPointStatus() == null) {
			throw new IllegalArgumentException("포인트 변화 유형은 null일 수 없습니다.");
		}
		if (userPointHistoryDto.getPointDt() == null) {
			throw new IllegalArgumentException("포인트 변화 시간은 null일 수 없습니다.");
		}
	}

	public static void validateWaitingQueueDto(WaitingQueueDto waitingQueueDto){

		if(waitingQueueDto == null){
			throw new IllegalArgumentException("대기열 Queue는 null일 수 없습니다.");
		}


		if(waitingQueueDto.getUserId() < 0){
			throw new IllegalArgumentException("유효한 사용자 ID가 필요합니다.");
		}


		if(waitingQueueDto.getConcertScheduleId() < 0){
			throw new IllegalArgumentException("유효한 콘서트ScheduleID가 필요합니다.");
		}

		if(waitingQueueDto.getReservationDt() == null){
			throw new IllegalArgumentException("유효한 예약 날짜가 아닙니다.");
		}

		if(waitingQueueDto.getWaitingStatus() == null){
			throw new IllegalArgumentException("유효한 대기열 상태가 아닙니다.");
		}

		if(waitingQueueDto.getPriority() < 0){
			throw new IllegalArgumentException("유효한 대기열 순서가 아닙니다.");
		}

	}

	public static void validateTokenDto(TokenDto tokenDto){
		if(tokenDto == null){
			throw new  IllegalArgumentException("tokenDto는 null일 수 없습니다.");
		}

		if(tokenDto.getUserId() < 0){
			throw new  IllegalArgumentException("유효한 userID가 아닙니다.");
		}

		if(!StringUtils.hasText(tokenDto.getToken())){
			throw new  IllegalArgumentException("유효한 Token값이 아닙니다.");
		}

		if(tokenDto.getIssuedAt() == null){
			throw new  IllegalArgumentException("유효한 발생일이 아닙니다.");
		}

		if(tokenDto.getExpiresAt() == null){
			throw new  IllegalArgumentException("유효한 만료일이 아닙니다.");
		}

	}
}