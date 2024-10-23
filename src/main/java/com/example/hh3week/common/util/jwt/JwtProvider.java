package com.example.hh3week.common.util.jwt;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

@Component
public class JwtProvider {

	@Value("${jwt.secret}")
	private String jwtSecret;

	@Value("${jwt.expiration}")
	private long jwtExpirationInMs;


	/**
	 * JWT 토큰 생성 메서드
	 *
	 * @param userId        사용자 고유 ID
	 * @param queueOrder    대기열 순서
	 * @param remainingTime 남은 대기 시간
	 * @param seatDetailId
	 * @return 생성된 JWT 토큰
	 */
	public String generateToken(long userId, long queueOrder, long remainingTime, long seatDetailId) {
		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

		return Jwts.builder()
			.setSubject(Long.toString(userId))
			.claim("queueOrder", queueOrder)
			.claim("remainingTime", remainingTime)
			.claim("seatDetailId", seatDetailId)
			.setIssuedAt(now)
			.setExpiration(expiryDate)
			.signWith(SignatureAlgorithm.HS512, jwtSecret)
			.compact();
	}

	/**
	 * JWT 토큰에서 사용자 ID 추출
	 *
	 * @param token JWT 토큰
	 * @return 사용자 ID
	 */
	public Long getUserIdFromJWT(String token) {
		Claims claims = Jwts.parser()
			.setSigningKey(jwtSecret)
			.parseClaimsJws(token)
			.getBody();

		return Long.parseLong(claims.getSubject());
	}

	/**
	 * JWT 토큰에서 모든값 추출
	 *
	 * @param token
	 * @return map
	 * */
	public Map<String, Object> getMapFromJWT(String token){
		Claims claims = Jwts.parser()
			.setSigningKey(jwtSecret)
			.parseClaimsJws(token)
			.getBody();

		return Map.of(
			"userId", Long.parseLong(claims.getSubject()),
			"queueOrder", claims.get("queueOrder"),
			"seatDetailId", claims.get("seatDetailId"),
			"remainingTime", claims.get("remainingTime")
		);
	}

	/**
	 * JWT 토큰 유효성 검증 메서드
	 *
	 * @param token JWT 토큰
	 * @return 유효한 토큰이면 true, 아니면 false
	 */
	public boolean validateToken(String token) {
		try {
			Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
			return true;
		} catch (SignatureException | MalformedJwtException | ExpiredJwtException | UnsupportedJwtException | IllegalArgumentException ex) {
			// 로그 추가 가능
			return false;
		}
	}

	/**
	 * JWT 토큰 만료 여부 확인
	 *
	 * @param token JWT 토큰
	 * @return 만료되었으면 true, 아니면 false
	 */
	public boolean isTokenExpired(String token) {
		try {
			Claims claims = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();
			return claims.getExpiration().before(new Date());
		} catch (JwtException ex) {
			// 예외 처리 가능
			return true;
		}
	}
}
