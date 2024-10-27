package com.example.hh3week.application.useCase;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.hh3week.adapter.in.dto.payment.PaymentHistoryDto;
import com.example.hh3week.adapter.in.dto.reservation.ReservationSeatDetailDto;
import com.example.hh3week.adapter.in.dto.reservation.ReservationSeatDto;
import com.example.hh3week.adapter.in.dto.user.UserDto;
import com.example.hh3week.adapter.in.dto.user.UserPointHistoryDto;
import com.example.hh3week.application.port.in.PaymentUseCase;
import com.example.hh3week.application.service.PaymentHistoryService;
import com.example.hh3week.application.service.ReservationService;
import com.example.hh3week.application.service.TokenService;
import com.example.hh3week.application.service.UserService;
import com.example.hh3week.application.service.WaitingQueueService;
import com.example.hh3week.common.config.CustomException;
import com.example.hh3week.domain.payment.entity.PaymentStatus;
import com.example.hh3week.domain.user.entity.PointStatus;
import com.example.hh3week.domain.waitingQueue.entity.WaitingStatus;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PaymentUseCaseInteractor implements PaymentUseCase {

	private final PaymentHistoryService paymentHistoryService;
	private final UserService userService;
	private final WaitingQueueService waitingQueueService;
	private final TokenService tokenService;
	private final ReservationService reservationService;

	public PaymentUseCaseInteractor(PaymentHistoryService paymentHistoryService, UserService userService,
		WaitingQueueService waitingQueueService, TokenService tokenService, ReservationService reservationService) {
		this.paymentHistoryService = paymentHistoryService;
		this.userService = userService;
		this.waitingQueueService = waitingQueueService;
		this.tokenService = tokenService;
		this.reservationService = reservationService;
	}

	/*
	 * 결제 기능
	 *
	 * - [ ]  토큰 값 으로 좌석 상세정보 받기
	 * - [ ]  유저 포인트 차감
	 * - [ ]  죄석 상태값 변환
	 * - [ ]  유저 포인트 히스토리 등록
	 * - [ ]  결제 히스토리 등록
	 * */
	@Override
	@Transactional
	public PaymentHistoryDto registerPaymentHistory(PaymentHistoryDto paymentHistoryDto) {

		// 토큰값 검증
		boolean tokenExpired = tokenService.isTokenExpired(paymentHistoryDto.getToken());

		if (tokenExpired) {
			CustomException.illegalArgument("토큰값이 만료되었습니다.", new IllegalArgumentException(), this.getClass());
		}

		// 토큰의 모든 값 가져오기
		Map<String, Object> tokensAllValue = tokenService.getTokensAllValue(paymentHistoryDto.getToken());

		if (tokensAllValue.isEmpty()) {
			CustomException.illegalArgument("토큰값에 정보를 찾을수 없습니다.", new IllegalArgumentException(), this.getClass());
		}

		// 토큰에서 좌석 상세 정보, 사용자 ID, 큐 순번 추출
		long seatDetailId = Long.parseLong(tokensAllValue.get("seatDetailId").toString());
		long userId = Long.parseLong(tokensAllValue.get("userId").toString());
		long queueOrder = Long.parseLong(tokensAllValue.get("queueOrder").toString());

		// 좌석 상세 정보 받기
		ReservationSeatDetailDto seatDetailDto = reservationService.getSeatDetailById(seatDetailId);

		// 유저 정보 가져오기
		UserDto userDto = userService.getUserInfo(userId);

		// 유저 포인트 차감
		userService.useBalance(userDto, seatDetailDto.getSeatPrice());

		// 좌석 상태값 업데이트
		waitingQueueService.updateStatus(queueOrder, WaitingStatus.EXPIRED);

		// 현재 남은 좌석 갯수 update
		ReservationSeatDto seatDto = reservationService.getSeatById(seatDetailDto.getSeatId());
		reservationService.updateSeatReservation(seatDto);

		// 유저 포인트 히스토리 등록
		UserPointHistoryDto userPointHistoryDto = UserPointHistoryDto.builder()
			.userId(userId)
			.pointDt(LocalDateTime.now())
			.pointAmount(seatDetailDto.getSeatPrice())
			.pointStatus(PointStatus.USE)
			.build();
		userService.addUserPointHistoryInUser(userPointHistoryDto);

		// 결제 히스토리 등록
		PaymentHistoryDto reqHistoryDto = PaymentHistoryDto.builder()
			.reservationId(seatDetailId)
			.paymentAmount(seatDetailDto.getSeatPrice())
			.paymentStatus(PaymentStatus.COMPLETED)
			.userId(userId)
			.build();

		return paymentHistoryService.registerPaymentHistory(reqHistoryDto);
	}


	@Override
	public PaymentHistoryDto getPaymentHistory(long paymentId) {
		return paymentHistoryService.getPaymentHistory(paymentId);
	}

	@Override
	public List<PaymentHistoryDto> getPaymentHistoryByUserId(long userId) {
		return paymentHistoryService.getPaymentHistoryByUserId(userId);
	}
}
