package com.example.hh3week.application.port.out;

import com.example.hh3week.domain.waitingQueue.entity.WaitingQueue;
import com.example.hh3week.domain.waitingQueue.entity.WaitingStatus;

public interface WaitingQueueRepositoryPort {

	void addToQueue(WaitingQueue waitingQueue);

	WaitingQueue getNextInQueue(long concertScheduleId);

	void updateStatus(long waitingId, WaitingStatus status);

	WaitingQueue getQueueStatus(long userId, long concertScheduleId);

	int getQueuePosition(long waitingId);
}
