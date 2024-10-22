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

	/**
	 * 결제 내역을 등록합니다.
	 *
	 * @param paymentHistory 등록할 PaymentHistory 객체
	 * @return
	 */
	@Override
	@Transactional
	public PaymentHistory registerPaymentHistory(PaymentHistory paymentHistory) {
		entityManager.persist(paymentHistory);
		return paymentHistory;
	}

	/**
	 * 특정 결제 ID에 대한 결제 내역을 조회합니다.
	 *
	 * @param paymentId 조회할 결제 ID
	 * @return 해당 결제 내역이 있으면 Optional로 반환, 없으면 Optional.empty()
	 */
	@Override
	@Transactional(readOnly = true)
	public PaymentHistory getPaymentHistory(long paymentId) {
		return queryFactory.selectFrom(qPaymentHistory)
			.where(qPaymentHistory.paymentId.eq(paymentId))
			.fetchOne();
	}

	/**
	 * 특정 사용자 ID에 대한 모든 결제 내역을 조회합니다.
	 *
	 * @param userId 조회할 사용자 ID
	 * @return 해당 사용자의 모든 결제 내역 리스트
	 */
	@Override
	@Transactional(readOnly = true)
	public List<PaymentHistory> getPaymentHistoryByUserId(long userId) {
		return queryFactory.selectFrom(qPaymentHistory)
			.where(qPaymentHistory.userId.eq(userId))
			.orderBy(qPaymentHistory.paymentId.desc())
			.fetch();
	}
}
