package com.example.hh3week.adapter.out.persistence;

import org.springframework.stereotype.Repository;

import com.example.hh3week.application.port.out.WaitingQueueRepositoryPort;
import com.example.hh3week.domain.waitingQueue.entity.WaitingQueue;
import com.example.hh3week.domain.waitingQueue.entity.WaitingStatus;

@Repository
public class WaitingQueueRepositoryImpl implements WaitingQueueRepositoryPort {
	@Override
	public WaitingQueue addToQueue(WaitingQueue waitingQueue) {

		return waitingQueue;
	}

	@Override
	public WaitingQueue getNextInQueue(long concertScheduleId) {
		return null;
	}

	@Override
	public void updateStatus(long waitingId, WaitingStatus status) {

	}

	@Override
	public WaitingQueue getQueueStatus(long userId, long seatDetailId) {
		return null;
	}

	@Override
	public int getQueuePosition(long waitingId) {
		return 0;
	}
}
