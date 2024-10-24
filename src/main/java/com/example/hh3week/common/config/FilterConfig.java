package com.example.hh3week.common.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.hh3week.common.config.LoggingFilter;

@Configuration
public class FilterConfig {

	@Bean
	public FilterRegistrationBean<LoggingFilter> loggingFilter() {
		FilterRegistrationBean<LoggingFilter> registrationBean = new FilterRegistrationBean<>();

		registrationBean.setFilter(new LoggingFilter());
		registrationBean.addUrlPatterns("/*");

		return registrationBean;
	}
}
