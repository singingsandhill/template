package org.personal.template.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(exclude = "password") // 로그에 비밀번호가 출력되지 않도록 제외
public class LoginRequestDTO {
	@NotBlank(message = "이메일은 필수 입력 항목입니다")
	@Email(message = "유효한 이메일 형식이 아닙니다")
	@Schema(example = "test@test")
	private String email;

	@NotBlank(message = "비밀번호는 필수 입력 항목입니다")
	@Schema(example = "testtest")
	private String password;
}
