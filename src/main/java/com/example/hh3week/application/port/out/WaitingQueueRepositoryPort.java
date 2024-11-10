package com.example.hh3week.application.port.out;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import com.example.hh3week.domain.waitingQueue.entity.WaitingQueue;
import com.example.hh3week.domain.waitingQueue.entity.WaitingStatus;

public interface WaitingQueueRepositoryPort {

	WaitingQueue addToQueue(WaitingQueue waitingQueue);

	WaitingQueue getNextInQueue(long concertScheduleId);

	void updateStatus(long waitingId, WaitingStatus status);

	WaitingQueue getQueueStatus(long userId, long seatDetailId);

	int getQueuePosition(long waitingId);

	int getQueuePosition(long waitingId, long seatDetailId);

	void expireQueue(long waitingId);

	List<WaitingQueue> getQueueBySeatDetailId(long seatDetailId);

	void clearQueue();

	List<WaitingQueue> findExpiredQueues(LocalDateTime currentTime);

	Long findMaxPriorityBySeatDetailIdForUpdate(long seatDetailId);

}
