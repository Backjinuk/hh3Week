package com.example.hh3week.adapter.out.streaming.kafka.dto;

import com.example.hh3week.adapter.in.dto.token.TokenDto;
import com.example.hh3week.adapter.in.dto.waitingQueue.WaitingQueueDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TokenIssuedResponse  {
	private String correlationId;
	private TokenDto tokenDto;
	private String message;
}
