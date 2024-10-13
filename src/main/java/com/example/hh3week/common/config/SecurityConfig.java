// package com.example.hh3week.common.config;
//
// import com.example.hh3week.application.service.CustomUserDetailsService;
// import com.example.hh3week.common.util.JwtUtil;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.security.authentication.AuthenticationManager;
// import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
// import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
// import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// import org.springframework.security.config.http.SessionCreationPolicy;
// import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
// import org.springframework.security.web.SecurityFilterChain;
// import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
// import org.springframework.security.authentication.AuthenticationProvider;
// import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// import org.springframework.security.crypto.password.PasswordEncoder;
//
// @Configuration
// @EnableGlobalMethodSecurity(prePostEnabled = true)
// public class SecurityConfig {
//
// 	@Autowired
// 	private CustomUserDetailsService userDetailsService;
//
// 	@Autowired
// 	private JwtRequestFilter jwtRequestFilter;
//
// 	@Bean
// 	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
// 		http.csrf().disable()
// 			// 인증이 필요 없는 엔드포인트 설정
// 			.authorizeRequests()
// 			.antMatchers("/authenticate", "/register").permitAll()
// 			.anyRequest().authenticated()
// 			.and()
// 			// 세션을 사용하지 않고, STATELESS로 설정
// 			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
//
// 		// JWT 필터를 UsernamePasswordAuthenticationFilter 전에 추가
// 		http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
//
// 		return http.build();
// 	}
//
// 	// AuthenticationManager 빈 등록
// 	@Bean
// 	public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
// 		return authConfig.getAuthenticationManager();
// 	}
//
// 	// 패스워드 인코더 빈 등록
// 	@Bean
// 	public PasswordEncoder passwordEncoder() {
// 		return new BCryptPasswordEncoder();
// 	}
//
// 	// AuthenticationProvider 설정
// 	@Bean
// 	public AuthenticationProvider authenticationProvider() {
// 		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
//
// 		authProvider.setUserDetailsService(userDetailsService);
// 		authProvider.setPasswordEncoder(passwordEncoder());
//
// 		return authProvider;
// 	}
// }
