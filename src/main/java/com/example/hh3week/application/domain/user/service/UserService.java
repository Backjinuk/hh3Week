package com.example.hh3week.application.domain.user.service;

import org.springframework.stereotype.Service;

import com.example.hh3week.application.domain.user.repository.UserRepository;

@Service
public class UserService {

	private final UserRepository userRepository;

	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}
}
