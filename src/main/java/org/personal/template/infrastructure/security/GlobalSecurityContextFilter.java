package org.personal.template.infrastructure.security;

import java.io.IOException;
import java.util.Optional;

import org.personal.template.domain.entity.User;
import org.personal.template.domain.repository.UserRepository;
import org.personal.template.infrastructure.exception.BaseException;
import org.personal.template.infrastructure.jwt.JWTUtil;
import org.personal.template.infrastructure.response.ApiResponseData;
import org.personal.template.infrastructure.response.Code;
import org.personal.template.infrastructure.response.ErrorData;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "JWT 검증 및 인가")
@Component
@RequiredArgsConstructor
public class GlobalSecurityContextFilter extends OncePerRequestFilter {

	private final JWTUtil jwtUtil;
	private final UserRepository userRepository;
	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		try {
			if (shouldNotFilter(request)) {
				filterChain.doFilter(request, response);
				return;
			}

			// 1) JWT 토큰 우선 처리
			String tokenValue = jwtUtil.getTokenFromRequest(request);
			if (StringUtils.hasText(tokenValue)) {
				log.info("Extracted Token: {}", tokenValue);
				if (!jwtUtil.validateToken(tokenValue)) {
					throw new BaseException(Code.AUTH005, "Token validation failed");
				}

				// 클레임에서 이메일 추출 후 사용자 조회
				String email = jwtUtil.getEmail(tokenValue);
				if (email == null) {
					throw new BaseException(Code.AUTH004, "토큰에 이메일 정보가 없습니다");
				}

				// 사용자가 존재하는지 확인
				setAuthentication(email);
			}
			// 2) JWT 헤더가 없으면 레거시 헤더 인증 시도
			else {
				String username = request.getHeader("X-USER-NAME");
				String role = request.getHeader("X-USER-ROLE");
				String userId = request.getHeader("X-USER-ID");

				if (username != null && role != null && userId != null) {
					try {
						Long id = Long.parseLong(userId);
						User user = userRepository.findByUsername(username);
						if (user == null) {
							throw new BaseException(Code.SIGN001, "사용자를 찾을 수 없습니다");
						}

						UserDetailsImpl userDetails = new UserDetailsImpl(user);
						Authentication auth = new UsernamePasswordAuthenticationToken(
							userDetails, null, userDetails.getAuthorities());
						SecurityContextHolder.getContext().setAuthentication(auth);
					} catch (NumberFormatException e) {
						log.warn("Invalid X-USER-ID header: {}", userId);
					}
				}
			}

			filterChain.doFilter(request, response);
		} catch (BaseException e) {
			// JWT 검증 중 에러 발생 시 JSON 에러 응답
			response.setContentType("application/json;charset=UTF-8");
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

			ErrorData errorData = new ErrorData(
				e.getErrorCode().getCode(),
				e.getErrorCode().getMessage(),
				e.getMessage()
			);
			ApiResponseData<ErrorData> errorResponse =
				new ApiResponseData<>(403, "fail", errorData);

			String json = new ObjectMapper().writeValueAsString(errorResponse);
			response.getWriter().write(json);
			response.getWriter().flush();
		}
	}

	private void setAuthentication(String email) {
		try {
			// 1) DB에서 User 엔티티 조회
			Optional<User> userOpt = userRepository.findByEmail(email);

			if (userOpt.isEmpty()) {
				log.error("User not found for email: {}", email);
				throw new BaseException(Code.SIGN001, "해당 이메일의 사용자를 찾을 수 없습니다: " + email);
			}

			User user = userOpt.get();
			log.info("User found: {}", user.getUsername());

			// 2) User -> UserDetailsImpl 변환
			UserDetailsImpl userDetails = new UserDetailsImpl(user);

			// 3) Authentication 객체에 authorities로 getAuthorities() 사용
			Authentication auth = new UsernamePasswordAuthenticationToken(
				userDetails,
				null,
				userDetails.getAuthorities()
			);

			SecurityContextHolder.getContext().setAuthentication(auth);
		} catch (Exception e) {
			log.error("Error setting authentication: {}", e.getMessage(), e);
			throw e;
		}
	}

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {
		String path = request.getRequestURI();
		return path.startsWith("/swagger-ui") || path.startsWith("/v3/api-docs") ||
			path.startsWith("/api/user/login") ||
			path.startsWith("/api/user/register");
	}
}
