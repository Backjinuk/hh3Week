package com.example.hh3week.application.port.in;

import java.util.List;

import com.example.hh3week.adapter.in.dto.user.UserDto;
import com.example.hh3week.adapter.in.dto.user.UserPointHistoryDto;

public interface UserUseCase {

	UserPointHistoryDto handleUserPoint(UserPointHistoryDto userPointHistoryDto);

	UserDto getUserInfo(Long userId);

	List<UserPointHistoryDto> getUserPointHistoryListByUserId(Long userId);
}