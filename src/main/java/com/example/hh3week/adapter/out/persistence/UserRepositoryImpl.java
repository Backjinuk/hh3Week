package com.example.hh3week.adapter.out.persistence;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.example.hh3week.adapter.in.dto.user.UserDto;
import com.example.hh3week.application.port.out.UserRepositoryPort;
import com.example.hh3week.domain.user.entity.User;
import com.example.hh3week.domain.user.entity.UserPointHistory;

@Repository
public class UserRepositoryImpl implements UserRepositoryPort {

	@Override
	public UserDto getUserInfo(long userId) {
		return null;
	}

	@Override
	public void updateDepositBalance(User user) {

	}

	@Override
	public UserPointHistory addUserPointHistoryInUser(UserPointHistory entity) {
		return null;
	}

	@Override
	public List<UserPointHistory> getUserPointHistoryFindByUserId(long userId) {
		return List.of();
	}
}
