package com.example.hh3week.integration.adapter.out.persistence;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import com.example.hh3week.application.port.out.WaitingQueueRepositoryPort;
import com.example.hh3week.domain.waitingQueue.entity.WaitingQueue;
import com.example.hh3week.domain.waitingQueue.entity.WaitingStatus;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ActiveProfiles("test")
@SpringBootTest
@Transactional
@Sql({"classpath:schema.sql", "classpath:data.sql"})
public class WaitingQueueRepositoryImplTest {

	@Autowired
	private WaitingQueueRepositoryPort waitingQueueRepositoryImpl;


	@Test
	@DisplayName("대기열에 새로운 항목 추가 - 정상적으로 추가됨")
	void 대기열에새로운항목추가_정상적으로추가됨() {
		// Given
		WaitingQueue newQueue = WaitingQueue.builder()
			.userId(104L)
			.seatDetailId(1L)
			.reservationDt(LocalDateTime.of(2024, 1, 1, 10, 10))
			.waitingStatus(WaitingStatus.WAITING)
			.priority(4L)
			.build();

		// When
		WaitingQueue savedQueue = waitingQueueRepositoryImpl.addToQueue(newQueue);

		// Then
		assertThat(savedQueue.getWaitingId()).isGreaterThan(0);
		assertThat(savedQueue.getUserId()).isEqualTo(104L);
		assertThat(savedQueue.getSeatDetailId()).isEqualTo(1L);
		assertThat(savedQueue.getReservationDt()).isEqualTo(LocalDateTime.of(2024, 1, 1, 10, 10));
		assertThat(savedQueue.getWaitingStatus()).isEqualTo(WaitingStatus.WAITING);
		assertThat(savedQueue.getPriority()).isEqualTo(6L);
	}

	@Test
	@DisplayName("다음 대기열 항목 조회 - 정상적으로 반환됨")
	void 다음대기열항목조회_정상적으로반환됨() {
		// Given
		long seatDetailId = 1L;

		// When
		WaitingQueue nextQueue = waitingQueueRepositoryImpl.getNextInQueue(seatDetailId);

		// Then
		assertThat(nextQueue).isNotNull();
		assertThat(nextQueue.getWaitingId()).isEqualTo(1100L);
		assertThat(nextQueue.getUserId()).isEqualTo(101L);
		assertThat(nextQueue.getWaitingStatus()).isEqualTo(WaitingStatus.WAITING);
	}

	@Test
	@DisplayName("다음 대기열 항목 조회 - 대기열에 항목이 없을 경우 예외 발생")
	void 다음대기열항목조회_대기열에항목이없을경우예외발생() {
		// Given
		long seatDetailId = 1; // 대기열에 항목이 없는 콘서트 스케줄 ID

		// When & Then

		WaitingQueue nextInQueue = waitingQueueRepositoryImpl.getNextInQueue(seatDetailId);

		assertThat(nextInQueue.getPriority()).isEqualTo(5);
	}

	@Test
	@DisplayName("대기열 항목 상태 업데이트 - 정상적으로 업데이트됨")
	void 대기열항목상태업데이트_정상적으로업데이트됨() {
		// Given
		long waitingId = 1100L;
		WaitingStatus newStatus = WaitingStatus.WAITING;

		// When
		waitingQueueRepositoryImpl.updateStatus(waitingId, newStatus);

		// Then
		WaitingQueue updatedQueue = waitingQueueRepositoryImpl.getQueueStatus(101L, 1L);
		assertThat(updatedQueue.getWaitingStatus()).isEqualTo(newStatus);
	}

	@Test
	@DisplayName("대기열 항목 상태 업데이트 - 존재하지 않는 waitingId 시 예외 발생")
	void 대기열항목상태업데이트_존재하지않는waitingId시예외발생() {
		// Given
		long waitingId = 999L;
		WaitingStatus newStatus = WaitingStatus.NOTIFIED;

		// When & Then

		NullPointerException exception = assertThrows(NullPointerException.class, () -> {
			waitingQueueRepositoryImpl.updateStatus(waitingId, newStatus);
		});

		assertThat(exception.getMessage()).isEqualTo("대기열 항목을 찾을 수 없습니다.");
	}

	@Test
	@DisplayName("특정 사용자와 좌석 상세 ID에 대한 대기열 상태 조회 - 정상적으로 반환됨")
	void 특정사용자와좌석상세ID에대한대기열상태조회_정상적으로반환됨() {
		// Given
		long userId = 101L;
		long seatDetailId = 1L;

		// When
		WaitingQueue queueStatus = waitingQueueRepositoryImpl.getQueueStatus(userId, seatDetailId);

		// Then
		assertThat(queueStatus).isNotNull();
		assertThat(queueStatus.getWaitingId()).isEqualTo(1100L);
		assertThat(queueStatus.getUserId()).isEqualTo(101L);
		assertThat(queueStatus.getSeatDetailId()).isEqualTo(1L);
		assertThat(queueStatus.getWaitingStatus()).isEqualTo(WaitingStatus.WAITING);
	}

	@Test
	@DisplayName("특정 사용자와 좌석 상세 ID에 대한 대기열 상태 조회 - 존재하지 않을 경우 예외 발생")
	void 특정사용자와좌석상세ID에대한대기열상태조회_존재하지않을경우예외발생() {
		// Given
		long userId = 999L;
		long seatDetailId = 999L;

		// When & Then

		WaitingQueue queueStatus = waitingQueueRepositoryImpl.getQueueStatus(userId, seatDetailId);

		assertThat(queueStatus).isNull();

	}

	@Test
	@DisplayName("대기열 항목의 위치 조회 - 정상적으로 반환됨")
	void 대기열항목의위치조회_정상적으로반환됨() {
		// Given
		long waitingId = 2200L; // userId 102L, seatDetailId 2L

		// When
		int position = waitingQueueRepositoryImpl.getQueuePosition(waitingId);

		// Then
		assertThat(position).isEqualTo(6); // 대기열에서 두 번째 위치
	}

	@Test
	@DisplayName("대기열 항목의 위치 조회 - 존재하지 않는 waitingId 시 예외 발생")
	void 대기열항목의위치조회_존재하지않는waitingId시예외발생() {
		// Given
		long waitingId = 999L;

		// When & Then
		NullPointerException exception = assertThrows(NullPointerException.class, () -> {
			waitingQueueRepositoryImpl.getQueuePosition(waitingId);
		});

		assertThat(exception.getMessage()).isEqualTo("대기열 항목을 찾을 수 없습니다.");
	}
}
