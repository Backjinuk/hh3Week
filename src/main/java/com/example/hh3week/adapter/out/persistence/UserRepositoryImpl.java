package com.example.hh3week.adapter.out.persistence;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.hh3week.application.port.out.UserRepositoryPort;
import com.example.hh3week.domain.user.entity.QUser;
import com.example.hh3week.domain.user.entity.QUserPointHistory;
import com.example.hh3week.domain.user.entity.User;
import com.example.hh3week.domain.user.entity.UserPointHistory;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;

@Repository
@Transactional
public class UserRepositoryImpl implements UserRepositoryPort {

	private final JPAQueryFactory queryFactory;
	private final EntityManager entityManager;

	private final QUser qUser = QUser.user;
	private final QUserPointHistory qUserPointHistory = QUserPointHistory.userPointHistory;

	// 생성자 주입
	public UserRepositoryImpl(JPAQueryFactory queryFactory, EntityManager entityManager) {
		this.queryFactory = queryFactory;
		this.entityManager = entityManager;
	}

	@Override
	public User getUserInfo(long userId) {
		User user = queryFactory.selectFrom(qUser).where(qUser.userId.eq(userId)).fetchOne();

		if(user == null){
			throw new NullPointerException("사용자를 찾을 수 없습니다.");
		}
		return user;
	}

	@Override
	public void updateDepositBalance(User user) {
		queryFactory.update(qUser)
			.set(qUser.pointBalance, user.getPointBalance())
			.where(qUser.userId.eq(user.getUserId()))
			.execute();
	}

	@Override
	public UserPointHistory addUserPointHistoryInUser(UserPointHistory userPointHistory) {

		userPointHistory.setPointDt(LocalDateTime.now());
		entityManager.persist(userPointHistory);
		return userPointHistory;
	}

	@Override
	public List<UserPointHistory> getUserPointHistoryFindByUserId(long userId) {
		return queryFactory.selectFrom(qUserPointHistory)
			.where(qUserPointHistory.userId.eq(userId))
			.fetch();
	}
}
