package com.example.hh3week.application.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.example.hh3week.adapter.in.dto.concert.ConcertDto;
import com.example.hh3week.application.port.out.ReservationSeatDetailRepositoryPort;
import com.example.hh3week.application.port.out.ReservationSeatRepositoryPort;
import com.example.hh3week.domain.concert.entity.Concert;
import com.example.hh3week.domain.concert.entity.ConcertStatus;

class ReservationServiceTest {

	@Mock
	private ReservationSeatRepositoryPort reservationSeatRepositoryPort;

	@Mock
	ReservationSeatDetailRepositoryPort reservationSeatDetailRepositoryPort;

	@InjectMocks
	private ReservationService reservationService;





}