package com.example.hh3week.application.useCase;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.hh3week.adapter.in.dto.concert.ConcertScheduleDto;
import com.example.hh3week.application.port.in.ConcertUseCase;
import com.example.hh3week.application.service.ConcertService;
import com.example.hh3week.common.config.CustomException;

@Service
public class ConcertUseCaseInteractor implements ConcertUseCase {

	private final ConcertService concertService;

	public ConcertUseCaseInteractor(ConcertService concertService) {
		this.concertService = concertService;
	}

	/*
	 * 예약 가능한 콘서트 날짜 또는 특정 날짜 범위의 콘서트 스케줄을 조회하는 메서드
	 *
	 * @param startDate 시작 날짜 (Optional, null일 경우 모든 예약 가능한 콘서트 조회)
	 * @param endDate   종료 날짜 (Optional, null일 경우 모든 예약 가능한 콘서트 조회)
	 * @return 예약 가능한 콘서트 목록
	 */
	@Override
	public List<ConcertScheduleDto> getAvailableConcertDates(LocalDateTime startDate, LocalDateTime endDate) {
		if (startDate != null && endDate != null) {
			// 특정 날짜 범위로 필터링하여 콘서트를 조회

			if (startDate.isAfter(endDate)) {
				CustomException.illegalArgument("startDate는 endDate보다 이전이어야 합니다.", new IllegalArgumentException(), this.getClass());
			}

			return concertService.getConcertsByDate(startDate, endDate);
		}
		// 날짜 없이 전체 예약 가능한 콘서트 조회
		return concertService.getAvilbleConcertScheduletList();
	}

	@Override
	public ConcertScheduleDto getConcertScheduleFindById(long concertId) {
		return concertService.getConcertScheduleFindById(concertId);
	}

}
