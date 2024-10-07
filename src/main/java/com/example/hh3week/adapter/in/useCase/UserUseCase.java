package com.example.hh3week.adapter.in.useCase;

import org.springframework.stereotype.Service;

import com.example.hh3week.application.domain.user.service.UserService;

@Service
public class UserUseCase {

	private final UserService userService;

	public UserUseCase(UserService userService) {
		this.userService = userService;
	}
}
