package com.example.hh3week.application.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.hh3week.adapter.in.dto.concert.ConcertDto;
import com.example.hh3week.adapter.in.dto.concert.ConcertScheduleDto;
import com.example.hh3week.application.port.out.ConcertRepositoryPort;
import com.example.hh3week.domain.concert.entity.Concert;
import com.example.hh3week.domain.concert.entity.ConcertSchedule;
import com.example.hh3week.domain.concert.entity.ConcertScheduleStatus;
import com.example.hh3week.domain.concert.entity.ConcertStatus;

class ConcertServiceTest {

	@Mock
	private ConcertRepositoryPort concertRepositoryPort;

	@InjectMocks
	private ConcertService concertService;

	@BeforeEach
	public void setUp(){
		MockitoAnnotations.openMocks(this);
	}

	/*
	 *
	 * 콘서트 목록 조회 api
	 *
	 * */
	@Test
	@DisplayName("콘서트 목록 조회 api")
	void 콘서트_목록_조회_api() {
		// given
		ConcertDto concertDto1 = ConcertDto.builder()
			.concertId(1)
			.concertContent("향플 콘서트1")
			.concertContent("향해플러스 콘서트1")
			.build();

		ConcertDto concertDto2 = ConcertDto.builder()
			.concertId(2)
			.concertContent("향플 콘서트2")
			.concertContent("향해플러스 콘서트1")
			.build();


		ConcertDto concertDto3 = ConcertDto.builder()
			.concertId(3)
			.concertContent("향플 콘서트3")
			.concertContent("향해플러스 콘서트3")
			.build();


		ConcertDto concertDto4 = ConcertDto.builder()
			.concertId(4)
			.concertContent("향플 콘서트4")
			.concertContent("향해플러스 콘서트4")
			.build();

		List<ConcertDto> concertDtoList = List.of(concertDto1, concertDto2, concertDto3, concertDto4);

		// when
		when(concertRepositoryPort.getAvilbleConcertList()).thenReturn(concertDtoList.stream().map(Concert::ToEntity).toList());
		List<ConcertDto> concertDtoList1 = concertService.getAvilbleConcertList();

		// then
		assertThat(concertDtoList1).hasSize(4);
	}


	/*
	* 특정 콘서트만 조회
	*
	* */
	@Test
	@DisplayName("특정 콘서트 조회")
	void 특정_콘서트_조회() {
		// given
		ConcertDto concertDto1 = ConcertDto.builder()
			.concertId(1)
			.concertName("향플 콘서트1")
			.concertContent("향해플러스 콘서트1")
			.build();

		long concertId = 1;

		// when
		when(concertRepositoryPort.getConcertFindById(concertId)).thenReturn(Concert.ToEntity(concertDto1));
		ConcertDto concertDto = concertService.getConcertFindById(concertId);

		// then
		assertThat(concertDto.getConcertId()).isEqualTo(concertId);
		assertThat(concertDto.getConcertName()).isEqualTo("향플 콘서트1");
		assertThat(concertDto.getConcertContent()).isEqualTo("향해플러스 콘서트1");
	}

	@Test
	@DisplayName("예약가능한 콘서트 목록 조회")
	void 예약가능한_콘서트_목록_조회() {
		// given
		ConcertScheduleDto concertScheduleDto1 = ConcertScheduleDto.builder()
			.concertScheduleId(1)
			.concertId(1)
			.concertScheduleStatus(ConcertScheduleStatus.AVAILABLE)
			.endDt(LocalDateTime.now().minusDays(1))
			.startDt(LocalDateTime.now())
			.build();

		ConcertScheduleDto concertScheduleDto2 = ConcertScheduleDto.builder()
			.concertScheduleId(2)
			.concertId(1)
			.concertScheduleStatus(ConcertScheduleStatus.AVAILABLE)
			.endDt(LocalDateTime.now().minusDays(1))
			.startDt(LocalDateTime.now())
			.build();


		ConcertScheduleDto concertScheduleDto3 = ConcertScheduleDto.builder()
			.concertScheduleId(3)
			.concertId(1)
			.concertScheduleStatus(ConcertScheduleStatus.AVAILABLE)
			.endDt(LocalDateTime.now().minusDays(1))
			.startDt(LocalDateTime.now())
			.build();


		ConcertScheduleDto concertScheduleDto4 = ConcertScheduleDto.builder()
			.concertScheduleId(4)
			.concertId(1)
			.concertScheduleStatus(ConcertScheduleStatus.AVAILABLE)
			.endDt(LocalDateTime.now().minusDays(1))
			.startDt(LocalDateTime.now())
			.build();

		List<ConcertScheduleDto> concertScheduleDtoList = List.of(concertScheduleDto1, concertScheduleDto2, concertScheduleDto3, concertScheduleDto4);
		when(concertRepositoryPort.getAvilbleConcertSchedueList()).thenReturn(concertScheduleDtoList.stream().map(ConcertSchedule::ToEntity).toList());

		// when

		List<ConcertScheduleDto> concertScheduleDtoList1 = concertService.getAvilbleConcertScheduletList();

		// then
		assertThat(concertScheduleDtoList1).hasSize(4);
	}

	@Test
	@DisplayName("특정 예약가능한 콘서트 가지고 오기")
	void 특정_예약가능한_콘서트_가지고_오기() {
		// given
		ConcertScheduleDto concertScheduleDto1 = ConcertScheduleDto.builder()
			.concertScheduleId(1)
			.concertId(1)
			.concertScheduleStatus(ConcertScheduleStatus.AVAILABLE)
			.endDt(LocalDateTime.now().minusDays(1))
			.startDt(LocalDateTime.now())
			.build();

		long concertId = 1;

		when(concertRepositoryPort.getConcertScheduleFindById(concertId)).thenReturn(ConcertSchedule.ToEntity(concertScheduleDto1));

		// when
		ConcertScheduleDto concertScheduleDto = concertService.getConcertScheduleFindById(concertId);
		// then
		assertThat(concertScheduleDto.getConcertId()).isEqualTo(concertId);
		assertThat(concertScheduleDto.getConcertScheduleId()).isEqualTo(1);
		assertThat(concertScheduleDto.getConcertScheduleStatus()).isEqualTo(ConcertScheduleStatus.AVAILABLE);

	}

}