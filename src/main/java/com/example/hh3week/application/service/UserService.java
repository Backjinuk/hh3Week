package com.example.hh3week.application.service;

import org.springframework.stereotype.Service;

import com.example.hh3week.application.port.out.UserRepositoryPort;

@Service
public class UserService {

	private final UserRepositoryPort userRepositoryPort;

	public UserService(UserRepositoryPort userRepositoryPort) {
		this.userRepositoryPort = userRepositoryPort;
	}
}
