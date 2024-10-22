package com.example.hh3week.common.util;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtFilter extends OncePerRequestFilter {

	private final JwtProvider jwtProvider;

	public JwtFilter(JwtProvider jwtProvider) {
		this.jwtProvider = jwtProvider;
	}


	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
		throws ServletException, IOException, IOException {

		String authorizationHeader = request.getHeader("Authorization");

		// JWT 토큰이 있는지 확인
		if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
			String token = authorizationHeader.substring(7);
			if (jwtProvider.validateToken(token)) {
				// JWT 유효성을 검사한 후, 토큰에서 사용자 정보 설정
				Long userId = jwtProvider.getUserIdFromJWT(token);
				SecurityContextHolder.getContext().setAuthentication(
					new UsernamePasswordAuthenticationToken(userId, null, null)
				);
			} else {
				// 유효하지 않은 토큰일 경우 401 에러 반환
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				response.getWriter().write("Invalid JWT Token");
				return;
			}
		} else {
			// Authorization 헤더가 없는 경우 또는 형식이 잘못된 경우
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.getWriter().write("Missing or invalid Authorization header");
			return;
		}

		chain.doFilter(request, response);
	}
}
