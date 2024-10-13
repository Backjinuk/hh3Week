package com.example.hh3week.application.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.example.hh3week.adapter.in.dto.waitingQueue.WaitingQueueDto;
import com.example.hh3week.application.port.out.WaitingQueueRepositoryPort;
import com.example.hh3week.domain.waitingQueue.entity.WaitingQueue;
import com.example.hh3week.domain.waitingQueue.entity.WaitingStatus;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.time.LocalDateTime;

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
		long concertScheduleId = 100L;

		WaitingQueue existingQueue = WaitingQueue.builder()
			.waitingId(1L)
			.userId(userId)
			.concertScheduleId(concertScheduleId)
			.waitingStatus(WaitingStatus.WAITING)
			.priority(1L)
			.reservationDt(LocalDateTime.now())
			.build();

		when(waitingQueueRepository.getQueueStatus(userId, concertScheduleId)).thenReturn(existingQueue);

		boolean result = waitingQueueService.isUserInQueue(userId, concertScheduleId);

		assertTrue(result);
		verify(waitingQueueRepository, times(1)).getQueueStatus(userId, concertScheduleId);
	}

	@Test
	@DisplayName("사용자가 대기열에 없는 경우 isUserInQueue 메서드는 false를 반환한다.")
	public void 사용자가_대기열에_없는_경우_isUserInQueue_메서드는_false를_반환한다() {
		long userId = 2L;
		long concertScheduleId = 200L;

		when(waitingQueueRepository.getQueueStatus(userId, concertScheduleId)).thenReturn(null);

		boolean result = waitingQueueService.isUserInQueue(userId, concertScheduleId);

		assertFalse(result);
		verify(waitingQueueRepository, times(1)).getQueueStatus(userId, concertScheduleId);
	}

	@Test
	@DisplayName("createWaitingQueue 메서드는 새로운 WaitingQueueDto를 생성한다.")
	public void createWaitingQueue_메서드는_새로운_WaitingQueueDto를_생성한다() {
		long userId = 3L;
		long concertScheduleId = 300L;

		WaitingQueue waitingQueue = WaitingQueue.builder()
			.userId(userId)
			.concertScheduleId(concertScheduleId)
			.waitingStatus(WaitingStatus.WAITING)
			.priority(1L)
			.reservationDt(LocalDateTime.now())
			.build();

		WaitingQueueDto expectedDto = WaitingQueueDto.ToDto(waitingQueue);

		WaitingQueueDto actualDto = waitingQueueService.createWaitingQueue(userId, concertScheduleId);

		assertNotNull(actualDto);
		assertEquals(waitingQueue.getUserId(), actualDto.getUserId());
		assertEquals(waitingQueue.getConcertScheduleId(), actualDto.getConcertScheduleId());
		assertEquals(waitingQueue.getWaitingStatus(), actualDto.getWaitingStatus());
		assertEquals(waitingQueue.getPriority(), actualDto.getPriority());
		assertEquals(waitingQueue.getReservationDt(), actualDto.getReservationDt());
	}


	@Test
	@DisplayName("getNextInQueue 메서드는 다음 대기 사용자의 WaitingQueueDto를 반환한다.")
	public void getNextInQueue_메서드는_다음_대기_사용자의_WaitingQueueDto를_반환한다() {
		long concertScheduleId = 500L;

		WaitingQueue nextInQueue = WaitingQueue.builder()
			.waitingId(5L)
			.userId(5L)
			.concertScheduleId(concertScheduleId)
			.waitingStatus(WaitingStatus.WAITING)
			.priority(1L)
			.reservationDt(LocalDateTime.now())
			.build();

		when(waitingQueueRepository.getNextInQueue(concertScheduleId)).thenReturn(nextInQueue);

		WaitingQueueDto actualDto = waitingQueueService.getNextInQueue(concertScheduleId);

		assertNotNull(actualDto);
		assertEquals(nextInQueue.getWaitingId(), actualDto.getWaitingId());
		assertEquals(nextInQueue.getUserId(), actualDto.getUserId());
		assertEquals(nextInQueue.getConcertScheduleId(), actualDto.getConcertScheduleId());
		assertEquals(nextInQueue.getWaitingStatus(), actualDto.getWaitingStatus());
		assertEquals(nextInQueue.getPriority(), actualDto.getPriority());
		assertEquals(nextInQueue.getReservationDt(), actualDto.getReservationDt());

		verify(waitingQueueRepository, times(1)).getNextInQueue(concertScheduleId);
	}

	@Test
	@DisplayName("getNextInQueue 메서드는 대기열에 다음 사용자가 없을 경우 null을 반환한다.")
	public void getNextInQueue_메서드는_대기열에_다음_사용자가_없을_경우_null을_반환한다() {
		long concertScheduleId = 600L;

		when(waitingQueueRepository.getNextInQueue(concertScheduleId)).thenReturn(null);

		WaitingQueueDto actualDto = waitingQueueService.getNextInQueue(concertScheduleId);

		assertNull(actualDto);
		verify(waitingQueueRepository, times(1)).getNextInQueue(concertScheduleId);
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
		long concertScheduleId = 900L;

		WaitingQueue queueStatus = WaitingQueue.builder()
			.waitingId(9L)
			.userId(userId)
			.concertScheduleId(concertScheduleId)
			.waitingStatus(WaitingStatus.WAITING)
			.priority(1L)
			.reservationDt(LocalDateTime.now())
			.build();

		when(waitingQueueRepository.getQueueStatus(userId, concertScheduleId)).thenReturn(queueStatus);

		WaitingQueueDto actualDto = waitingQueueService.getQueueStatus(userId, concertScheduleId);

		assertNotNull(actualDto);
		assertEquals(queueStatus.getWaitingId(), actualDto.getWaitingId());
		assertEquals(queueStatus.getUserId(), actualDto.getUserId());
		assertEquals(queueStatus.getConcertScheduleId(), actualDto.getConcertScheduleId());
		assertEquals(queueStatus.getWaitingStatus(), actualDto.getWaitingStatus());
		assertEquals(queueStatus.getPriority(), actualDto.getPriority());
		assertEquals(queueStatus.getReservationDt(), actualDto.getReservationDt());

		verify(waitingQueueRepository, times(1)).getQueueStatus(userId, concertScheduleId);
	}

	@Test
	@DisplayName("getQueueStatus 메서드는 사용자가 대기열에 없는 경우 null을 반환한다.")
	public void getQueueStatus_메서드는_사용자가_대기열에_없는_경우_null을_반환한다() {
		long userId = 10L;
		long concertScheduleId = 1000L;

		when(waitingQueueRepository.getQueueStatus(userId, concertScheduleId)).thenReturn(null);

		WaitingQueueDto actualDto = waitingQueueService.getQueueStatus(userId, concertScheduleId);

		assertNull(actualDto);
		verify(waitingQueueRepository, times(1)).getQueueStatus(userId, concertScheduleId);
	}



}
