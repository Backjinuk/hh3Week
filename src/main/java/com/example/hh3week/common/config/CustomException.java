package com.example.hh3week.common.config;

import lombok.extern.slf4j.Slf4j;

/**
 * CustomException 클래스는 NullPointerException 또는 IllegalArgumentException을 던지기 위한 헬퍼 클래스입니다.
 * 예외 발생 시, 예외 메시지와 함께 호출한 클래스, 메서드 이름, 라인 번호를 자동으로 로그에 남깁니다.
 */
@Slf4j
public class CustomException {


	public static void nullPointer(String message, Class<?> clazz) {
		logError(message, clazz, null, "NullPointerException");
		throw new NullPointerException(message);
	}

	public static void illegalArgument(String message, Throwable cause, Class<?> clazz) {
		logError(message, clazz, cause, "IllegalArgumentException");
		throw new IllegalArgumentException(message, cause);
	}

	/**
	 * 예외 발생 위치와 예외 메시지를 포함한 로그를 남깁니다.
	 *
	 * @param message 예외 메시지 (필수)
	 * @param clazz 예외가 발생한 클래스 정보 (필수)
	 * @param cause 원인 예외 (optional)
	 * @param exceptionType 발생한 예외 타입 (필수)
	 */
	private static void logError(String message, Class<?> clazz, Throwable cause, String exceptionType) {
		// 현재 스레드의 스택 트레이스를 가져와서 예외를 발생시킨 메서드 및 라인 번호를 추적합니다.
		StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();

		// 스택 트레이스에서 호출한 메서드 정보를 추출 (index 3은 호출한 메서드)
		StackTraceElement callingElement = stackTraceElements[3];

		// 호출한 클래스의 메서드 이름과 라인 번호 추출
		String methodName = callingElement.getMethodName();
		int lineNumber = callingElement.getLineNumber();

		// 예외 발생 위치 정보를 포함한 로그 메시지 생성
		String logMessage = String.format("Exception occurred in %s.%s() at line %d: %s [%s]",
			clazz.getSimpleName(), methodName, lineNumber, message, exceptionType);

		// 원인 예외에 따라 로그를 다르게 처리 (cause가 null이면 원인 없이 기록)
		if (cause == null) {
			log.error(logMessage);
		} else {
			log.error(logMessage, cause);
		}
	}
}
