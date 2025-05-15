package org.personal.template.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterAdminRequestDTO {
	@NotBlank(message = "이메일은 필수 입력 항목입니다")
	@Email(message = "유효한 이메일 형식이 아닙니다")
	@Schema(example = "admin@test")
	private String email;

	@NotBlank(message = "비밀번호는 필수 입력 항목입니다")
	@Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다")
	@Schema(example = "testtest")
	private String password;

	@NotBlank(message = "사용자 이름은 필수 입력 항목입니다")
	@Size(min = 2, max = 50, message = "사용자 이름은 2자에서 50자 사이여야 합니다")
	@Schema(example = "admintest")
	private String username;

	@NotBlank(message = "확인 코드는 필수 입력 항목입니다")
	private String admincode;
}
