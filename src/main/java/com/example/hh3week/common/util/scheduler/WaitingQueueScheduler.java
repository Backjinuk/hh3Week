package com.example.hh3week.common.util.scheduler;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.hh3week.adapter.in.dto.waitingQueue.WaitingQueueDto;
import com.example.hh3week.application.service.WaitingQueueService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class WaitingQueueScheduler {

    private final WaitingQueueService waitingQueueService;

    public WaitingQueueScheduler(WaitingQueueService waitingQueueService) {
        this.waitingQueueService = waitingQueueService;
    }

	/**
	 * 매 1분마다 대기열을 확인하고 만료된 항목을 처리하는 메서드
	 */
	@Scheduled(fixedRate = 60000) // 60,000 밀리초 = 1분
	public void processExpiredQueues() {
		LocalDateTime currentTime = LocalDateTime.now();
		List<WaitingQueueDto> expiredQueues = waitingQueueService.getExpiredQueues(currentTime);

		for (WaitingQueueDto queue : expiredQueues) {
			try {
				waitingQueueService.expireQueue(queue.getWaitingId());
				// 필요한 추가 작업: 좌석 상태 업데이트, 사용자 알림 등
				log.info("Expired WaitingQueue ID: {}", queue.getWaitingId());
			} catch (Exception e) {
				log.error("Failed to expire WaitingQueue ID: {}", queue.getWaitingId(), e);
			}
		}
	}
}