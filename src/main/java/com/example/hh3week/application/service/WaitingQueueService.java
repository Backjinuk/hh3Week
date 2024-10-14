package com.example.hh3week.application.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.hh3week.adapter.in.dto.waitingQueue.WaitingQueueDto;
import com.example.hh3week.application.port.out.WaitingQueueRepositoryPort;
import com.example.hh3week.domain.waitingQueue.entity.WaitingQueue;
import com.example.hh3week.domain.waitingQueue.entity.WaitingStatus;

@Service
public class WaitingQueueService {

	private final WaitingQueueRepositoryPort waitingQueueRepository;

	@Autowired
	public WaitingQueueService(WaitingQueueRepositoryPort waitingQueueRepository) {
		this.waitingQueueRepository = waitingQueueRepository;
	}

	/**
	 * 사용자가 대기열에 이미 있는지 확인하는 메서드입니다.
	 *
	 * @param userId 사용자의 UUID
	 * @param concertScheduleId 콘서트 스케줄의 고유 ID
	 * @return 사용자가 대기열에 있는 경우 true, 아니면 false
	 */
	@Transactional(readOnly = true)
	public boolean isUserInQueue(long userId, long concertScheduleId) {
		WaitingQueue existingQueue = waitingQueueRepository.getQueueStatus(userId, concertScheduleId);
		return existingQueue != null;
	}


	/**
	 * 대기열에 대기 항목을 추가하는 메서드입니다.
	 *
	 * @param waitingQueueDto 대기 항목 엔티티
	 */
	@Transactional
	public void addWaitingQueue(WaitingQueueDto waitingQueueDto) {
		waitingQueueRepository.addToQueue(WaitingQueue.ToEntity(waitingQueueDto));
	}

	/**
	 * 대기열에서 다음 사용자를 가져오는 메서드입니다.
	 *
	 * @param concertScheduleId 콘서트 스케줄의 고유 ID
	 * @return 다음 대기 중인 사용자의 WaitingQueueDto 또는 null
	 */
	@Transactional(readOnly = true)
	public WaitingQueueDto getNextInQueue(long concertScheduleId) {
		WaitingQueue nextInQueue = waitingQueueRepository.getNextInQueue(concertScheduleId);
		return WaitingQueueDto.ToDto(nextInQueue);
	}

	/**
	 * 대기열에서 사용자의 위치를 계산하는 메서드입니다.
	 *
	 * @param waitingId 대기열 항목의 고유 ID
	 * @return 대기열에서의 위치
	 */
	@Transactional(readOnly = true)
	public int getQueuePosition(long waitingId) {
		return waitingQueueRepository.getQueuePosition(waitingId);
	}

	/**
	 * 대기열 항목의 상태를 업데이트하는 메서드입니다.
	 *
	 * @param waitingId 대기열 항목의 고유 ID
	 * @param status 업데이트할 상태 (예: "NOTIFIED", "EXPIRED")
	 */
	@Transactional
	public void updateStatus(long waitingId, WaitingStatus status) {
		waitingQueueRepository.updateStatus(waitingId, status);
	}

	/**
	 * 사용자의 대기열 상태를 조회하는 메서드입니다.
	 *
	 * @param userId 사용자의 UUID
	 * @param concertScheduleId 콘서트 스케줄의 고유 ID
	 * @return 사용자의 WaitingQueueDto 또는 null
	 */
	@Transactional(readOnly = true)
	public WaitingQueueDto getQueueStatus(long userId, long concertScheduleId) {
		WaitingQueue queueStatus = waitingQueueRepository.getQueueStatus(userId, concertScheduleId);
		return (queueStatus != null) ? WaitingQueueDto.ToDto(queueStatus) : null;
	}
}
