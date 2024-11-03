package com.example.hh3week.concurrency;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.test.context.jdbc.Sql;

import com.example.hh3week.adapter.in.dto.user.UserPointHistoryDto;
import com.example.hh3week.application.service.UserService;
import com.example.hh3week.application.useCase.UserUseCaseInteract;
import com.example.hh3week.domain.user.entity.PointStatus;
import com.example.hh3week.domain.user.entity.User;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
@Sql({"classpath:schema.sql", "classpath:data.sql"})
public class UserUseCaseInteractConcurrencyTest {

	private final List<Long> userIds = List.of(1L, 2L, 3L, 104L, 101L);
	private final Random random = new Random();

	@Autowired
	private UserUseCaseInteract userUseCaseInteract;

	@Autowired
	private UserService userService;





	@Test
	public void testRandomConcurrentPointTransactions() throws InterruptedException, ExecutionException {
		System.out.println("testRandomConcurrentPointTransactions 시작");
		int numberOfUsers = userIds.size();
		int numberOfThreads = 100; // 동시에 실행할 스레드 수
		int operationsPerThread = 10; // 각 스레드당 수행할 연산 수

		ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
		CountDownLatch latch = new CountDownLatch(1);
		List<Callable<Void>> tasks = new ArrayList<>();

		// 각 스레드가 수행할 작업 정의
		for (int i = 0; i < numberOfThreads; i++) {
			final int threadId = i + 1;
			tasks.add(() -> {
				System.out.println("스레드 " + threadId + " 준비 완료");
				latch.await(); // 모든 스레드가 동시에 시작하도록 대기
				System.out.println("스레드 " + threadId + " 시작");
				for (int j = 0; j < operationsPerThread; j++) {
					Long userId = userIds.get(random.nextInt(numberOfUsers));
					PointStatus status = random.nextBoolean() ? PointStatus.EARN : PointStatus.USE;
					long amount = random.nextInt(100) + 1; // 1부터 100 사이의 랜덤 금액

					UserPointHistoryDto dto = new UserPointHistoryDto();
					dto.setUserId(userId);
					dto.setPointStatus(status);
					dto.setPointAmount(amount);

					System.out.println("스레드 " + threadId + " - 연산 " + (j + 1) + ": 사용자 ID = " + userId +
						", 상태 = " + status + ", 금액 = " + amount);

					try {
						userUseCaseInteract.handleUserPoint2(dto);
						System.out.println("스레드 " + threadId + " - 연산 " + (j + 1) + " 완료");
					} catch (OptimisticLockingFailureException | IllegalArgumentException e) {
						// 예외 발생 시 로깅하거나 필요한 처리를 수행
						// 여기서는 단순히 무시
						System.out.println("스레드 " + threadId + " - 연산 " + (j + 1) + " 예외 발생: " + e.getMessage());
					}
				}
				System.out.println("스레드 " + threadId + " 종료");
				return null;
			});
		}

		// 모든 스레드가 준비되었음을 표시하고, 작업을 제출
		List<Future<Void>> futures = new ArrayList<>();
		for (Callable<Void> task : tasks) {
			futures.add(executor.submit(task));
		}
		System.out.println("모든 스레드 준비 완료, 동시에 시작");

		// 동시에 시작
		latch.countDown();

		// 모든 작업이 완료될 때까지 대기
		executor.shutdown();
		boolean terminated = executor.awaitTermination(60, TimeUnit.SECONDS);
		if (!terminated) {
			executor.shutdownNow();
			System.out.println("Executor가 지정된 시간 내에 종료되지 않음");
			throw new RuntimeException("Executor did not terminate in the specified time.");
		}
		System.out.println("모든 스레드 작업 완료");

		// 모든 스레드에서 발생한 예외 확인
		for (Future<Void> future : futures) {
			try {
				future.get();
			} catch (ExecutionException e) {
				// OptimisticLockingFailureException 또는 IllegalArgumentException 발생 시 무시
				Throwable cause = e.getCause();
				if (!(cause instanceof OptimisticLockingFailureException)
					&& !(cause instanceof IllegalArgumentException)) {
					throw e;
				}
				// 예외는 @Retryable에 의해 처리되므로 여기서는 무시
				System.out.println("테스트 중 예외 발생: " + cause.getMessage());
			}
		}

		// 최종 포인트 확인
		System.out.println("최종 포인트 확인 시작");
		for (Long userId : userIds) {
			User updatedUser = userService.getUserInfo2(userId);
			System.out.println("사용자 ID: " + userId + ", 최종 포인트 잔액: " + updatedUser.getPointBalance());
			// 포인트가 음수가 되지 않았는지 확인
			assertTrue(updatedUser.getPointBalance() >= 0, "포인트 잔액이 음수가 되어서는 안 됩니다.");
		}
		System.out.println("testRandomConcurrentPointTransactions 완료");
	}




