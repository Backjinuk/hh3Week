package com.example.hh3week.adapter.in.controller;

import com.example.hh3week.adapter.in.dto.concert.ConcertDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/v1/concerts")
public class ConcertController {

	// Mock concert data
	private static final List<ConcertDto> CONCERTS = Arrays.asList(
		new ConcertDto(1, "Concert A", "Experience the amazing Concert A!"),
		new ConcertDto(2, "Concert B", "Join us for the spectacular Concert B!")
	);

	/**
	 * 모든 콘서트 목록을 조회하는 API
	 *
	 * @return List of ConcertDto
	 */
	@GetMapping
	public ResponseEntity<List<ConcertDto>> getAllConcerts() {
		return new ResponseEntity<>(CONCERTS, HttpStatus.OK);
	}
}
