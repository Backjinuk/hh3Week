package com.example.hh3week.application.useCase;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import com.example.hh3week.adapter.in.dto.reservation.ReservationSeatDetailDto;
import com.example.hh3week.adapter.in.dto.token.TokenDto;
import com.example.hh3week.adapter.in.dto.waitingQueue.WaitingQueueDto;
import com.example.hh3week.application.service.ReservationService;
import com.example.hh3week.application.service.TokenService;
import com.example.hh3week.application.service.WaitingQueueService;
import com.example.hh3week.domain.reservation.entity.ReservationStatus;
import com.example.hh3week.domain.waitingQueue.entity.WaitingQueue;
import com.example.hh3week.domain.waitingQueue.entity.WaitingStatus;

import lombok.extern.slf4j.Slf4j;

@Slf4j
// @ActiveProfiles("test")
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

		// 대기열 초기화
		waitingQueueService.clearQueue();
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
					latch.await();

					TokenDto token = reservationUseCaseInteractor.reserveSeat(uid, seatDetailId);
					tokens.add(token);

					Map<String, Object> tokensAllValue = tokenService.getTokensAllValue(token.getToken());
					long queueOrder = Long.parseLong(tokensAllValue.get("queueOrder").toString());

					if (queueOrder == 0) {
						successCount.incrementAndGet();
					} else {
						failureCount.incrementAndGet();
					}
				} catch (Exception e) {
					failureCount.incrementAndGet();
				}
			});
		}

		latch.countDown();

		executor.shutdown();
		boolean finished = executor.awaitTermination(2, TimeUnit.MINUTES);
		assertTrue(finished, "스레드가 제 시간에 종료되지 않았습니다.");

		System.out.println("count : " + successCount.get() + failureCount.get());


		assertEquals(numberOfThreads, tokens.size(), "모든 사용자에게 토큰이 발급되어야 합니다.");

		assertEquals(1, successCount.get(), "하나의 예약만 성공해야 합니다.");
		assertEquals(numberOfThreads - 1, failureCount.get(), "나머지 예약은 실패해야 합니다.");

		ReservationSeatDetailDto seatDetail = reservationService.getSeatDetailById(seatDetailId);
		assertEquals(ReservationStatus.PENDING, seatDetail.getReservationStatus(), "좌석 상태는 PENDING이어야 합니다.");

		List<WaitingQueue> waitingQueues = waitingQueueService.getQueueBySeatDetailId(seatDetailId);
		assertEquals(numberOfThreads - 1, waitingQueues.size(), "대기열에 나머지 사용자가 추가되어야 합니다.");

		List<Long> sortedPriorities = waitingQueues.stream()
			.map(WaitingQueue::getPriority)
			.sorted()
			.collect(Collectors.toList());

		for (int i = 0; i < sortedPriorities.size(); i++) {
			assertEquals(i + 1, sortedPriorities.get(i), "우선순위가 올바르게 연속적으로 증가해야 합니다.");
		}

		Set<Long> uniquePriorities = waitingQueues.stream().map(WaitingQueue::getPriority).collect(Collectors.toSet());
		assertEquals(waitingQueues.size(), uniquePriorities.size(), "대기열의 모든 우선순위는 고유해야 합니다.");
	}

	@Test
	public void testConcurrentReservationsRandom() throws InterruptedException {
		int numberOfUsers = 10;
		int numberOfSeats = 10;
		int totalAttempts = numberOfUsers * numberOfSeats;
		int numberOfThreads = 100;
		ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
		CountDownLatch latch = new CountDownLatch(1);
		AtomicInteger successCount = new AtomicInteger(0);
		AtomicInteger failureCount = new AtomicInteger(0);

		List<TokenDto> tokens = new CopyOnWriteArrayList<>();

		List<Pair<Long, Long>> userSeatPairs = new ArrayList<>();
		for (long userId = 1; userId <= numberOfUsers; userId++) {
			for (long seatDetailId = 1; seatDetailId <= numberOfSeats; seatDetailId++) {
				userSeatPairs.add(new Pair<>(userId, seatDetailId));
			}
		}

		for (Pair<Long, Long> pair : userSeatPairs) {
			executor.submit(() -> {
				try {
					latch.await();

					long userId = pair.getFirst();
					long seatDetailId = pair.getSecond();

					// 예약 시도
					TokenDto token = reservationUseCaseInteractor.reserveSeat(userId, seatDetailId);
					tokens.add(token);

					// 토큰 정보 조회
					Map<String, Object> tokensAllValue = tokenService.getTokensAllValue(token.getToken());
					long queueOrder = Long.parseLong(tokensAllValue.get("queueOrder").toString());

					if (queueOrder == 0) {
						successCount.incrementAndGet();
					} else {
						failureCount.incrementAndGet();
					}
				} catch (Exception e) {
					failureCount.incrementAndGet();
				}
			});
		}

		latch.countDown(); // 모든 스레드가 시작되도록 신호

		executor.shutdown();
		boolean finished = executor.awaitTermination(5, TimeUnit.MINUTES);
		assertTrue(finished, "스레드가 제 시간에 종료되지 않았습니다.");

		System.out.println("성공 횟수: " + successCount.get() + ", 실패 횟수: " + failureCount.get());

		// Assertions

		// 발급된 토큰 수 확인
		assertEquals(totalAttempts, tokens.size(), "모든 사용자에게 토큰이 발급되어야 합니다.");

		assertEquals(numberOfSeats, successCount.get(), "각 좌석당 하나의 예약만 성공해야 합니다.");
		assertEquals(totalAttempts - numberOfSeats, failureCount.get(), "나머지 예약은 대기열에 추가되어야 합니다.");

		for (long seatId = 1; seatId <= numberOfSeats; seatId++) {
			ReservationSeatDetailDto seatDetail = reservationService.getSeatDetailById(seatId);
			assertEquals(ReservationStatus.PENDING, seatDetail.getReservationStatus(),
				"좌석 ID " + seatId + "의 상태는 PENDING이어야 합니다.");
		}

		for (long seatId = 1; seatId <= numberOfSeats; seatId++) {
			List<WaitingQueue> waitingQueues = waitingQueueService.getQueueBySeatDetailId(seatId);
			assertEquals(numberOfUsers - 1, waitingQueues.size(),
				"대기열에 나머지 사용자가 추가되어야 합니다. (좌석 ID: " + seatId + ")");
		}

		for (long seatId = 1; seatId <= numberOfSeats; seatId++) {
			List<WaitingQueue> waitingQueues = waitingQueueService.getQueueBySeatDetailId(seatId);
			List<Long> sortedPriorities = waitingQueues.stream()
				.map(WaitingQueue::getPriority)
				.sorted()
				.toList();

			for (int i = 0; i < sortedPriorities.size(); i++) {
				assertEquals(i + 1, sortedPriorities.get(i),
					"우선순위가 올바르게 연속적으로 증가해야 합니다. (좌석 ID: " + seatId + ")");
			}

			// 대기열 우선순위의 고유성 확인
			Set<Long> uniquePriorities = waitingQueues.stream()
				.map(WaitingQueue::getPriority)
				.collect(Collectors.toSet());
			assertEquals(waitingQueues.size(), uniquePriorities.size(),
				"대기열의 모든 우선순위는 고유해야 합니다. (좌석 ID: " + seatId + ")");
		}
	}


	@Test
	public void testConcurrentReservations2() throws InterruptedException {
		final int numberOfUsers = 40;
		final long seatDetailId = 1L;

		// ExecutorService를 사용하여 동시성 테스트 수행
		ExecutorService executorService = Executors.newFixedThreadPool(numberOfUsers);
		CountDownLatch latch = new CountDownLatch(1); // 모든 스레드가 동시에 시작되도록 제어

		List<Future<TokenDto>> futures = new ArrayList<>();

		// 40명의 사용자가 예약을 시도하도록 스레드 생성
		for (long userId = 1; userId <= numberOfUsers; userId++) {
			final long uid = userId;
			Future<TokenDto> future = executorService.submit(() -> {
				try {
					latch.await(); // 모든 스레드가 준비될 때까지 대기
					return reservationUseCaseInteractor.reserveSeat(uid, seatDetailId);
				} catch (Exception e) {
					System.err.println("사용자 " + uid + "의 예약 실패: " + e.getMessage());
					return null;
				}
			});
			futures.add(future);
		}

		// 모든 스레드가 준비되면 실행 시작
		latch.countDown();

		// 결과 수집
		List<TokenDto> tokens = new ArrayList<>();
		for (Future<TokenDto> future : futures) {
			try {
				TokenDto token = future.get(5, TimeUnit.SECONDS); // 각 스레드가 최대 5초 내에 완료되도록 설정
				if (token != null) {
					tokens.add(token);
				}
			} catch (ExecutionException | TimeoutException e) {
				System.err.println("스레드 실행 중 오류 발생: " + e.getMessage());
			}
		}

		// ExecutorService 종료
		executorService.shutdown();
		executorService.awaitTermination(10, TimeUnit.SECONDS);

		// 토큰 발급 수 확인
		System.out.println("필요: " + numberOfUsers);
		System.out.println("실제: " + tokens.size());

		// 모든 사용자에게 토큰이 발급되었는지 확인
		assertEquals(numberOfUsers, tokens.size(), "모든 사용자에게 토큰이 발급되어야 합니다.");
	}

	@Test
	public void testPriorityIncrement() {
		int numberOfUsers = 5;
		long seatDetailId = 1L;

		// 사용자 ID 1부터 numberOfUsers까지 대기열에 추가
		for (long userId = 1; userId <= numberOfUsers; userId++) {
			WaitingQueueDto waitingQueueDto = WaitingQueueDto.builder()
				.userId(userId)
				.seatDetailId(seatDetailId)
				.waitingStatus(WaitingStatus.WAITING)
				.reservationDt(LocalDateTime.now())
				.build();

			WaitingQueueDto waitingQueue = waitingQueueService.addWaitingQueue(waitingQueueDto);
			assertNotNull(waitingQueue.getWaitingId(), "대기열 ID가 null이어서는 안 됩니다.");
			assertTrue(waitingQueue.getPriority() > 0, "우선순위는 0보다 커야 합니다.");
		}

		List<WaitingQueue> waitingQueues = waitingQueueService.getQueueBySeatDetailId(seatDetailId);
		assertEquals(numberOfUsers, waitingQueues.size(), "대기열에 추가된 사용자 수가 일치해야 합니다.");

		List<Long> sortedPriorities = waitingQueues.stream()
			.map(WaitingQueue::getPriority)
			.sorted()
			.collect(Collectors.toList());

		for (int i = 0; i < sortedPriorities.size(); i++) {
			assertEquals(i + 1, sortedPriorities.get(i), "우선순위가 올바르게 연속적으로 증가해야 합니다.");
		}

		// 우선순위의 고유성 확인
		Set<Long> uniquePriorities = waitingQueues.stream().map(WaitingQueue::getPriority).collect(Collectors.toSet());
		assertEquals(waitingQueues.size(), uniquePriorities.size(), "모든 우선순위는 고유해야 합니다.");
	}

	@Test
	public void testPriorityIncrementWithPessimisticLocking() {
		int numberOfUsers = 5;
		long seatDetailId = 1L;

		for (long userId = 1; userId <= numberOfUsers; userId++) {
			WaitingQueueDto waitingQueueDto = WaitingQueueDto.builder()
				.userId(userId)
				.seatDetailId(seatDetailId)
				.waitingStatus(WaitingStatus.WAITING)
				.reservationDt(LocalDateTime.now())
				.build();

			WaitingQueueDto waitingQueue = waitingQueueService.addWaitingQueue(waitingQueueDto);
			assertNotNull(waitingQueue.getWaitingId(), "대기열 ID가 null이어서는 안 됩니다.");
			assertTrue(waitingQueue.getPriority() > 0, "우선순위는 0보다 커야 합니다.");
		}

		List<WaitingQueue> waitingQueues = waitingQueueService.getQueueBySeatDetailId(seatDetailId);
		assertEquals(numberOfUsers, waitingQueues.size(), "대기열에 추가된 사용자 수가 일치해야 합니다.");

		List<Long> sortedPriorities = waitingQueues.stream()
			.map(WaitingQueue::getPriority)
			.sorted()
			.collect(Collectors.toList());

		for (int i = 0; i < sortedPriorities.size(); i++) {
			assertEquals(i + 1, sortedPriorities.get(i), "우선순위가 올바르게 연속적으로 증가해야 합니다.");
		}

		// 우선순위의 고유성 확인
		Set<Long> uniquePriorities = waitingQueues.stream().map(WaitingQueue::getPriority).collect(Collectors.toSet());
		assertEquals(waitingQueues.size(), uniquePriorities.size(), "모든 우선순위는 고유해야 합니다.");
	}


	private static class Pair<F, S> {
		private final F first;
		private final S second;

		public Pair(F first, S second) {
			this.first = first;
			this.second = second;
		}

		public F getFirst() { return first; }

		public S getSecond() { return second; }
	}
}

