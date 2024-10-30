package com.example.hh3week.application.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.hh3week.adapter.in.dto.user.UserDto;
import com.example.hh3week.adapter.in.dto.user.UserPointHistoryDto;
import com.example.hh3week.application.port.out.UserRepositoryPort;
import com.example.hh3week.common.config.CustomException;
import com.example.hh3week.domain.user.entity.User;
import com.example.hh3week.domain.user.entity.UserPointHistory;

@Service
public class UserService {

	private final UserRepositoryPort userRepositoryPort;

	public UserService(UserRepositoryPort userRepositoryPort) {
		this.userRepositoryPort = userRepositoryPort;
	}

	/**
	 * 잔액 충전 Use Case
	 *
	 * @param userDto 사용자 ID
	 * @param amount 충전할 금액
	 */
	public void depositBalance(UserDto userDto, long amount) {
		if (userDto == null) {

			CustomException.nullPointer("UserDto는 null일 수 없습니다.", this.getClass());
		}
		if (amount < 0) {
			CustomException.illegalArgument("충전 금액은 음수일 수 없습니다.", new IllegalArgumentException(), this.getClass());
		}

		User user = User.toEntity(userDto);

		user.setPointBalance(user.getPointBalance() + amount);
		userRepositoryPort.updateDepositBalance(user);
	}



	public void depositBalance2(User userDto, long amount) {
		if (userDto == null) {

			CustomException.nullPointer("UserDto는 null일 수 없습니다.", this.getClass());
		}
		if (amount < 0) {
			CustomException.illegalArgument("충전 금액은 음수일 수 없습니다.", new IllegalArgumentException(), this.getClass());
		}


		userDto.setPointBalance(userDto.getPointBalance() + amount);
		userRepositoryPort.updateDepositBalance(userDto);
	}


	/**
	 * 잔액 사용 Use Case
	 *
	 * @param userDto 사용자 ID
	 * @param amount 충전할 금액
	 */
	public void useBalance(UserDto userDto, long amount) {
		if (userDto == null) {

			CustomException.nullPointer("UserDto는 null일 수 없습니다.", this.getClass());

		}
		if (amount < 0) {
			CustomException.illegalArgument("충전 금액은 음수일 수 없습니다.", new IllegalArgumentException(), this.getClass());
		}

		if(userDto.getPointBalance() <= amount){
			CustomException.illegalArgument("사용할 포인트가 부족합니다.", new IllegalArgumentException(), this.getClass());
		}

		User user = User.toEntity(userDto);

		user.setPointBalance(user.getPointBalance() - amount);
		userRepositoryPort.updateDepositBalance(user);
	}


	/**
	 * 잔액 사용 Use Case
	 *
	 * @param userDto 사용자 ID
	 * @param amount 충전할 금액
	 */
	public void useBalance2(User userDto, long amount) {

		userDto.setPointBalance(userDto.getPointBalance() - amount);
		userRepositoryPort.updateDepositBalance(userDto);
	}


	public UserDto getUserInfo(long userId) {
		UserDto userDto = UserDto.toDto(userRepositoryPort.getUserInfo(userId));

		if (userDto.getUserId() <= 0) {
			CustomException.illegalArgument("사용자를 찾을수 없습니다.", new IllegalArgumentException(), this.getClass());
		}

		return userDto;
	}


	public User getUserInfo2(long userId) {
		User userInfo = userRepositoryPort.getUserInfo(userId);

		if (userInfo.getUserId() <= 0) {
			CustomException.illegalArgument("사용자를 찾을수 없습니다.", new IllegalArgumentException(), this.getClass());
		}

		return userInfo;
	}


	public UserPointHistoryDto addUserPointHistoryInUser(UserPointHistoryDto userPointHistoryDto) {
		if (userPointHistoryDto == null) {
			CustomException.illegalArgument("UserPointHistoryDto는 null일 수 없습니다.", new IllegalArgumentException(), this.getClass());
		}
		UserPointHistory userPointHistory = userRepositoryPort.addUserPointHistoryInUser(
			UserPointHistory.toEntity(userPointHistoryDto));
		return UserPointHistoryDto.toDto(userPointHistory);
	}

	public List<UserPointHistoryDto> getUserPointHistoryFindByUserId(long userId) {
		return userRepositoryPort.getUserPointHistoryFindByUserId(userId)
			.stream()
			.map(UserPointHistoryDto::toDto)
			.toList();

	}

}
