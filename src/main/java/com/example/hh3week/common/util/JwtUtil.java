package com.example.hh3week.common.util;

import io.jsonwebtoken.*;

// import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import java.util.Date;
import java.util.function.Function;

import com.example.hh3week.adapter.in.dto.user.UserDto;

@Component
public class JwtUtil {

	private final String SECRET_KEY = "your_secret_key"; // 안전하게 관리 필요
	private final long JWT_TOKEN_VALIDITY = 5 * 60 * 60 * 1000; // 5시간

	// 토큰에서 사용자 이름 추출
	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	// 토큰에서 만료 시간 추출
	public Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}

	// 특정 클레임 추출
	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}

	// 토큰에서 모든 클레임 추출
	private Claims extractAllClaims(String token) {
		return Jwts.parser()
			.setSigningKey(SECRET_KEY)
			.parseClaimsJws(token)
			.getBody();
	}

	// 토큰이 만료되었는지 확인
	private Boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}

	// 사용자 이름과 비밀번호로 토큰 생성
	public String generateToken(UserDto userDetails) {
		return createToken(userDetails.getUserName());
	}

	private String createToken(String subject) {

		return Jwts.builder()
			.setSubject(subject)
			.setIssuedAt(new Date(System.currentTimeMillis()))
			.setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY))
			.signWith(SignatureAlgorithm.HS256, SECRET_KEY)
			.compact();
	}

	// 토큰 유효성 검사
	// public Boolean validateToken(String token, UserDetails userDetails) {
	// 	final String username = extractUsername(token);
	// 	return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
	// }
}
