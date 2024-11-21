package com.example.hh3week.domain.outBox;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReservationOutBox {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String aggregateType;  // 이벤트 타입
	private String aggregateId;    // 관련된 ID (예: userId)
	private String type;           // 이벤트의 유형 (예: 예약 요청)

	@Lob
	private String payload;        // 이벤트의 실제 내용 (JSON 형식 등)
	private LocalDateTime createdAt; // 이벤트 생성 시간
	private boolean processed;     // 처리 여부 (true: 처리됨, false: 미처리)


	@Builder
	public ReservationOutBox(Long id, String aggregateType, String aggregateId, String type, String payload,
		LocalDateTime createdAt, boolean processed) {
		this.id = id;
		this.aggregateType = aggregateType;
		this.aggregateId = aggregateId;
		this.type = type;
		this.payload = payload;
		this.createdAt = createdAt;
		this.processed = processed;
	}

}