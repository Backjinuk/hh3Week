
package com.example.hh3week.common.config.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.NoSuchElementException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	/**
	 * IllegalArgumentException 처리 핸들러
	 */
	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
		log.error("IllegalArgumentException: {}", ex.getMessage(), ex);  // 예외 정보 로그 기록
		ErrorResponse error = new ErrorResponse(ex.getMessage());
		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
	}

	/**
	 * NullPointerException 처리 핸들러
	 */
	@ExceptionHandler(NullPointerException.class)
	public ResponseEntity<ErrorResponse> handleNullPointerException(NullPointerException ex) {
		log.error("NullPointerException: {}", ex.getMessage(), ex);  // 예외 정보 로그 기록
		ErrorResponse error = new ErrorResponse(ex.getMessage());
		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
	}

	/**
	 * IllegalStateException 처리 핸들러
	 */
	@ExceptionHandler(IllegalStateException.class)
	public ResponseEntity<ErrorResponse> handleIllegalStateException(IllegalStateException ex) {
		log.error("IllegalStateException: {}", ex.getMessage(), ex);  // 예외 정보 로그 기록
		ErrorResponse error = new ErrorResponse(ex.getMessage());
		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
	}

	/**
	 * NoSuchElementException 처리 핸들러
	 */
	@ExceptionHandler(NoSuchElementException.class)
	public ResponseEntity<ErrorResponse> handleNoSuchElementException(NoSuchElementException ex) {
		log.error("NoSuchElementException: {}", ex.getMessage(), ex);  // 예외 정보 로그 기록
		ErrorResponse error = new ErrorResponse(ex.getMessage());
		return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
	}

	/**
	 * 기타 Exception 처리 핸들러
	 */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleException(Exception ex) {
		log.error("Exception: {}", ex.getMessage(), ex);  // 예외 정보 로그 기록
		ErrorResponse error = new ErrorResponse("서버 오류가 발생했습니다. 잠시 후 다시 시도해 주세요.");
		return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * 오류 응답 데이터 구조 정의
	 */
	public static class ErrorResponse {
		private String message;

		public ErrorResponse() {}

		public ErrorResponse(String message) {
			this.message = message;
		}

		// Getter 및 Setter
		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}
	}
}
