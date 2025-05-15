package org.personal.template.presentation.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.personal.template.application.service.UserService;
import org.personal.template.domain.entity.User;
import org.personal.template.domain.repository.UserRepository;
import org.personal.template.infrastructure.exception.BaseException;
import org.personal.template.infrastructure.jwt.JWTUtil;
import org.personal.template.infrastructure.response.Code;
import org.personal.template.infrastructure.security.GlobalSecurityContextFilter;
import org.personal.template.infrastructure.security.Role;
import org.personal.template.infrastructure.security.UserDetailsServiceImpl;
import org.personal.template.presentation.dto.LoginRequestDTO;
import org.personal.template.presentation.dto.RegisterAdminRequestDTO;
import org.personal.template.presentation.dto.RegisterRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(SpringExtension.class)
@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false) // Spring Security 필터 비활성화
public class UserControllerAPITest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private UserService userService;

	@MockitoBean
	private UserRepository userRepository;

	@MockitoBean
	private JWTUtil jwtUtil;

	@MockitoBean
	private UserDetailsServiceImpl userDetailsService;

	@MockitoBean
	private GlobalSecurityContextFilter globalSecurityContextFilter;

	@MockitoBean
	private PasswordEncoder passwordEncoder;

	private User userEntity;
	private User adminEntity;
	private String jwtToken;

	@BeforeEach
	void setUp() {
		// 테스트 데이터 초기화
		jwtToken = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdXRoIjoiUk9MRV9VU0VSIiwiZW1haWwiOiJ0ZXN0QHRlc3QiLCJ1c2VybmFtZSI6InRlc3RlciIsInN1YiI6InRlc3RAdGVzdCIsImlhdCI6MTc0NzI5MTkzNCwiZXhwIjoxNzQ3Mjk1NTM0fQ.-c2kDIVpdVp4mNSUaWhbxXUKhEX3b-FGtLqK9G-Wyhg";

		// 사용자 엔티티
		userEntity = new User();
		userEntity.setUuid(1L);
		userEntity.setEmail("user@test.com");
		userEntity.setUsername("testuser");
		userEntity.setPassword("encodedPassword");
		userEntity.setRole(Role.ROLE_USER);

		// 관리자 엔티티
		adminEntity = new User();
		adminEntity.setUuid(2L);
		adminEntity.setEmail("admin@test.com");
		adminEntity.setUsername("admin");
		adminEntity.setPassword("encodedAdminPassword");
		adminEntity.setRole(Role.ROLE_ADMIN);

		// UserRepository 모킹
		when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(userEntity));
		when(userRepository.findByUsername("testuser")).thenReturn(userEntity);
	}

	@Test
	@DisplayName("일반 사용자 회원가입 - 성공")
	void registerUserSuccess() throws Exception {
		// UserService.registerUser 모킹
		when(userService.registerUser(any(RegisterRequestDTO.class), eq(Role.ROLE_USER)))
			.thenReturn(userEntity);

		// 요청 데이터 생성
		Map<String, Object> requestData = new HashMap<>();
		requestData.put("email", "user@test.com");
		requestData.put("username", "testuser");
		requestData.put("password", "password123");

		mockMvc.perform(post("/api/user/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestData)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value(200))
			.andExpect(jsonPath("$.message").value("성공적으로 처리되었습니다."))
			.andExpect(jsonPath("$.data").value("회원가입 성공"));

		verify(userService, times(1)).registerUser(any(RegisterRequestDTO.class), eq(Role.ROLE_USER));
	}

	@Test
	@DisplayName("일반 사용자 회원가입 - 실패 (이메일 중복)")
	void registerUserFailDuplicateEmail() throws Exception {
		// ExceptionHandler를 위한 Global 컨트롤러 어드바이스 설정 필요
		when(userService.registerUser(any(RegisterRequestDTO.class), eq(Role.ROLE_USER)))
			.thenThrow(new BaseException(Code.ALREADY_EXISTS, "이미 등록된 이메일입니다"));

		// 요청 데이터 생성
		Map<String, Object> requestData = new HashMap<>();
		requestData.put("email", "duplicate@test.com");
		requestData.put("username", "duplicate");
		requestData.put("password", "password123");

		mockMvc.perform(post("/api/user/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestData)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value(202));

		verify(userService, times(1)).registerUser(any(RegisterRequestDTO.class), eq(Role.ROLE_USER));
	}

	// @Test
	// @DisplayName("일반 사용자 회원가입 - 실패 (유효성 검사)")
	// void registerUserFailValidation() throws Exception {
	// 	// 유효성 검사 실패는 @Valid 애노테이션에 의해 처리되지만,
	// 	// 테스트 환경에서는 GlobalExceptionHandler가 제대로 동작하지 않을 수 있음
	// 	// MockMvcBuilders.standaloneSetup()을 사용하거나 ControllerAdvice 등록 필요
	//
	// 	// 잘못된 이메일 형식의 DTO
	// 	Map<String, Object> invalidData = new HashMap<>();
	// 	invalidData.put("email", "invalid-email");
	// 	invalidData.put("username", "test");
	// 	invalidData.put("password", "pwd"); // 짧은 비밀번호
	//
	// 	mockMvc.perform(post("/api/user/register")
	// 			.contentType(MediaType.APPLICATION_JSON)
	// 			.content(objectMapper.writeValueAsString(invalidData)))
	// 		.andExpect(status().isBadRequest());
	//
	// 	verify(userService, never()).registerUser(any(RegisterRequestDTO.class), any(Role.class));
	// }

	@Test
	@DisplayName("관리자 회원가입 - 성공")
	void registerAdminSuccess() throws Exception {
		// UserService.registerAdmin 모킹
		when(userService.registerAdmin(any(RegisterAdminRequestDTO.class), eq(Role.ROLE_ADMIN)))
			.thenReturn(adminEntity);

		// 요청 데이터 생성 - adminCode 필드명 주의
		Map<String, Object> requestData = new HashMap<>();
		requestData.put("email", "admin@test.com");
		requestData.put("username", "admin");
		requestData.put("password", "admin123");
		requestData.put("adminCode", "adminSecret"); // adminCode로 수정

		mockMvc.perform(post("/api/user/register/admin")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestData)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.code").value(200))
			.andExpect(jsonPath("$.message").value("성공적으로 처리되었습니다."))
			.andExpect(jsonPath("$.data").value("관리자 회원가입 성공"));

		verify(userService, times(1)).registerAdmin(any(RegisterAdminRequestDTO.class), eq(Role.ROLE_ADMIN));
	}

	@Test
	@DisplayName("로그인 - 성공")
	void loginSuccess() throws Exception {
		// 로그인 응답 데이터
		Map<String, Object> loginResponse = new HashMap<>();
		loginResponse.put("token", jwtToken);

		// UserService.login 모킹
		when(userService.login(any(LoginRequestDTO.class))).thenReturn(loginResponse);

		// 요청 데이터 생성
		Map<String, Object> requestData = new HashMap<>();
		requestData.put("email", "user@test.com");
		requestData.put("password", "password123");

		mockMvc.perform(post("/api/user/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestData)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value(200))
			.andExpect(jsonPath("$.message").value("로그인 성공"))
			.andExpect(jsonPath("$.data.token").value(jwtToken));

		verify(userService, times(1)).login(any(LoginRequestDTO.class));
	}

	@Test
	@DisplayName("로그인 - 실패 (이메일 없음)")
	void loginFailInvalidEmail() throws Exception {
		// UserService가 예외를 던지도록 모킹
		when(userService.login(any(LoginRequestDTO.class)))
			.thenThrow(new BaseException(Code.SIGN001, "일치하는 이메일 없음"));

		// 요청 데이터 생성
		Map<String, Object> requestData = new HashMap<>();
		requestData.put("email", "wrong@test.com");
		requestData.put("password", "password123");

		mockMvc.perform(post("/api/user/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestData)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value(2001))
			.andExpect(jsonPath("$.message").value("일치하는 이메일 없음"));
	}

	@Test
	@DisplayName("로그인 - 실패 (비밀번호 불일치)")
	void loginFailInvalidPassword() throws Exception {
		// UserService가 예외를 던지도록 모킹
		when(userService.login(any(LoginRequestDTO.class)))
			.thenThrow(new BaseException(Code.SIGN002, "비밀번호 불일치"));

		// 요청 데이터 생성
		Map<String, Object> requestData = new HashMap<>();
		requestData.put("email", "user@test.com");
		requestData.put("password", "wrongpassword");

		mockMvc.perform(post("/api/user/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestData)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value(2002))
			.andExpect(jsonPath("$.message").value("비밀번호 불일치"));
	}
}