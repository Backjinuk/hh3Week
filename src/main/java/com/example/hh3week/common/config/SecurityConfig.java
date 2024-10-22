package com.example.hh3week.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.hh3week.common.util.JwtFilter;
import com.example.hh3week.common.util.JwtProvider;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

	private final JwtProvider jwtProvider;

	public SecurityConfig(JwtProvider jwtProvider) {
		this.jwtProvider = jwtProvider;
	}

	@Bean
	public JwtFilter jwtFilter() {
		return new JwtFilter(jwtProvider);
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		http
			.csrf(csrf -> csrf.disable())  // CSRF 보호 비활성화
			.authorizeHttpRequests(auth -> auth
				.requestMatchers("/api/v1/payment/registerPaymentHistory").authenticated()  // 임시로 결재시에만 JWT 인증 필요
				.anyRequest().permitAll()  // 나머지 경로는 인증 없이 허용
			)
		 .addFilterBefore(jwtFilter(), UsernamePasswordAuthenticationFilter.class);
		return http.build();
	}
}
