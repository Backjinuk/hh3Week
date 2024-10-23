package com.example.hh3week.application.useCase;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import com.example.hh3week.adapter.in.dto.reservation.ReservationSeatDetailDto;
import com.example.hh3week.adapter.in.dto.token.TokenDto;
import com.example.hh3week.application.service.ReservationService;
import com.example.hh3week.application.service.TokenService;
import com.example.hh3week.application.service.WaitingQueueService;
import com.example.hh3week.domain.reservation.entity.ReservationStatus;
import com.example.hh3week.domain.waitingQueue.entity.WaitingQueue;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
@Sql({"classpath:schema.sql", "classpath:data.sql"})
public class ReservationUseCaseInteractorConcurrencyTest {

	@Autowired
	private ReservationUseCaseInteractor reservationUseCaseInteractor;

	@Autowired
	private WaitingQueueService waitingQueueService;

	@Autowired
	private ReservationService reservationService;

	@Autowired
	private TokenService tokenService;

	@BeforeEach
	public void setUp() {
		// 특정 seatDetailId가 AVAILABLE 상태인지 확인 및 설정
		long seatDetailId = 1L; // 예시 seatDetailId
		ReservationSeatDetailDto seatDetail = reservationService.getSeatDetailById(seatDetailId);
		if (seatDetail.getReservationStatus() != ReservationStatus.AVAILABLE) {
			seatDetail.setReservationStatus(ReservationStatus.AVAILABLE);
			reservationService.updateSeatDetailStatus(seatDetail);
		}

		// 대기열 초기화
		waitingQueueService.clearQueue();

		// 대기열이 비어 있는지 확인
		List<WaitingQueue> waitingQueues = waitingQueueService.getQueueBySeatDetailId(seatDetailId);
		assertTrue(waitingQueues.isEmpty(), "대기열이 초기화되지 않았습니다.");
	}

	@Test
	public void testConcurrentReservations() throws InterruptedException {
		int numberOfThreads = 50;
		ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
		CountDownLatch latch = new CountDownLatch(1);
		AtomicInteger successCount = new AtomicInteger(0);
		AtomicInteger failureCount = new AtomicInteger(0);
		long seatDetailId = 1L;

		// 결과를 저장할 리스트 (스레드 안전)
		List<TokenDto> tokens = new CopyOnWriteArrayList<>();

		for (long userId = 1; userId <= numberOfThreads; userId++) {
			final long uid = userId;
			executor.submit(() -> {
				try {
					latch.await(); // 모든 스레드가 준비되면 동시에 실행
					TokenDto token = reservationUseCaseInteractor.reserveSeat(uid, seatDetailId);
					tokens.add(token);

					Map<String, Object> tokensAllValue = tokenService.getTokensAllValue(token.getToken());
					long queueOrder = Long.parseLong(tokensAllValue.get("queueOrder").toString());
					System.out.println("사용자 대기열 순서: " + queueOrder);

					if (queueOrder == 0) {
						successCount.incrementAndGet();
					} else {
						failureCount.incrementAndGet();
					}
				} catch (Exception e) {
					failureCount.incrementAndGet();
					System.out.println("사용자 예약 실패 ");
				}
			});
		}

		latch.countDown(); // 모든 스레드가 시작되도록 신호

		executor.shutdown();
		boolean finished = executor.awaitTermination(2, TimeUnit.MINUTES);
		assertTrue(finished, "스레드가 제 시간에 종료되지 않았습니다.");

		// Assertions

		// 발급된 토큰 수 확인
		assertEquals(numberOfThreads, tokens.size(), "모든 사용자에게 토큰이 발급되어야 합니다.");

		// 성공과 실패 카운트 확인
		assertEquals(1, successCount.get(), "하나의 예약만 성공해야 합니다.");
		assertEquals(numberOfThreads - 1, failureCount.get(), "나머지 예약은 실패해야 합니다.");

		// 좌석 상태가 PENDING으로 변경되었는지 확인
		ReservationSeatDetailDto seatDetail = reservationService.getSeatDetailById(seatDetailId);
		assertEquals(ReservationStatus.PENDING, seatDetail.getReservationStatus(), "좌석 상태는 PENDING이어야 합니다.");

		// 대기열에 나머지 사용자가 추가되었는지 확인
		List<WaitingQueue> waitingQueues = waitingQueueService.getQueueBySeatDetailId(seatDetailId);
		assertEquals(numberOfThreads, waitingQueues.size(), "대기열에 나머지 사용자가 추가되어야 합니다.");

		// 대기열 우선순위 검증
		// 대기열 우선순위가 1부터 numberOfThreads -1까지 연속적으로 있는지 확인
		List<Long> sortedPriorities = waitingQueues.stream()
			.map(WaitingQueue::getPriority)
			.sorted()
			.toList();

		for (int i = 0; i < sortedPriorities.size(); i++) {
			assertEquals(i + 1, sortedPriorities.get(i), "우선순위가 올바르게 연속적으로 증가해야 합니다.");
		}

		// 대기열 우선순위의 고유성 확인
		Set<Long> uniquePriorities = waitingQueues.stream()
			.map(WaitingQueue::getPriority)
			.collect(Collectors.toSet());
		assertEquals(waitingQueues.size(), uniquePriorities.size(), "대기열의 모든 우선순위는 고유해야 합니다.");
	}
}
