package com.example.hh3week.application.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.example.hh3week.application.port.out.ReservationSeatRepositoryPort;

class ReservationServiceTest {

	@Mock
	private ReservationSeatRepositoryPort reservationSeatRepositoryPort;

	@InjectMocks
	private ReservationService reservationService;

	@Test
	@DisplayName("특정 콘서트의 좌석 정보 가지고 오기")
	void 특정_콘서트의() {
		// given

		// when

		// then

	}

}