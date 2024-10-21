package com.example.hh3week.application.useCase;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.hh3week.adapter.in.dto.user.UserDto;
import com.example.hh3week.adapter.in.dto.user.UserPointHistoryDto;
import com.example.hh3week.adapter.in.dto.validation.DtoValidation;
import com.example.hh3week.application.port.in.UserUseCase;
import com.example.hh3week.application.service.UserService;
import com.example.hh3week.common.config.CustomException;
import com.example.hh3week.domain.user.entity.PointStatus;

@Service
public class UserUseCaseInteract implements UserUseCase {

	private final UserService userService;

	public UserUseCaseInteract(UserService userService) {
		this.userService = userService;
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
}
