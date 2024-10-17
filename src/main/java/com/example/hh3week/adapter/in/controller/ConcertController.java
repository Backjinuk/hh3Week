package com.example.hh3week.adapter.in.controller;

import com.example.hh3week.adapter.in.dto.concert.ConcertDto;
import com.example.hh3week.adapter.in.dto.concert.ConcertScheduleDto;
import com.example.hh3week.application.port.in.ConcertUseCase;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.Null;

@RestController
@RequestMapping("/api/v1/concerts")
public class ConcertController {

	private final ConcertUseCase concertUseCase;

	public ConcertController(ConcertUseCase concertUseCase) {
		this.concertUseCase = concertUseCase;
	}


	@Operation(summary = "특정 날짜 범위 내의 사용 가능한 콘서트 조회", description = "특정 시작 날짜와 종료 날짜를 기반으로 사용 가능한 모든 콘서트를 조회합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "성공적으로 콘서트 목록을 반환",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = ConcertScheduleDto.class))),
		@ApiResponse(responseCode = "400", description = "잘못된 요청 데이터",
			content = @Content),
		@ApiResponse(responseCode = "500", description = "서버 오류",
			content = @Content)
	})
	@PostMapping("/getAvailableConcertDates")
	public ResponseEntity<List<ConcertScheduleDto>> getAvailableConcertDates(
		@Parameter(description = "조회 시작 날짜 및 종료 날짜를 포함한 콘서트 스케줄 DTO", required = true)
		@RequestBody ConcertScheduleDto concertScheduleDto) {

		// 날짜 없이 전체 예약 가능한 콘서트 조회
		return ResponseEntity.ok(concertUseCase.getAvailableConcertDates(concertScheduleDto.getStartDt(), concertScheduleDto.getEndDt()));
	}


	@Operation(summary = "특정 콘서트 조회", description = "콘서트 ID를 기반으로 특정 콘서트의 상세 정보를 조회합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "성공적으로 콘서트 정보를 반환",
			content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = ConcertDto.class))),
		@ApiResponse(responseCode = "400", description = "잘못된 콘서트 ID",
			content = @Content),
		@ApiResponse(responseCode = "500", description = "서버 오류",
			content = @Content)
	})
	@PostMapping("/getConcertScheduleFindById")
	public ResponseEntity<ConcertScheduleDto> getConcertScheduleFindById(
		@Parameter(description = "조회할 콘서트의 ID를 포함한 콘서트 스케줄 DTO", required = true)
		@RequestBody ConcertDto concertDto) {

		if(concertDto.getConcertId() <= 0){
			throw new NullPointerException("콘서트 스케줄을 조회할수 업습니다.");
		}

		ConcertScheduleDto concert = concertUseCase.getConcertScheduleFindById(concertDto.getConcertId());
		return ResponseEntity.ok(concert);
	}

}
