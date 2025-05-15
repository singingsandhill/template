package org.personal.template.presentation.controller;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.personal.template.domain.entity.User;
import org.personal.template.domain.repository.UserRepository;
import org.personal.template.infrastructure.jwt.JWTUtil;
import org.personal.template.infrastructure.security.Role;
import org.personal.template.infrastructure.security.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class AdminControllerAPITest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private UserRepository userRepository;

	@MockitoBean
	private JWTUtil jwtUtil;

	@MockitoBean
	private UserDetailsServiceImpl userDetailsService;

	private List<User> testUsers;

	@BeforeEach
	void setUp() {
		// 테스트 사용자 데이터 설정
		User user1 = new User();
		user1.setUuid(1L);
		user1.setEmail("user1@example.com");
		user1.setUsername("user1");
		user1.setPassword("password1");
		user1.setRole(Role.ROLE_USER);

		User user2 = new User();
		user2.setUuid(2L);
		user2.setEmail("user2@example.com");
		user2.setUsername("user2");
		user2.setPassword("password2");
		user2.setRole(Role.ROLE_USER);

		User adminUser = new User();
		adminUser.setUuid(3L);
		adminUser.setEmail("admin@example.com");
		adminUser.setUsername("admin");
		adminUser.setPassword("adminPassword");
		adminUser.setRole(Role.ROLE_ADMIN);

		testUsers = Arrays.asList(user1, user2, adminUser);

		// Mock 응답 설정
		when(userRepository.findAll()).thenReturn(testUsers);
		when(userRepository.count()).thenReturn(3L);
	}

	@Test
	@DisplayName("관리자 사용자: 모든 사용자 조회 성공")
	@WithMockUser(username = "admin", roles = {"ADMIN"})
	void adminCanGetAllUsers() throws Exception {
		mockMvc.perform(get("/api/admin/users")
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.code").value(200))
			.andExpect(jsonPath("$.message").value("전체 사용자 목록"))
			.andExpect(jsonPath("$.data").isArray())
			.andExpect(jsonPath("$.data", hasSize(3)))
			.andExpect(jsonPath("$.data[0].username").value("user1"))
			.andExpect(jsonPath("$.data[1].username").value("user2"))
			.andExpect(jsonPath("$.data[2].username").value("admin"));

		verify(userRepository, times(1)).findAll();
	}

	@Test
	@DisplayName("관리자 사용자: 사용자 수 조회 성공")
	@WithMockUser(username = "admin", roles = {"ADMIN"})
	void adminCanGetUserCount() throws Exception {
		// 타임스탬프를 포함한 결과 데이터 준비
		Map<String, Object> resultData = new HashMap<>();
		resultData.put("totalUsers", 3L);
		resultData.put("timestamp", System.currentTimeMillis());

		mockMvc.perform(get("/api/admin/users/count")
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.code").value(200))
			.andExpect(jsonPath("$.message").value(containsString("사용자 수")))
			.andExpect(jsonPath("$.data.totalUsers").value(3))
			.andExpect(jsonPath("$.data.timestamp").exists());

		verify(userRepository, times(1)).count();
	}

	@Test
	@DisplayName("일반 사용자: 관리자 API 접근 실패")
	@WithMockUser(username = "user1", roles = {"USER"})
	void regularUserCannotAccessAdminApi() throws Exception {
		mockMvc.perform(get("/api/admin/users")
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isForbidden());

		verify(userRepository, never()).findAll();
	}

	@Test
	@DisplayName("인증되지 않은 사용자: 관리자 API 접근 실패")
	void unauthenticatedUserCannotAccessAdminApi() throws Exception {
		mockMvc.perform(get("/api/admin/users")
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isUnauthorized());

		verify(userRepository, never()).findAll();
	}
}