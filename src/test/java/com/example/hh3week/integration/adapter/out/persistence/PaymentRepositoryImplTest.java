package com.example.hh3week.integration.adapter.out.persistence;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import com.example.hh3week.application.port.out.PaymentRepositoryPort;
import com.example.hh3week.domain.payment.entity.PaymentHistory;
import com.example.hh3week.domain.payment.entity.PaymentStatus;

@SpringBootTest
@Transactional
@Sql({"classpath:schema.sql", "classpath:data.sql"})
public class PaymentRepositoryImplTest {

	@Autowired
	private PaymentRepositoryPort paymentRepositoryImpl;

	@Test
	@DisplayName("결제 내역 등록 - 정상적으로 추가됨")
	void 결제내역등록_정상적으로추가됨() {
		// Given
		PaymentHistory newPayment = PaymentHistory.builder()
			.userId(103L)
			.reservationId(1004L)
			.paymentAmount(15000L)
			.paymentStatus(PaymentStatus.COMPLETED)
			.paymentDt(LocalDateTime.of(2024, 4, 1, 14, 0))
			.build();

		// When
		paymentRepositoryImpl.registerPaymentHistory(newPayment);

		// Then
		PaymentHistory fetchedPaymentOpt = paymentRepositoryImpl.getPaymentHistory(newPayment.getPaymentId());
		assertThat(fetchedPaymentOpt.getUserId()).isEqualTo(103L);
		assertThat(fetchedPaymentOpt.getReservationId()).isEqualTo(1004L);
		assertThat(fetchedPaymentOpt.getPaymentAmount()).isEqualTo(15000L);
		assertThat(fetchedPaymentOpt.getPaymentDt()).isEqualTo(LocalDateTime.of(2024, 4, 1, 14, 0));
		assertThat(fetchedPaymentOpt.getPaymentStatus()).isEqualTo(PaymentStatus.COMPLETED);
	}

	@Test
	@DisplayName("결제 내역 조회 - 존재하는 결제 ID일 경우 결제 내역 반환")
	void 결제내역조회_존재하는결제ID일경우결제내역반환() {
		// Given
		long existingPaymentId = 1L;

		// When
		PaymentHistory aymentOpt = paymentRepositoryImpl.getPaymentHistory(existingPaymentId);

		// Then
		assertThat(aymentOpt.getPaymentId()).isEqualTo(existingPaymentId);
		assertThat(aymentOpt.getUserId()).isEqualTo(101L);
		assertThat(aymentOpt.getReservationId()).isEqualTo(1001L);
		assertThat(aymentOpt.getPaymentAmount()).isEqualTo(10000L);
		assertThat(aymentOpt.getPaymentStatus()).isEqualTo(PaymentStatus.COMPLETED);
	}

	@Test
	@DisplayName("결제 내역 조회 - 존재하지 않는 결제 ID일 경우 Optional.empty() 반환")
	void 결제내역조회_존재하지않는결제ID일경우OptionalEmpty반환() {
		// Given
		long nonExistentPaymentId = 999L;

		// When
		PaymentHistory paymentOpt = paymentRepositoryImpl.getPaymentHistory(nonExistentPaymentId);

		// Then
		assertThat(paymentOpt).isNull();
	}

	@Test
	@DisplayName("사용자 ID로 결제 내역 조회 - 해당 사용자에게 할당된 결제 내역이 존재할 경우 반환")
	void 사용자ID로결제내역조회_해당사용자에게할당된결제내역이존재할경우반환() {
		// Given
		long userId = 101L;

		// When
		List<PaymentHistory> paymentList = paymentRepositoryImpl.getPaymentHistoryByUserId(userId);

		// Then
		assertThat(paymentList).hasSize(2);
		assertThat(paymentList).extracting("userId").containsOnly(101L);
	}

	@Test
	@DisplayName("사용자 ID로 결제 내역 조회 - 해당 사용자에게 할당된 결제 내역이 없을 경우 빈 리스트 반환")
	void 사용자ID로결제내역조회_해당사용자에게할당된결제내역이없을경우빈리스트반환() {
		// Given
		long userId = 999L;

		// When
		List<PaymentHistory> paymentList = paymentRepositoryImpl.getPaymentHistoryByUserId(userId);

		// Then
		assertThat(paymentList).isEmpty();
	}
}
