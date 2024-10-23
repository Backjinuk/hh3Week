package com.example.hh3week.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

public class TestSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())  // CSRF 보호 비활성화
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()  // 모든 요청을 허용하여 Security 비활성화
            );
        return http.build();
    }
}
