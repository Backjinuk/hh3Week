package com.example.hh3week.unit.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.hh3week.adapter.in.dto.waitingQueue.WaitingQueueDto;
import com.example.hh3week.application.port.out.WaitingQueueRepositoryPort;
import com.example.hh3week.application.service.WaitingQueueService;
import com.example.hh3week.domain.waitingQueue.entity.WaitingQueue;
import com.example.hh3week.domain.waitingQueue.entity.WaitingStatus;

public class WaitingQueueServiceTest {

	@Mock
	private WaitingQueueRepositoryPort waitingQueueRepository;

	@InjectMocks
	private WaitingQueueService waitingQueueService;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	@DisplayName("사용자가 대기열에 있는 경우 isUserInQueue 메서드는 true를 반환한다.")
	public void 사용자가_대기열에_있는_경우_isUserInQueue_메서드는_true를_반환한다() {
		long userId = 1L;
		long seatDetailId = 100L;

		WaitingQueue existingQueue = WaitingQueue.builder()
			.waitingId(1L)
			.userId(userId)
			.seatDetailId(seatDetailId)
			.waitingStatus(WaitingStatus.WAITING)
			.priority(1L)
			.reservationDt(LocalDateTime.now())
			.build();

		when(waitingQueueRepository.getQueueStatus(userId, seatDetailId)).thenReturn(existingQueue);

		boolean result = waitingQueueService.isUserInQueue(userId, seatDetailId);

		assertTrue(result);
		verify(waitingQueueRepository, times(1)).getQueueStatus(userId, seatDetailId);
	}

	@Test
	@DisplayName("사용자가 대기열에 없는 경우 isUserInQueue 메서드는 false를 반환한다.")
	public void 사용자가_대기열에_없는_경우_isUserInQueue_메서드는_false를_반환한다() {
		long userId = 2L;
		long seatDetailId = 200L;

		when(waitingQueueRepository.getQueueStatus(userId, seatDetailId)).thenReturn(null);

		boolean result = waitingQueueService.isUserInQueue(userId, seatDetailId);

		assertFalse(result);
		verify(waitingQueueRepository, times(1)).getQueueStatus(userId, seatDetailId);
	}


	@Test
	@DisplayName("getNextInQueue 메서드는 다음 대기 사용자의 WaitingQueueDto를 반환한다.")
	public void getNextInQueue_메서드는_다음_대기_사용자의_WaitingQueueDto를_반환한다() {
		long seatDetailId = 500L;

		WaitingQueue nextInQueue = WaitingQueue.builder()
			.waitingId(5L)
			.userId(5L)
			.seatDetailId(seatDetailId)
			.waitingStatus(WaitingStatus.WAITING)
			.priority(1L)
			.reservationDt(LocalDateTime.now())
			.build();

		when(waitingQueueRepository.getNextInQueue(seatDetailId)).thenReturn(nextInQueue);

		WaitingQueueDto actualDto = waitingQueueService.getNextInQueue(seatDetailId);

		assertNotNull(actualDto);
		assertEquals(nextInQueue.getWaitingId(), actualDto.getWaitingId());
		assertEquals(nextInQueue.getUserId(), actualDto.getUserId());
		assertEquals(nextInQueue.getSeatDetailId(), actualDto.getSeatDetailId());
		assertEquals(nextInQueue.getWaitingStatus(), actualDto.getWaitingStatus());
		assertEquals(nextInQueue.getPriority(), actualDto.getPriority());
		assertEquals(nextInQueue.getReservationDt(), actualDto.getReservationDt());

		verify(waitingQueueRepository, times(1)).getNextInQueue(seatDetailId);
	}

	@Test
	@DisplayName("getNextInQueue 메서드는 대기열에 다음 사용자가 없을 경우 null을 반환한다.")
	public void getNextInQueue_메서드는_대기열에_다음_사용자가_없을_경우_null을_반환한다() {
		long seatDetailId = 600L;

		when(waitingQueueRepository.getNextInQueue(seatDetailId)).thenReturn(null);

		WaitingQueueDto actualDto = waitingQueueService.getNextInQueue(seatDetailId);

		assertNull(actualDto);
		verify(waitingQueueRepository, times(1)).getNextInQueue(seatDetailId);
	}

	@Test
	@DisplayName("getQueuePosition 메서드는 대기열 위치를 올바르게 반환한다.")
	public void getQueuePosition_메서드는_대기열_위치를_올바르게_반환한다() {
		long waitingId = 7L;
		int expectedPosition = 2;

		when(waitingQueueRepository.getQueuePosition(waitingId)).thenReturn(expectedPosition);

		int actualPosition = waitingQueueService.getQueuePosition(waitingId);

		assertEquals(expectedPosition, actualPosition);
		verify(waitingQueueRepository, times(1)).getQueuePosition(waitingId);
	}

	@Test
	@DisplayName("updateStatus 메서드는 대기열 항목의 상태를 업데이트한다.")
	public void updateStatus_메서드는_대기열_항목의_상태를_업데이트한다() {
		long waitingId = 8L;
		WaitingStatus status = WaitingStatus.NOTIFIED;

		doNothing().when(waitingQueueRepository).updateStatus(waitingId, status);

		waitingQueueService.updateStatus(waitingId, status);

		verify(waitingQueueRepository, times(1)).updateStatus(waitingId, status);
	}

	@Test
	@DisplayName("getQueueStatus 메서드는 사용자가 대기열에 있는 경우 WaitingQueueDto를 반환한다.")
	public void getQueueStatus_메서드는_사용자가_대기열에_있는_경우_WaitingQueueDto를_반환한다() {
		long userId = 9L;
		long seatDetailId = 900L;

		WaitingQueue queueStatus = WaitingQueue.builder()
			.waitingId(9L)
			.userId(userId)
			.seatDetailId(seatDetailId)
			.waitingStatus(WaitingStatus.WAITING)
			.priority(1L)
			.reservationDt(LocalDateTime.now())
			.build();

		when(waitingQueueRepository.getQueueStatus(userId, seatDetailId)).thenReturn(queueStatus);

		WaitingQueueDto actualDto = waitingQueueService.getQueueStatus(userId, seatDetailId);

		assertNotNull(actualDto);
		assertEquals(queueStatus.getWaitingId(), actualDto.getWaitingId());
		assertEquals(queueStatus.getUserId(), actualDto.getUserId());
		assertEquals(queueStatus.getSeatDetailId(), actualDto.getSeatDetailId());
		assertEquals(queueStatus.getWaitingStatus(), actualDto.getWaitingStatus());
		assertEquals(queueStatus.getPriority(), actualDto.getPriority());
		assertEquals(queueStatus.getReservationDt(), actualDto.getReservationDt());

		verify(waitingQueueRepository, times(1)).getQueueStatus(userId, seatDetailId);
	}

	@Test
	@DisplayName("getQueueStatus 메서드는 사용자가 대기열에 없는 경우 null을 반환한다.")
	public void getQueueStatus_메서드는_사용자가_대기열에_없는_경우_null을_반환한다() {
		long userId = 10L;
		long seatDetailId = 1000L;

		when(waitingQueueRepository.getQueueStatus(userId, seatDetailId)).thenReturn(null);

		WaitingQueueDto actualDto = waitingQueueService.getQueueStatus(userId, seatDetailId);

		assertNull(actualDto);
		verify(waitingQueueRepository, times(1)).getQueueStatus(userId, seatDetailId);
	}



}
