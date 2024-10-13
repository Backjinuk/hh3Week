package com.example.hh3week.application.port.out;

import java.util.List;

import com.example.hh3week.adapter.in.dto.user.UserDto;
import com.example.hh3week.adapter.in.dto.user.UserPointHistoryDto;
import com.example.hh3week.domain.user.entity.User;
import com.example.hh3week.domain.user.entity.UserPointHistory;

public interface UserRepositoryPort {

	public UserDto getUserInfo(long userId);

	void updateDepositBalance(User user);

	UserPointHistory addUserPointHistoryInUser(UserPointHistory entity);

	List<UserPointHistory> getUserPointHistoryFindByUserId(long userId);
}
