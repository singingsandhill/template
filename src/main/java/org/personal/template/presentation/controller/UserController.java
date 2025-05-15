package org.personal.template.presentation.controller;

import java.util.Map;

import org.personal.template.application.service.UserService;
import org.personal.template.domain.entity.User;
import org.personal.template.infrastructure.response.ApiResponseData;
import org.personal.template.infrastructure.security.Role;
import org.personal.template.presentation.dto.LoginRequestDTO;
import org.personal.template.presentation.dto.RegisterAdminRequestDTO;
import org.personal.template.presentation.dto.RegisterRequestDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "회원", description = "회원 가입 및 로그인")
public class UserController {

	private final UserService userService;

	@PostMapping("/login")
	@Operation(
		summary = "로그인",
		description = "JWT 토큰 기반",
		responses = {
			@ApiResponse(responseCode = "200", description = "로그인 성공",
				content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseData.class),
					examples = @ExampleObject(value = """
						{
							"code": 200,
							"message": "로그인 성공",
							"data": {
								"token": "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdXRoIjoiUk9MRV9VU0VSIiwiZW1haWwiOiJ0ZXN0QHRlc3QiLCJ1c2VybmFtZSI6InRlc3RlciIsInN1YiI6InRlc3RAdGVzdCIsImlhdCI6MTc0NzI5MTkzNCwiZXhwIjoxNzQ3Mjk1NTM0fQ.-c2kDIVpdVp4mNSUaWhbxXUKhEX3b-FGtLqK9G-Wyhg"
							}
						}
						"""))),
			@ApiResponse(responseCode = "401", description = "로그인 실패",
				content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseData.class),
					examples = @ExampleObject(value = """
						{
						     "code": 2001,
						     "message": "일치하는 이메일 없음",
						     "data": null
						   }
						""")))
		}
	)
	public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO requestDTO) {
		Map<String, Object> result = userService.login(requestDTO);
		return ResponseEntity.ok()
			.contentType(MediaType.APPLICATION_JSON)
			.body(ApiResponseData.success(result, "로그인 성공"));
	}

	@PostMapping("/register")
	@Operation(summary = "일반 회원 가입", description = "",
		responses = {
			@ApiResponse(responseCode = "200", description = "회원 가입 성공",
				content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseData.class),
					examples = @ExampleObject(value = """
						{
							"code": 200,
							"message": "성공적으로 처리되었습니다.",
						    "data": "회원가입 성공"
						}
						"""))),
			@ApiResponse(responseCode = "400", description = "회원 가입 실패",
				content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseData.class),
					examples = @ExampleObject(value = """
						{
						     "code": 202,
						     "message": "이미 존재하는 리소스입니다.",
						     "data": null
						   }
						""")))
		})
	public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequestDTO requestDTO) {
		userService.registerUser(requestDTO, Role.ROLE_USER);
		return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(ApiResponseData.success("회원가입 성공"));
	}

	@PostMapping("/register/admin")
	@Operation(
		summary = "관리자 회원가입",
		description = "",
		responses = {
			@ApiResponse(responseCode = "200", description = "관리자 가입 성공",
				content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseData.class),
					examples = @ExampleObject(value = """
						{
						 	"code": 200,
						 	"message": "성공적으로 처리되었습니다.",
						 	"data": "관리자 회원가입 성공"
						 	}
						"""))),
			@ApiResponse(responseCode = "400", description = "관리자 가입 실패",
				content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponseData.class),
					examples = @ExampleObject(value = """
						{
						     "code": 202,
						     "message": "이미 존재하는 리소스입니다.",
						     "data": null
						   }
						""")))
		}
	)
	public ResponseEntity<?> registerAdmin(@Valid @RequestBody RegisterAdminRequestDTO requestDTO) {
		User user = userService.registerAdmin(requestDTO, Role.ROLE_ADMIN);
		return ResponseEntity.status(HttpStatus.CREATED).contentType(MediaType.APPLICATION_JSON)
			.body(ApiResponseData.success("관리자 회원가입 성공"));
	}
}
