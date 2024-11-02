package com.example.hh3week.concurrency;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import com.example.hh3week.adapter.in.dto.reservation.ReservationSeatDetailDto;
import com.example.hh3week.adapter.in.dto.token.TokenDto;
import com.example.hh3week.adapter.in.dto.waitingQueue.WaitingQueueDto;
import com.example.hh3week.application.port.in.ReservationUseCase;
import com.example.hh3week.application.service.ReservationService;
import com.example.hh3week.application.service.TokenService;
import com.example.hh3week.application.service.WaitingQueueService;
import com.example.hh3week.application.useCase.ReservationUseCaseInteractor;
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
	private	ReservationUseCase useCase;

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
	@DisplayName("여러회원이 1개의 좌석을 예약")
	public void 여러회원이_1개의_좌석을_예약() throws InterruptedException {
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

					TokenDto token = useCase.sendReservationRequest(uid, seatDetailId)
						.get(30, TimeUnit.SECONDS);
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
		boolean finished = executor.awaitTermination(10, TimeUnit.MINUTES);
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
	@DisplayName("동시예약_랜덤한사용자_랜덤한좌석예약")
	public void 동시예약_랜덤한사용자_랜덤한좌석예약() throws InterruptedException {
		int numberOfUsers = 10;
		int numberOfSeats = 5;
		int totalAttempts = numberOfUsers * numberOfSeats;
		int numberOfThreads = 100;
		ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
		CountDownLatch latch = new CountDownLatch(1);
		AtomicInteger successCount = new AtomicInteger(0);
		AtomicInteger failureCount = new AtomicInteger(0);

		Set<Long> seatDetailIdList = new java.util.HashSet<>(Set.of());
		Set<Long> userIdList = new java.util.HashSet<>(Set.of());

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

					synchronized (seatDetailIdList) {
						seatDetailIdList.add(seatDetailId);
					}
					synchronized (userIdList) {
						userIdList.add(userId);
					}

					// 예약 시도 (재시도 로직 포함)
					TokenDto token = null;
					try {
						token = useCase.sendReservationRequest(userId, seatDetailId)
							.get(30, TimeUnit.SECONDS);
					} catch (Exception e) {
						failureCount.incrementAndGet();
						return;
					}

					if (token != null) {
						tokens.add(token);

						// 토큰 정보 조회
						Map<String, Object> tokensAllValue = tokenService.getTokensAllValue(token.getToken());
						long queueOrder = Long.parseLong(tokensAllValue.get("queueOrder").toString());

						if (queueOrder == 0) {
							successCount.incrementAndGet();
						} else {
							failureCount.incrementAndGet();
						}
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
		System.out.println("seatDetailsList : " + seatDetailIdList);
		System.out.println("userIdList : " + userIdList);

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
			List<Long> sortedPriorities = waitingQueues.stream().map(WaitingQueue::getPriority).sorted().toList();

			for (int i = 0; i < sortedPriorities.size(); i++) {
				assertEquals(i + 1, sortedPriorities.get(i), "우선순위가 올바르게 연속적으로 증가해야 합니다. (좌석 ID: " + seatId + ")");
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
	@DisplayName("동시예약_랜덤한사용자_고정한좌석예약")
	public void 동시예약_랜덤한사용자_고정한좌석예약() throws InterruptedException {
		int numberOfUsers = 100;
		int numberOfSeats = 1; // 좌석을 하나로 고정
		int totalAttempts = numberOfUsers * numberOfSeats;
		int numberOfThreads = 100;
		ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
		CountDownLatch latch = new CountDownLatch(1);
		AtomicInteger successCount = new AtomicInteger(0);
		AtomicInteger failureCount = new AtomicInteger(0);

		long fixedSeatDetailId = 1L; // 고정된 좌석 ID

		List<TokenDto> tokens = new CopyOnWriteArrayList<>();

		// 사용자-좌석 조합 리스트 생성 (사용자 ID는 랜덤, 좌석 ID는 고정)
		List<Long> userIds = new ArrayList<>();
		for (long userId = 1; userId <= numberOfUsers; userId++) {
			userIds.add(userId);
		}

		for (Long userId : userIds) {
			executor.submit(() -> {
				try {
					latch.await();

					// 예약 시도
					TokenDto token = useCase.sendReservationRequest(userId, fixedSeatDetailId)
						.get(30, TimeUnit.SECONDS);
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

		// 성공과 실패 카운트 확인
		assertEquals(1, successCount.get(), "좌석 하나당 하나의 예약만 성공해야 합니다.");
		assertEquals(totalAttempts - 1, failureCount.get(), "나머지 예약은 대기열에 추가되어야 합니다.");

		// 좌석 상태가 PENDING으로 변경되었는지 확인
		ReservationSeatDetailDto seatDetail = reservationService.getSeatDetailById(fixedSeatDetailId);
		assertEquals(ReservationStatus.PENDING, seatDetail.getReservationStatus(),
			"좌석 ID " + fixedSeatDetailId + "의 상태는 PENDING이어야 합니다.");

		// 대기열에 나머지 사용자가 추가되었는지 확인
		List<WaitingQueue> waitingQueues = waitingQueueService.getQueueBySeatDetailId(fixedSeatDetailId);
		assertEquals(numberOfUsers - 1, waitingQueues.size(),
			"대기열에 나머지 사용자가 추가되어야 합니다. (좌석 ID: " + fixedSeatDetailId + ")");

		// 대기열 우선순위 검증
		List<Long> sortedPriorities = waitingQueues.stream()
			.map(WaitingQueue::getPriority)
			.sorted()
			.collect(Collectors.toList());

		for (int i = 0; i < sortedPriorities.size(); i++) {
			assertEquals(i + 1, sortedPriorities.get(i), "대기열 우선순위가 올바르게 연속적으로 증가해야 합니다.");
		}

		// 대기열 우선순위의 고유성 확인
		Set<Long> uniquePriorities = waitingQueues.stream().map(WaitingQueue::getPriority).collect(Collectors.toSet());
		assertEquals(waitingQueues.size(), uniquePriorities.size(), "대기열의 모든 우선순위는 고유해야 합니다.");
	}

	/**
	 * 좌석 랜덤, 사용자 고정 테스트
	 * 모든 좌석이 특정 사용자에 의해 랜덤하게 예약 시도됩니다.
	 */
	@Test
	@DisplayName("동시예약_고정한사용자_랜덤한좌석예약")
	public void 동시예약_고정한사용자_랜덤한좌석예약() throws InterruptedException {
		int numberOfUsers = 1; // 사용자 수 고정
		int numberOfSeats = 10; // 좌석 수 증가
		int totalAttempts = numberOfUsers * numberOfSeats;
		int numberOfThreads = 10;
		ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
		CountDownLatch latch = new CountDownLatch(1);
		AtomicInteger successCount = new AtomicInteger(0);
		AtomicInteger failureCount = new AtomicInteger(0);

		long fixedUserId = 1L; // 고정된 사용자 ID

		List<TokenDto> tokens = new CopyOnWriteArrayList<>();

		Set<Long> setList = new HashSet<>();

		// 좌석-사용자 조합 리스트 생성 (좌석 ID는 랜덤, 사용자 ID는 고정)
		List<Long> seatIds = new ArrayList<>();
		for (long seatId = 1; seatId <= numberOfSeats; seatId++) {
			seatIds.add(seatId);
		}

		for (Long seatId : seatIds) {
			executor.submit(() -> {
				try {
					latch.await();

					setList.add(seatId);

					// 예약 시도
					TokenDto token = useCase.sendReservationRequest(fixedUserId, seatId)
						.get(30, TimeUnit.SECONDS);
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
		boolean finished = executor.awaitTermination(1000, TimeUnit.MINUTES);
		assertTrue(finished, "스레드가 제 시간에 종료되지 않았습니다.");

		System.out.println("seatList : " + setList.size());

		System.out.println("성공 횟수: " + successCount.get() + ", 실패 횟수: " + failureCount.get());

		// Assertions

		// 발급된 토큰 수 확인
		assertEquals(totalAttempts, tokens.size(), "모든 사용자에게 토큰이 발급되어야 합니다.");

		// 성공과 실패 카운트 확인
		assertEquals(numberOfSeats, successCount.get(), "각 좌석당 하나의 예약만 성공해야 합니다.");
		assertEquals(totalAttempts - numberOfSeats, failureCount.get(), "나머지 예약은 대기열에 추가되어야 합니다.");

		// 좌석 상태가 PENDING으로 변경되었는지 확인
		for (long seatId = 1; seatId <= numberOfSeats; seatId++) {
			ReservationSeatDetailDto seatDetail = reservationService.getSeatDetailById(seatId);
			assertEquals(ReservationStatus.PENDING, seatDetail.getReservationStatus(),
				"좌석 ID " + seatId + "의 상태는 PENDING이어야 합니다.");
		}

		// 대기열에 나머지 사용자가 추가되었는지 확인
		for (long seatId = 1; seatId <= numberOfSeats; seatId++) {
			List<WaitingQueue> waitingQueues = waitingQueueService.getQueueBySeatDetailId(seatId);
			assertEquals(numberOfUsers - 1, waitingQueues.size(), "대기열에 나머지 사용자가 추가되어야 합니다. (좌석 ID: " + seatId + ")");
		}

		// 대기열 우선순위 검증
		for (long seatId = 1; seatId <= numberOfSeats; seatId++) {
			List<WaitingQueue> waitingQueues = waitingQueueService.getQueueBySeatDetailId(seatId);
			List<Long> sortedPriorities = waitingQueues.stream()
				.map(WaitingQueue::getPriority)
				.sorted()
				.collect(Collectors.toList());

			for (int i = 0; i < sortedPriorities.size(); i++) {
				assertEquals(i + 1, sortedPriorities.get(i), "우선순위가 올바르게 연속적으로 증가해야 합니다. (좌석 ID: " + seatId + ")");
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
					return useCase.sendReservationRequest(uid, seatDetailId)
						.get(30, TimeUnit.SECONDS);

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

		public F getFirst() {
			return first;
		}

		public S getSecond() {
			return second;
		}
	}
}

