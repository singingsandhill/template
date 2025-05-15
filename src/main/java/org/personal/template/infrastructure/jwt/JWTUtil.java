package org.personal.template.infrastructure.jwt;

import java.security.Key;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.personal.template.infrastructure.exception.BaseException;
import org.personal.template.infrastructure.response.Code;
import org.personal.template.infrastructure.security.Role;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JWTUtil {

	// Header Key
	public static final String AUTHORIZATION_HEADER = "Authorization";
	// admin key
	public static final String AUTHORIZATION_KEY = "auth";
	// token prefix
	public static final String BEARER_PREFIX = "Bearer ";
	// username key
	public static final String USERNAME_KEY = "username";
	// email key
	public static final String EMAIL_KEY = "email";
	// token exp
	private final long TOKEN_TIME = 60 * 60 * 1000L; // 60min

	@Value("${jwt.secret.key}")
	private String secretKey;

	private Key key;

	private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

	@PostConstruct
	public void init() {
		byte[] bytes = Base64.getDecoder().decode(secretKey);
		key = Keys.hmacShaKeyFor(bytes);
	}

	public String createAccessToken(String email, String username, Role role) {
		Date now = new Date();
		Date expiration = new Date(now.getTime() + TOKEN_TIME);

		Map<String, Object> claims = new HashMap<>();
		claims.put(USERNAME_KEY, username);
		claims.put(EMAIL_KEY, email);
		claims.put(AUTHORIZATION_KEY, role.name());

		return BEARER_PREFIX + Jwts.builder()
			.setClaims(claims)                // 커스텀 클레임 설정
			.setSubject(email)                // 토큰 제목 (subject)
			.setIssuedAt(now)                 // 토큰 발행 시간
			.setExpiration(expiration)        // 토큰 만료 시간
			.signWith(key, signatureAlgorithm)
			.compact();
	}

	// HTTP 헤더에서 토큰을 추출
	public List<String> getHeaderToken(ServerHttpRequest request, String headerName) {
		return request.getHeaders().getOrDefault(headerName, Collections.emptyList());
	}

	// 토큰의 유효성을 확인
	public boolean validateToken(String token) {
		try {
			Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
			return true;
		} catch (SecurityException | MalformedJwtException e) {
			throw new BaseException(Code.AUTH001, "유효하지 않은 JWT 서명");
		} catch (ExpiredJwtException e) {
			throw new BaseException(Code.AUTH002, "만료된 JWT 토큰");
		} catch (UnsupportedJwtException e) {
			throw new BaseException(Code.AUTH003, "지원하지 않는 JWT 토큰");
		} catch (IllegalArgumentException e) {
			throw new BaseException(Code.AUTH004, "JWT 토큰이 잘못되었습니다");
		}
	}

	/**
	 * 토큰에서 이메일 추출
	 */
	public String getEmail(String token) {
		Claims claims = getClaims(token);
		return claims.get(EMAIL_KEY, String.class);
	}

	/**
	 * 토큰에서 사용자명 추출
	 */
	public String getUsername(String token) {
		Claims claims = getClaims(token);
		return claims.get(USERNAME_KEY, String.class);
	}

	/**
	 * 토큰에서 권한 정보 추출
	 */
	public String getRole(String token) {
		Claims claims = getClaims(token);
		return claims.get(AUTHORIZATION_KEY, String.class);
	}

	/**
	 * 토큰에서 모든 클레임 추출
	 */
	public Claims getClaims(String token) {
		return Jwts.parserBuilder()
			.setSigningKey(key)
			.build()
			.parseClaimsJws(token)
			.getBody();
	}

	public Boolean isExpired(String token) {
		try {
			return getClaims(token).getExpiration().before(new Date());
		} catch (ExpiredJwtException e) {
			return true;
		}
	}

	/**
	 * JWT 토큰 prefix 제거
	 */
	public String substringToken(String token) {
		if (StringUtils.hasText(token) && token.startsWith(BEARER_PREFIX)) {
			return token.substring(7);
		}
		throw new BaseException(Code.AUTH005, "JWT 토큰 형식이 올바르지 않습니다");
	}

	/**
	 * Header에사 JWT 가져오기
	 */
	public String getTokenFromRequest(HttpServletRequest request) {
		String header = request.getHeader(AUTHORIZATION_HEADER);
		if (StringUtils.hasText(header) && header.startsWith(BEARER_PREFIX)) {
			String token = header.substring(7);
			if (!StringUtils.hasText(token)) {
				throw new BaseException(Code.AUTH005, "JWT 토큰이 비어있습니다");
			}
			return token;
		}
		return null;
	}
}
