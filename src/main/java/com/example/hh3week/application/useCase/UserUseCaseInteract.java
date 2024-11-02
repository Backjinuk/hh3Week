package com.example.hh3week.application.useCase;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.hh3week.adapter.in.dto.user.UserDto;
import com.example.hh3week.adapter.in.dto.user.UserPointHistoryDto;
import com.example.hh3week.adapter.in.dto.validation.DtoValidation;
import com.example.hh3week.application.port.in.UserUseCase;
import com.example.hh3week.application.service.UserService;
import com.example.hh3week.common.config.exception.CustomException;
import com.example.hh3week.domain.user.entity.PointStatus;
import com.example.hh3week.domain.user.entity.User;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserUseCaseInteract implements UserUseCase {

	private final UserService userService;
	private final RedissonClient redissonClient;


	public UserUseCaseInteract(UserService userService,
		@Qualifier("redissonClient") RedissonClient redissonClient) {
		this.userService = userService;
		this.redissonClient = redissonClient;
	}

	/**
	 * 사용자 포인트 충전 또는 사용을 처리하는 Use Case
	 *
	 * @param userPointHistoryDto 사용자 포인트 히스토리 DTO
	 * @return 저장된 사용자 포인트 히스토리 DTO
	 */
	@Override
	@Transactional
	public UserPointHistoryDto handleUserPoint(UserPointHistoryDto userPointHistoryDto) {
		DtoValidation.validateUserPointHistoryDto(userPointHistoryDto);

		UserDto userInfo = userService.getUserInfo(userPointHistoryDto.getUserId());

		if (userPointHistoryDto.getPointStatus() == PointStatus.EARN) {
			depositUserPoints(userInfo, userPointHistoryDto.getPointAmount());
		} else if (userPointHistoryDto.getPointStatus() == PointStatus.USE) {
			useUserPoints(userInfo, userPointHistoryDto.getPointAmount());
		} else {
			CustomException.illegalArgument("유효하지 않은 포인트 상태입니다.", new IllegalArgumentException(), this.getClass());
		}

		return userService.addUserPointHistoryInUser(userPointHistoryDto);
	}

	/**
	 * 사용자 포인트 충전 또는 사용을 처리하는 Use Case
	 *
	 * @param userPointHistoryDto 사용자 포인트 히스토리 DTO
	 * @return 저장된 사용자 포인트 히스토리 DTO
	 */
	// @Retryable(
	// 	value = {OptimisticLockingFailureException.class},
	// 	maxAttempts = 30,
	// 	backoff = @Backoff(delay = 10, multiplier = 1.2)
	// )
	@Override
	@Transactional
	public UserPointHistoryDto handleUserPoint2(UserPointHistoryDto userPointHistoryDto) {
		String lockKey = "lock:user:" + userPointHistoryDto.getUserId();
		RLock lock = redissonClient.getLock(lockKey);
		boolean isLocked = false;
		try {
			// 락 획득 시도: 최대 10초 대기, 락 유지 시간 60초
			isLocked = lock.tryLock(10, 60, TimeUnit.SECONDS);
			if (!isLocked) {
				CustomException.illegalArgument("사용자 락을 획득할 수 없습니다. 다시 시도해주세요.", new IllegalArgumentException(), this.getClass());
			}

			// 락을 획득한 후 트랜잭션 내에서 포인트 처리
			User userInfo = userService.getUserInfo2(userPointHistoryDto.getUserId());

			if (userPointHistoryDto.getPointStatus() == PointStatus.EARN) {
				depositUserPoints2(userInfo, userPointHistoryDto.getPointAmount());
			} else if (userPointHistoryDto.getPointStatus() == PointStatus.USE) {
				useUserPoints2(userInfo, userPointHistoryDto.getPointAmount());
			} else {
				CustomException.illegalArgument("유효하지 않은 포인트 상태입니다.", new IllegalArgumentException(), this.getClass());
			}

			return userService.addUserPointHistoryInUser(userPointHistoryDto);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			CustomException.illegalArgument("락 획득이 인터럽트되었습니다.", new IllegalArgumentException(), this.getClass());
		} finally {
			if (isLocked && lock.isHeldByCurrentThread()) {
				lock.unlock();
			}
		}
		return userPointHistoryDto;
	}

	@Override
	public UserDto getUserInfo(Long userId) {
		return userService.getUserInfo(userId);
	}

	/**
	 * 특정 사용자의 PointHistory를 조회
	 *
	 * @param userId
	 * @return List<UserPointHistoryDto>
	 * */
	@Override
	public List<UserPointHistoryDto> getUserPointHistoryListByUserId(Long userId) {
		return userService.getUserPointHistoryFindByUserId(userId);
	}

	/**
	 * 사용자 포인트를 충전합니다.
	 *
	 * @param userInfo 사용자 DTO
	 * @param amount   충전할 금액
	 */
	private void depositUserPoints(UserDto userInfo, long amount) {
		userService.depositBalance(userInfo, amount);
	}

	private void depositUserPoints2(User userInfo, long amount) {
		userService.depositBalance2(userInfo, amount);
	}

	/**
	 * 사용자 포인트를 사용합니다.
	 *
	 * @param userInfo 사용자 DTO
	 * @param amount   사용 금액
	 */
	private void useUserPoints(UserDto userInfo, long amount) {
		if (userInfo.getPointBalance() < amount) {
			throw new IllegalArgumentException("사용 금액이 포인트보다 많습니다.");
		}
		userService.useBalance(userInfo, amount);
	}

	/**
	 * 사용자 포인트를 사용합니다.
	 *
	 * @param userInfo 사용자 DTO
	 * @param amount   사용 금액
	 */
	private void useUserPoints2(User userInfo, long amount) {
		if (userInfo.getPointBalance() < amount) {
			throw new IllegalArgumentException("사용 금액이 포인트보다 많습니다.");
		}
		userService.useBalance2(userInfo, amount);
	}

}
