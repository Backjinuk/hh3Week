// src/main/java/com/example/hh3week/adapter/in/controller/ReservationController.java

package com.example.hh3week.adapter.in.controller;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.hh3week.adapter.in.dto.concert.ConcertScheduleDto;
import com.example.hh3week.adapter.in.dto.reservation.ReservationSeatDto;
import com.example.hh3week.adapter.in.dto.token.TokenDto;
import com.example.hh3week.application.port.in.ReservationUseCase;
import com.example.hh3week.application.service.kafka.ReservationProducerService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.transaction.Transactional;

@RestController
@RequestMapping("/api/v1")
public class ReservationController {

	private final ReservationUseCase reservationUseCase;
	private final ReservationProducerService reservationProducerService;

	public ReservationController(ReservationUseCase reservationUseCase,
		ReservationProducerService reservationProducerService) {
		this.reservationUseCase = reservationUseCase;
		this.reservationProducerService = reservationProducerService;
	}

	/**
	 * 예약 가능한 좌석 목록을 반환하는 API 엔드포인트입니다.
	 *
	 * @param concertScheduleDto 콘서트 스케줄 ID를 포함한 요청 본문
	 * @return 예약 가능한 좌석 목록
	 */
	@Operation(summary = "예약 가능한 좌석 조회", description = "특정 콘서트 스케줄 ID를 기반으로 예약 가능한 모든 좌석 목록을 조회합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "성공적으로 좌석 목록을 반환",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = ReservationSeatDto.class))),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 데이터",
			content = @Content),
		@ApiResponse(responseCode = "500", description = "서버 오류",
			content = @Content)
	})
	@PostMapping("/reservations/getAvailableReservationSeatList")
	public ResponseEntity<List<ReservationSeatDto>> getAvailableReservationSeatList(
		@Parameter(description = "예약 가능한 좌석 조회를 위한 콘서트 스케줄 ID", required = true)
		@RequestBody ConcertScheduleDto concertScheduleDto) {

		long concertScheduleId = concertScheduleDto.getConcertScheduleId();
		if (concertScheduleId == 0) {
			throw new IllegalArgumentException("concertScheduleId는 필수 입력 항목입니다.");
		}

		List<ReservationSeatDto> availableSeats = reservationUseCase.getAvailableReservationSeatList(concertScheduleId);
		return ResponseEntity.ok(availableSeats);
	}

	/**
	 * 사용자가 좌석 예약을 요청할 때, 대기열을 생성하고 토큰을 발급하는 API 엔드포인트입니다.
	 *
	 * @param requestBody 사용자 ID와 좌석 상세 ID를 포함한 요청 본문
	 * @return 대기열 및 토큰 정보
	 */
	@Operation(summary = "좌석 예약 요청", description = "사용자가 선택한 좌석을 예약하기 위해 대기열을 생성하고 토큰을 발급합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "성공적으로 토큰을 발급",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = TokenDto.class))),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 데이터",
			content = @Content),
		@ApiResponse(responseCode = "500", description = "서버 오류",
			content = @Content)
	})
	@PostMapping("/reservations/reserveSeat")
	@Transactional
	public ResponseEntity<TokenDto> reserveSeat(
		@Parameter(description = "좌석 예약을 위한 사용자 ID와 좌석 상세 ID", required = true)
		@RequestBody Map<String, Long> requestBody) throws ExecutionException, InterruptedException, TimeoutException {

		Long userId = requestBody.get("userId");
		Long seatDetailId = requestBody.get("seatDetailId");

		if (userId == null || seatDetailId == null) {
			throw new IllegalArgumentException("userId와 seatDetailId는 필수 입력 항목입니다.");
		}
		CompletableFuture<TokenDto> future = reservationProducerService.sendReservationRequest(userId, seatDetailId);
		//TokenDto tokenDto = reservationUseCase.reserveSeat(userId, seatDetailId);
		return ResponseEntity.ok(future.get(10, TimeUnit.SECONDS));
	}
}
