package com.example.hh3week.common.config.swagger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

@Configuration
@EnableRetry(proxyTargetClass=true)
public class RetryConfig {

	@Bean
	public RetryTemplate retryTemplate() {
		RetryTemplate retryTemplate = new RetryTemplate();

		// Retry Policy 설정 (예: 최대 3회 시도)
		SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
		retryPolicy.setMaxAttempts(3);
		retryTemplate.setRetryPolicy(retryPolicy);

		// BackOff Policy 설정 (예: 지수 백오프)
		ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
		backOffPolicy.setInitialInterval(100); // 초기 대기 시간 (밀리초)
		backOffPolicy.setMultiplier(1.2); // 배수
		backOffPolicy.setMaxInterval(1000); // 최대 대기 시간
		retryTemplate.setBackOffPolicy(backOffPolicy);

		return retryTemplate;
	}
}
