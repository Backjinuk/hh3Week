package com.example.hh3week.application.service;

import java.time.LocalDateTime;
import java.util.List;

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
	 * 사용자가 대기열에 이미 등록되어 있는지 확인하는 메서드입니다.
	 *
	 * @param userId        사용자의 ID
	 * @param seatDetailId  좌석 상세 정보의 ID
	 * @return 대기열에 등록되어 있으면 true, 아니면 false
	 */
	public boolean isUserInQueue(long userId, long seatDetailId) {
		WaitingQueue existingQueue = waitingQueueRepository.getQueueStatus(userId, seatDetailId);
		return  existingQueue != null && existingQueue.getWaitingStatus() == WaitingStatus.WAITING;
	}

	/**
	 * 사용자를 대기열에 추가하고, 생성된 대기열 항목의 ID를 반환하는 메서드입니다.
	 *
	 * @param waitingQueueDto 대기열에 추가할 사용자의 정보가 담긴 DTO
	 * @return 생성된 대기열 항목의 ID
	 */
	public WaitingQueueDto addWaitingQueue(WaitingQueueDto waitingQueueDto) {
		WaitingQueue waitingQueue = WaitingQueue.ToEntity(waitingQueueDto);
		return WaitingQueueDto.ToDto(waitingQueueRepository.addToQueue(waitingQueue));
	}

	/**
	 * 특정 좌석에 대해 대기열의 다음 사용자를 조회하는 메서드입니다.
	 *
	 * @param seatDetailId 좌석 상세 정보의 ID
	 * @return 다음 대기 중인 사용자의 WaitingQueueDto 또는 null
	 */
	public WaitingQueueDto getNextInQueue(long seatDetailId) {
		WaitingQueue nextInQueue = waitingQueueRepository.getNextInQueue(seatDetailId);
		return WaitingQueueDto.ToDto(nextInQueue);
	}

	/**
	 * 특정 대기열 항목의 대기 위치를 계산하는 메서드입니다.
	 *
	 * @param waitingId 대기열 항목의 ID
	 * @return 대기열 내에서의 위치 (1부터 시작)
	 */
	public int getQueuePosition(long waitingId) {
		return waitingQueueRepository.getQueuePosition(waitingId);
	}

	/**
	 * 특정 대기열 항목의 상태를 업데이트하는 메서드입니다.
	 *
	 * @param waitingId 대기열 항목의 ID
	 * @param status    업데이트할 상태 (예: WAITING, EXPIRED, COMPLETED)
	 */
	public void updateStatus(long waitingId, WaitingStatus status) {
		waitingQueueRepository.updateStatus(waitingId, status);
	}

	/**
	 * 특정 사용자가 특정 좌석에 대해 현재 대기열에 등록되어 있는지 조회하는 메서드입니다.
	 *
	 * @param userId             사용자의 ID
	 * @param concertScheduleId  콘서트 스케줄의 ID (좌석 상세 정보와 연관됨)
	 * @return 사용자의 WaitingQueueDto 또는 null
	 */
	public WaitingQueueDto getQueueStatus(long userId, long concertScheduleId) {
		WaitingQueue queueStatus = waitingQueueRepository.getQueueStatus(userId, concertScheduleId);
		return (queueStatus != null) ? WaitingQueueDto.ToDto(queueStatus) : null;
	}

	/**
	 * 특정 대기열 항목을 만료 상태로 변경하는 메서드입니다.
	 *
	 * @param waitingId 대기열 항목의 ID
	 */
	public void expireQueue(long waitingId) {
		waitingQueueRepository.expireQueue(waitingId);
	}

	/**
	 * 특정 좌석에 대한 모든 대기열 항목을 조회하는 메서드입니다.
	 *
	 * @param seatDetailId 좌석 상세 정보의 ID
	 * @return 해당 좌석에 대한 모든 대기열 항목의 리스트
	 */
	public List<WaitingQueue> getQueueBySeatDetailId(long seatDetailId) {
		return waitingQueueRepository.getQueueBySeatDetailId(seatDetailId);
	}

	/**
	 * 모든 대기열 항목을 초기화(삭제)하는 메서드입니다.
	 * 주로 테스트 환경에서 사용됩니다.
	 */
	@Transactional
	public void clearQueue() {
		waitingQueueRepository.clearQueue();
	}


	/**
	 * 현재 시각을 기준으로 만료된 대기열 항목들을 조회하는 메서드입니다.
	 *
	 * @param currentTime 현재 시각
	 * @return 만료된 대기열 항목들의 리스트
	 */
	public List<WaitingQueueDto> getExpiredQueues(LocalDateTime currentTime) {
		return waitingQueueRepository.findExpiredQueues(currentTime)
			.stream()
			.map(WaitingQueueDto::ToDto)
			.toList();
	}

}
