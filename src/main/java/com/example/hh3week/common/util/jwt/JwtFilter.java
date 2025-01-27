//
// package com.example.hh3week.common.util.jwt;
//
// import java.io.IOException;
//
// import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
// import org.springframework.security.core.context.SecurityContextHolder;
// import org.springframework.web.filter.OncePerRequestFilter;
//
// import jakarta.servlet.FilterChain;
// import jakarta.servlet.ServletException;
// import jakarta.servlet.http.HttpServletRequest;
// import jakarta.servlet.http.HttpServletResponse;
//
// public class JwtFilter extends OncePerRequestFilter {
//
// 	private final JwtProvider jwtProvider;
//
// 	// 적용할 경로를 설정합니다.
// 	private static final String AUTHENTICATION_URL = "/api/v1/payment/registerPaymentHistory";
//
// 	public JwtFilter(JwtProvider jwtProvider) {
// 		this.jwtProvider = jwtProvider;
// 	}
//
// 	@Override
// 	protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
// 		// 필터를 적용하지 않을 경로를 설정합니다.
// 		// 여기서는 특정 경로가 아닐 때 필터를 적용하지 않도록 설정합니다.
// 		String path = request.getRequestURI();
// 		return !AUTHENTICATION_URL.equals(path);
// 	}
//
// 	@Override
// 	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
// 		throws ServletException, IOException {
// 		// 여기부터는 AUTHENTICATION_URL에 해당하는 요청에만 필터가 적용됩니다.
//
// 		String authorizationHeader = request.getHeader("Authorization");
//
// 		// JWT 토큰이 있는지 확인
// 		if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
// 			String token = authorizationHeader.substring(7);
// 			if (jwtProvider.validateToken(token)) {
// 				// JWT 유효성을 검사한 후, 토큰에서 사용자 정보 설정
// 				Long userId = jwtProvider.getUserIdFromJWT(token);
// 				SecurityContextHolder.getContext().setAuthentication(
// 					new UsernamePasswordAuthenticationToken(userId, null, null)
// 				);
// 			} else {
// 				// 유효하지 않은 토큰일 경우 401 에러 반환
// 				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
// 				response.getWriter().write("Invalid JWT Token");
// 				return;
// 			}
// 		} else {
// 			// Authorization 헤더가 없는 경우 또는 형식이 잘못된 경우
// 			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
// 			response.getWriter().write("Missing or invalid Authorization header");
// 			return;
// 		}
//
// 		chain.doFilter(request, response);
// 	}
// }
