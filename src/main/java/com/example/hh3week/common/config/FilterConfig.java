package com.example.hh3week.common.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class FilterConfig {

	@Bean
	public FilterRegistrationBean<LoggingFilter> loggingFilter() {
		FilterRegistrationBean<LoggingFilter> registrationBean = new FilterRegistrationBean<>();

		registrationBean.setFilter(new LoggingFilter()); // 커스텀 필터 등록
		registrationBean.addUrlPatterns("/*"); // 모든 요청에 필터 적용

		return registrationBean;
	}
}
