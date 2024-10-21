package com.example.hh3week.integration.adapter.out.persistence;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import com.example.hh3week.adapter.out.persistence.UserRepositoryImpl;
import com.example.hh3week.domain.user.entity.PointStatus;
import com.example.hh3week.domain.user.entity.User;
import com.example.hh3week.domain.user.entity.UserPointHistory;

import jakarta.persistence.EntityManager;

@SpringBootTest
@Transactional // 각 테스트가 트랜잭션 내에서 실행되고, 테스트 후 롤백됨
@Sql({"classpath:schema.sql", "classpath:data.sql"})
public class UserRepositoryImplTest {

	@Autowired
	private UserRepositoryImpl userRepositoryImpl;

	@Autowired
	private EntityManager entityManager;

	@Test
	@DisplayName("사용자 정보 조회 - 사용자 존재 시 사용자 반환")
	void 사용자정보조회_사용자존재시사용자반환() {
		// Given
		long userId = 1L;

		System.out.println("1");

		// When
		User fetchedUser = userRepositoryImpl.getUserInfo(userId);

		System.out.println("2");

		// Then
		assertThat(fetchedUser).isNotNull();
		assertThat(fetchedUser.getUserId()).isEqualTo(userId);
		//assertThat(fetchedUser.getUserName()).isEqualTo("유저 A");
		assertThat(fetchedUser.getPointBalance()).isEqualTo(100000L);

		System.out.println("3");
	}

	@Test
	@DisplayName("사용자 정보 조회 - 사용자 존재하지 않을 시 null 반환")
	void 사용자정보조회_사용자존재하지않을시null반환() {
		// Given
		long nonExistentUserId = 999L;

		// When

		// Then
		assertThrows(NullPointerException.class, () -> {
			userRepositoryImpl.getUserInfo(nonExistentUserId);
		});

	}

	@Test
	@DisplayName("예치금 잔액 업데이트 - 성공적으로 잔액이 업데이트됨")
	void 예치금잔액업데이트_성공적으로잔액이업데이트됨() {
		// Given
		long userId = 2L;
		User user = userRepositoryImpl.getUserInfo(userId);
		user.setPointBalance(80000L);

		// When
		userRepositoryImpl.updateDepositBalance(user);

		// Then
		User updatedUser = userRepositoryImpl.getUserInfo(userId);
		assertThat(updatedUser).isNotNull();
		assertThat(updatedUser.getPointBalance()).isEqualTo(80000L);
	}

	@Test
	@DisplayName("예치금 잔액 업데이트 - 존재하지 않는 사용자일 경우 예외 발생")
	void 예치금잔액업데이트_존재하지않는사용자일경우예외발생() {
		// Given
		User nonExistentUser = User.builder().userId(999L).pointBalance(80000L).build();

		// When & Then
		NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
			userRepositoryImpl.updateDepositBalance(nonExistentUser);
		});

		assertThat(nullPointerException.getMessage()).isEqualTo("사용자를 찾을 수 없습니다.");
	}

	@Test
	@DisplayName("포인트 히스토리 추가 - 성공적으로 히스토리가 추가됨")
	void 포인트히스토리추가_성공적으로히스토리가추가됨() {
		// Given
		long userId = 3L;
		UserPointHistory history = UserPointHistory.builder()
			.userId(userId)
			.pointAmount(1500L)
			.pointStatus(PointStatus.EARN)
			.build();

		// When
		UserPointHistory savedHistory = userRepositoryImpl.addUserPointHistoryInUser(history);

		// Then
		assertThat(savedHistory.getHistoryId()).isNotNull();
		assertThat(savedHistory.getPointDt()).isNotNull();
		assertThat(savedHistory.getUserId()).isEqualTo(userId);
		assertThat(savedHistory.getPointAmount()).isEqualTo(1500L);
		assertThat(savedHistory.getPointStatus()).isEqualTo(PointStatus.EARN);

		// 추가적으로 데이터베이스에 실제로 저장되었는지 확인
		List<UserPointHistory> histories = userRepositoryImpl.getUserPointHistoryFindByUserId(userId);
		assertThat(histories).hasSize(6);
		UserPointHistory fetchedHistory = histories.get(histories.size() - 1);
		assertThat(fetchedHistory.getHistoryId()).isEqualTo(savedHistory.getHistoryId());
		assertThat(fetchedHistory.getPointDt()).isEqualTo(savedHistory.getPointDt());
		assertThat(fetchedHistory.getPointAmount()).isEqualTo(1500L);
		assertThat(fetchedHistory.getPointStatus()).isEqualTo(PointStatus.EARN);
	}

	@Test
	@DisplayName("사용자 포인트 히스토리 조회 - 히스토리가 존재할 경우 히스토리 목록 반환")
	void 사용자포인트히스토리조회_히스토리가존재할경우히스토리목록반환() {
		// Given
		long userId = 1L;

		// When
		List<UserPointHistory> histories = userRepositoryImpl.getUserPointHistoryFindByUserId(userId);

		// Then
		assertThat(histories).hasSize(5);
		assertThat(histories).extracting("pointAmount").containsExactlyInAnyOrder(10000L, 5000L, 20000L, 7000L, 15000L);
		assertThat(histories).extracting("pointStatus")
			.containsExactlyInAnyOrder(PointStatus.EARN, PointStatus.USE, PointStatus.EARN, PointStatus.USE,
				PointStatus.EARN);
	}

	@Test
	@DisplayName("사용자 포인트 히스토리 조회 - 히스토리가 존재하지 않을 경우 빈 목록 반환")
	void 사용자포인트히스토리조회_히스토리가존재하지않을경우빈목록반환() {
		// Given
		long userId = 999L; // 존재하지 않는 사용자 ID

		// When

		assertThatThrownBy(() -> userRepositoryImpl.getUserPointHistoryFindByUserId(userId))
			.isInstanceOf(NullPointerException.class)
			.hasMessage("사용자를 찾을 수 없습니다.");
	}
}
