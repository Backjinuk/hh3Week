package com.example.hh3week.application.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.hh3week.adapter.in.dto.concert.ConcertDto;
import com.example.hh3week.adapter.in.dto.concert.ConcertScheduleDto;
import com.example.hh3week.application.port.out.ConcertRepositoryPort;
import com.example.hh3week.domain.concert.entity.Concert;
import com.example.hh3week.domain.concert.entity.ConcertSchedule;

@Service
public class ConcertService {

	private final ConcertRepositoryPort concertRepositoryPort;

	public ConcertService(ConcertRepositoryPort concertRepositoryPort) {
		this.concertRepositoryPort = concertRepositoryPort;
	}

	/**
	 * 사용 가능한 모든 콘서트 목록을 반환하는 메서드입니다.
	 *
	 * @return 사용 가능한 콘서트들의 리스트
	 */
	public List<ConcertDto> getAvilbleConcertList() {
		return concertRepositoryPort.getAvilbleConcertList().stream()
			.map(ConcertDto::ToDto)
			.toList();
	}

	/**
	 * 지정된 콘서트 ID에 해당하는 콘서트 정보를 반환하는 메서드입니다.
	 *
	 * @param concertId 조회할 콘서트의 고유 ID
	 * @return 지정된 콘서트의 상세 정보
	 */
	public ConcertDto getConcertFindById(long concertId) {
		return ConcertDto.ToDto(concertRepositoryPort.getConcertFindById(concertId));
	}

	/**
	 * 사용 가능한 모든 콘서트 스케줄 목록을 반환하는 메서드입니다.
	 *
	 * @return 사용 가능한 콘서트 스케줄들의 리스트
	 */
	public List<ConcertScheduleDto> getAvilbleConcertScheduletList() {
		return concertRepositoryPort.getAvilbleConcertSchedueList().stream()
			.map(ConcertScheduleDto::ToDto)
			.toList();
	}

	/**
	 * 지정된 콘서트 ID에 해당하는 콘서트 스케줄 정보를 반환하는 메서드입니다.
	 *
	 * @param concertId 조회할 콘서트 스케줄의 콘서트 ID
	 * @return 지정된 콘서트 스케줄의 상세 정보
	 */
	public ConcertScheduleDto getConcertScheduleFindById(long concertId) {
		return ConcertScheduleDto.ToDto(concertRepositoryPort.getConcertScheduleFindById(concertId));
	}

	/**
	 * 특정 날짜 범위 내에 있는 콘서트 스케줄들을 조회하는 메서드입니다.
	 *
	 * @param startDate 조회 시작 날짜 및 시간
	 * @param endDate 조회 종료 날짜 및 시간
	 * @return 지정된 날짜 범위 내의 콘서트 스케줄 리스트
	 */
	@Transactional(readOnly = true)
	public List<ConcertScheduleDto> getConcertsByDate(LocalDateTime startDate, LocalDateTime endDate) {
		return concertRepositoryPort.getConcertsByDate(startDate, endDate).stream()
			.map(ConcertScheduleDto::ToDto)
			.toList();
	}


}