		@Test
	public void testOptimisticLocking_SuccessfulConcurrentUpdates() throws InterruptedException {
		// 두 개의 스레드가 동시에 포인트를 추가 및 사용하려고 시도
		Long userId = 104L;

		// 스레드 동기화를 위한 CountDownLatch
		CountDownLatch latch = new CountDownLatch(1);
		ExecutorService executor = Executors.newFixedThreadPool(150);

		Runnable task1 = () -> {
			try {
				latch.await(); // 두 스레드가 동시에 시작하도록 대기
				UserPointHistoryDto dto = new UserPointHistoryDto();
				dto.setUserId(userId);
				dto.setPointStatus(PointStatus.EARN);
				dto.setPointAmount(5); // 포인트 충전
				userUseCaseInteract.handleUserPoint2(dto);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		};

		Runnable task2 = () -> {
			try {
				latch.await();
				UserPointHistoryDto dto = new UserPointHistoryDto();
				dto.setUserId(userId);
				dto.setPointStatus(PointStatus.USE);
				dto.setPointAmount(5); // 포인트 사용
				userUseCaseInteract.handleUserPoint2(dto);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		};

		executor.submit(task1);
		executor.submit(task2);

		// 동시에 시작
		latch.countDown();

		// 모든 작업이 완료될 때까지 대기
		executor.shutdown();
		while (!executor.isTerminated()) {
			Thread.sleep(100);
		}

		// 최종 포인트 확인
		User updatedUser = userService.getUserInfo2(userId);
		// 초기 포인트 10 + 5 (충전) - 5 (사용) = 10
		assertEquals(10L, updatedUser.getPointBalance());
	}




	@Test
	public void testOptimisticLocking_ConflictOccurs() throws InterruptedException, ExecutionException {
		// 두 개의 스레드가 동시에 같은 포인트를 사용하려고 시도하여 충돌 발생
		Long userId = 104L;

		// 스레드 동기화를 위한 CountDownLatch
		CountDownLatch latch = new CountDownLatch(1);
		ExecutorService executor = Executors.newFixedThreadPool(2);

		Callable<Void> task1 = () -> {
			try {
				latch.await();
				UserPointHistoryDto dto = new UserPointHistoryDto();
				dto.setUserId(userId);
				dto.setPointStatus(PointStatus.USE);
				dto.setPointAmount(5); // 포인트 사용
				userUseCaseInteract.handleUserPoint2(dto);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
			return null;
		};

		Callable<Void> task2 = () -> {
			try {
				latch.await();
				UserPointHistoryDto dto = new UserPointHistoryDto();
				dto.setUserId(userId);
				dto.setPointStatus(PointStatus.USE);
				dto.setPointAmount(5); // 포인트 사용
				userUseCaseInteract.handleUserPoint2(dto);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
			return null;
		};

		Future<Void> future1 = executor.submit(task1);
		Future<Void> future2 = executor.submit(task2);

		// 동시에 시작
		latch.countDown();

		// 모든 작업이 완료될 때까지 대기
		executor.shutdown();
		boolean terminated = executor.awaitTermination(10, java.util.concurrent.TimeUnit.SECONDS);
		if (!terminated) {
			executor.shutdownNow();
			throw new RuntimeException("Executor did not terminate in the specified time.");
		}

		// 예외가 발생했는지 확인
		try {
			future1.get(); // 예외가 발생하면 ExecutionException이 던져짐
		} catch (ExecutionException e) {
			// 실제로는 OptimisticLockingFailureException이 발생할 수 있으나, @Retryable이 이를 처리해야 함
			Throwable cause = e.getCause();
			if (!(cause instanceof OptimisticLockingFailureException) && !(cause instanceof IllegalArgumentException)) {
				throw e;
			}
			// 예외는 재시도 로직에 의해 처리되므로 여기서는 무시
		}

		try {
			future2.get();
		} catch (ExecutionException e) {
			// 실제로는 OptimisticLockingFailureException이 발생할 수 있으나, @Retryable이 이를 처리해야 함
			Throwable cause = e.getCause();
			if (!(cause instanceof OptimisticLockingFailureException) && !(cause instanceof IllegalArgumentException)) {
				throw e;
			}
			// 예외는 재시도 로직에 의해 처리되므로 여기서는 무시
		}

		// 최종 포인트 확인
		User updatedUser = userService.getUserInfo2(userId);
		// 초기 포인트 10 -5 -5 =0
		assertEquals(0L, updatedUser.getPointBalance(), "최종 포인트는 0이어야 합니다.");
	}

	// @Test
	// public void testOptimisticLockingConflictException() {
	// 	Long userId = 104L;
	//
	// 	// 첫 번째 트랜잭션: 포인트 5 사용
	// 	User user1 = userRepository.findById(userId).orElseThrow();
	// 	user1.setPointBalance(user1.getPointBalance() - 5);
	// 	userRepository.saveAndFlush(user1);
	//
	// 	// 두 번째 트랜잭션: 같은 사용자의 포인트 5 사용 시도
	// 	User user2 = userRepository.findById(userId).orElseThrow();
	// 	user2.setPointBalance(user2.getPointBalance() - 5);
	//
	// 	// 첫 번째 트랜잭션이 먼저 저장되었으므로 두 번째 트랜잭션은 OptimisticLockException 발생 예상
	// 	assertThatThrownBy(() -> {
	// 		userRepository.saveAndFlush(user2);
	// 	}).isInstanceOf(OptimisticLockingFailureException.class);
	// }
}
