package com.example.hh3week.application.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.hh3week.adapter.in.dto.user.UserDto;
import com.example.hh3week.adapter.in.dto.user.UserPointHistoryDto;
import com.example.hh3week.application.port.out.UserRepositoryPort;
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
	@Transactional
	public void depositBalance(UserDto userDto, long amount) {
		if (userDto == null) {

			throw new NullPointerException("UserDto는 null일 수 없습니다.");
		}
		if (amount < 0) {
			throw new IllegalArgumentException("충전 금액은 음수일 수 없습니다.");
		}

		User user = User.toEntity(userDto);

		user.setPointBalance(user.getPointBalance() + amount);
		userRepositoryPort.updateDepositBalance(user);
	}

	/**
	 * 잔액 사용 Use Case
	 *
	 * @param userDto 사용자 ID
	 * @param amount 충전할 금액
	 */
	@Transactional
	public void useBalance(UserDto userDto, long amount) {
		if (userDto == null) {

			throw new NullPointerException("UserDto는 null일 수 없습니다.");
		}
		if (amount < 0) {
			throw new IllegalArgumentException("충전 금액은 음수일 수 없습니다.");
		}

		if(userDto.getPointBalance() <= amount){
			throw new IllegalArgumentException("사용할 포인트가 부족합니다.");
		}

		User user = User.toEntity(userDto);

		user.setPointBalance(user.getPointBalance() - amount);
		userRepositoryPort.updateDepositBalance(user);
	}

	public UserDto getUserInfo(long userId) {
		UserDto userDto = userRepositoryPort.getUserInfo(userId);

		if (userDto == null) {
			throw new IllegalArgumentException("사용자를 찾을수 없습니다.");
		}

		return userDto;
	}

	public UserPointHistoryDto addUserPointHistoryInUser(UserPointHistoryDto userPointHistoryDto) {
		if (userPointHistoryDto == null) {
			throw new IllegalArgumentException("UserPointHistoryDto는 null일 수 없습니다.");
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
