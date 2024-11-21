package com.example.hh3week.application.port.out;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.hh3week.domain.outBox.ReservationOutBox;

@Repository
public interface OutboxEventRepository {

	ReservationOutBox findByAggregateId(String correlationId);

	List<ReservationOutBox> findByProcessedFalse();

	void addReservationOutBox(ReservationOutBox reservationOutBox);
}
