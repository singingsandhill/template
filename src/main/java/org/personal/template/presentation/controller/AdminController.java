package org.personal.template.presentation.controller;

import java.util.HashMap;
import java.util.Map;

import org.personal.template.domain.repository.UserRepository;
import org.personal.template.infrastructure.response.ApiResponseData;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "Admin Management", description = "관리자 전용 API")
@SecurityRequirement(name = "JWT")
public class AdminController {

	private final UserRepository userRepository;

	@GetMapping(path = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
	@Secured("ROLE_ADMIN")
	@Operation(summary = "모든 사용자 조회", description = "등록된 모든 사용자 목록 조회")
	public ResponseEntity<?> getAllUsers() {
		return ResponseEntity.ok()
			.contentType(MediaType.APPLICATION_JSON)
			.body(ApiResponseData.success(
				userRepository.findAll(),
				"전체 사용자 목록"
			));
	}

	@GetMapping(path = "/users/count", produces = MediaType.APPLICATION_JSON_VALUE)
	@Secured("ROLE_ADMIN")
	@Operation(
		summary = "사용자 수 조회",
		description = "등록된 총 사용자 수 조회 (관리자 전용)",
		security = @SecurityRequirement(name = "JWT")
	)
	public ResponseEntity<?> getUserCount() {
		long count = userRepository.count();

		Map<String, Object> result = new HashMap<>();
		result.put("totalUsers", count);
		result.put("timestamp", System.currentTimeMillis());

		return ResponseEntity.ok()
			.contentType(MediaType.APPLICATION_JSON)
			.body(ApiResponseData.success( result,
				"전체 사용자 수"
			));
	}

}
