package com.example.hh3week.adapter.in.dto.user;

import com.example.hh3week.domain.user.entity.User;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserDto {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long userId;

	private String userName;

	private long pointBalance;

	@Builder
	public UserDto(long userId, String userName, long pointBalance) {
		this.userId = userId;
		this.userName = userName;
		this.pointBalance = pointBalance;
	}

	public static UserDto toDto(User user) {
		return UserDto.builder()
			.userId(user.getUserId())
			.userName(user.getUserName())
			.pointBalance(user.getPointBalance())
			.build();
	}
}

