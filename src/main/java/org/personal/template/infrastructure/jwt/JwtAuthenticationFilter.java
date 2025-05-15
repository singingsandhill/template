package org.personal.template.infrastructure.jwt;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.personal.template.infrastructure.response.ApiResponseData;
import org.personal.template.infrastructure.security.Role;
import org.personal.template.infrastructure.security.UserDetailsImpl;
import org.personal.template.presentation.dto.LoginRequestDTO;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	private final JWTUtil jwtUtil;private final ObjectMapper objectMapper = new ObjectMapper();

	public JwtAuthenticationFilter(JWTUtil jwtUtil) {
		this.jwtUtil = jwtUtil;
		setFilterProcessesUrl("/api/user/login"); // 로그인 URL 설정
	}

	/**
	 * 로그인 필터
	 * @param request
	 * @param response
	 * @return
	 * @throws AuthenticationException
	 */
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws
		AuthenticationException {
		log.info("로그인 시도: Content-Type = {}", request.getContentType());
		try {
			LoginRequestDTO requestDto = new ObjectMapper().readValue(request.getInputStream(), LoginRequestDTO.class);

			log.info("로그인 요청 데이터: {}", requestDto);

			return getAuthenticationManager().authenticate(
				new UsernamePasswordAuthenticationToken(
					requestDto.getEmail(),
					requestDto.getPassword(),
					null
				)
			);
		} catch (IOException e) {
			log.error("요청 데이터 읽기 실패", e);
			throw new RuntimeException(e.getMessage());
		}
	}

	/**
	 * 로그인 성공시
	 * @param request
	 * @param response
	 * @param chain
	 * @param authResult
	 * @throws IOException
	 * @throws ServletException
	 */
	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
		Authentication authResult) throws IOException, ServletException {
		log.info("로그인 성공 및 JWT 생성");
		UserDetailsImpl userDetails = (UserDetailsImpl) authResult.getPrincipal();

		// 사용자 정보 추출
		String email = userDetails.getUsername();
		String username = userDetails.getUser().getUsername();
		Role role = userDetails.getUser().getRole();

		String token = jwtUtil.createAccessToken(email, username, role);
		response.addHeader(JWTUtil.AUTHORIZATION_HEADER, token);

		Map<String, Object> userMap = new HashMap<>();
		userMap.put("email", email);
		userMap.put("username", username);
		userMap.put("role", role.name());
		userMap.put("token", token.replace(JWTUtil.BEARER_PREFIX, ""));

		ApiResponseData<Map<String, Object>> responseData = ApiResponseData.success(userMap, "로그인 성공");

		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(objectMapper.writeValueAsString(responseData));
	}

	/**
	 * 로그인 실패시
	 * @param request
	 * @param response
	 * @param failed
	 * @throws IOException
	 * @throws ServletException
	 */
	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
		AuthenticationException failed) throws IOException, ServletException {
		log.info("로그인 실패");

		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");

		ApiResponseData<String> responseData = ApiResponseData.failure(401, "로그인 실패: " + failed.getMessage());
		response.getWriter().write(objectMapper.writeValueAsString(responseData));
	}
}
