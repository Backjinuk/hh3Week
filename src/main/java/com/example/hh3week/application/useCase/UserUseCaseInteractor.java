package com.example.hh3week.application.useCase;

import org.springframework.stereotype.Service;

import com.example.hh3week.application.port.in.UserUseCase;
import com.example.hh3week.application.service.UserService;

@Service
public class UserUseCaseInteractor implements UserUseCase {

	private final UserService userService;

	public UserUseCaseInteractor(UserService userService) {
		this.userService = userService;
	}
}
