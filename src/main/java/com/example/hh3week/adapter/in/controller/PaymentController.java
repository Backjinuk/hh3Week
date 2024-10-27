package com.example.hh3week.adapter.in.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.hh3week.adapter.in.dto.payment.PaymentHistoryDto;
import com.example.hh3week.adapter.in.dto.user.UserDto;
import com.example.hh3week.application.port.in.PaymentUseCase;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/v1/payment")
public class PaymentController {

	private final PaymentUseCase paymentUseCase;

	public PaymentController(PaymentUseCase paymentUseCase) {
		this.paymentUseCase = paymentUseCase;
	}

	/**
	 * 결제 처리 API
	 *
	 * @param paymentHistoryDto 결제 요청 정보
	 * @return 결제 내역
	 */
	@Operation(summary = "결제 처리", description = "결제 요청 정보를 받아 결제를 처리합니다.")
	@ApiResponses(value = {@ApiResponse(responseCode = "200", description = "성공적으로 결제가 처리되었습니다."),
		@ApiResponse(responseCode = "400", description = "잘못된 요청입니다.")})
	@PostMapping("/registerPaymentHistory")
	public ResponseEntity<PaymentHistoryDto> registerPaymentHistory(
		@RequestBody @Parameter(description = "결제 요청 정보", required = true) PaymentHistoryDto paymentHistoryDto) {
		paymentUseCase.registerPaymentHistory(paymentHistoryDto);
		return new ResponseEntity<>(paymentHistoryDto, HttpStatus.OK);
	}

	/**
	 * 결제 내역 조회 API
	 *
	 * @param paymentHistoryDto 결제 ID
	 * @return 결제 내역
	 */
	@Operation(summary = "결제 내역 조회", description = "결제 ID를 기반으로 결제 내역을 조회합니다.")
	@ApiResponses(value = {@ApiResponse(responseCode = "200", description = "성공적으로 결제 내역을 조회했습니다."),
		@ApiResponse(responseCode = "404", description = "결제 내역을 찾을 수 없습니다.")})
	@PostMapping("/getPaymentHistory")
	public ResponseEntity<PaymentHistoryDto> getPaymentHistory(
		@RequestBody @Parameter(description = "조회할 결제 ID", required = true) PaymentHistoryDto paymentHistoryDto) {
		PaymentHistoryDto result = paymentUseCase.getPaymentHistory(paymentHistoryDto.getPaymentId());
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	/**
	 * 유저 ID로 결제 내역 조회 API
	 *
	 * @param userDto 유저 ID
	 * @return 결제 내역 리스트
	 */
	@Operation(summary = "유저 ID로 결제 내역 조회", description = "유저 ID를 기반으로 결제 내역을 조회합니다.")
	@ApiResponses(value = {@ApiResponse(responseCode = "200", description = "성공적으로 결제 내역을 조회했습니다."),
		@ApiResponse(responseCode = "404", description = "결제 내역을 찾을 수 없습니다.")})
	@PostMapping("/getPaymentHistoryByUserId")
	public ResponseEntity<List<PaymentHistoryDto>> getPaymentHistoryByUserId(
		@RequestBody @Parameter(description = "유저 ID", required = true) UserDto userDto) {
		List<PaymentHistoryDto> paymentHistoryByUserId = paymentUseCase.getPaymentHistoryByUserId(userDto.getUserId());
		return ResponseEntity.ok(paymentHistoryByUserId);
	}
}
