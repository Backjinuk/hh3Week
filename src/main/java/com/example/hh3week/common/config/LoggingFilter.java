package com.example.hh3week.common.config;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoggingFilter implements Filter {


	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
		throws IOException, ServletException {

		HttpServletRequest httpRequest = (HttpServletRequest) request;

		log.info("Incoming Request: URI: {}, Method: {}", httpRequest.getRequestURI(), httpRequest.getMethod());

		// 필터 체인 진행
		chain.doFilter(request, response);

		// 응답 처리 로깅 (필요하면 추가 가능)
	}

}
