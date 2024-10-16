package com.example.hh3week.common.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * 애플리케이션 전역에서 발생하는 예외를 처리하는 글로벌 예외 핸들러 클래스입니다.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

	/**
	 * IllegalArgumentException 발생 시 400 Bad Request 응답을 반환합니다.
	 *
	 * @param ex 발생한 IllegalArgumentException
	 * @return 에러 메시지를 담은 ResponseEntity 객체
	 */
	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
	}

	/**
	 * 모든 예외 발생 시 500 Internal Server Error 응답을 반환합니다.
	 *
	 * @param ex 발생한 Exception
	 * @return 에러 메시지를 담은 ResponseEntity 객체
	 */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<String> handleGeneralException(Exception ex) {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
			.body("서버 오류가 발생했습니다. 잠시 후 다시 시도해 주세요.");
	}

	// 기타 예외 핸들러들...
}
