// src/main/java/com/example/hh3week/adapter/out/persistence/PaymentRepositoryImpl.java

package com.example.hh3week.adapter.out.persistence;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.hh3week.application.port.out.PaymentRepositoryPort;
import com.example.hh3week.domain.payment.entity.PaymentHistory;
import com.example.hh3week.domain.payment.entity.QPaymentHistory;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Repository
public class PaymentRepositoryImpl implements PaymentRepositoryPort {

	private final JPAQueryFactory queryFactory;

	@PersistenceContext
	private EntityManager entityManager;

	private final QPaymentHistory qPaymentHistory = QPaymentHistory.paymentHistory;

	public PaymentRepositoryImpl(JPAQueryFactory queryFactory, EntityManager entityManager) {
		this.queryFactory = queryFactory;
		this.entityManager = entityManager;
	}

	@Override
	public PaymentHistory registerPaymentHistory(PaymentHistory paymentHistory) {
		entityManager.persist(paymentHistory);
		return paymentHistory;
	}

	@Override
	public PaymentHistory getPaymentHistory(long paymentId) {
		return queryFactory.selectFrom(qPaymentHistory)
			.where(qPaymentHistory.paymentId.eq(paymentId))
			.fetchOne();
	}

	@Override
	public List<PaymentHistory> getPaymentHistoryByUserId(long userId) {
		return queryFactory.selectFrom(qPaymentHistory)
			.where(qPaymentHistory.userId.eq(userId))
			.orderBy(qPaymentHistory.paymentId.desc())
			.fetch();
	}
}
