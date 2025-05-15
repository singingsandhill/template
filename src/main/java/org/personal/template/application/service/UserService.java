package org.personal.template.application.service;

import java.util.HashMap;
import java.util.Map;

import org.personal.template.domain.entity.User;
import org.personal.template.domain.repository.UserRepository;
import org.personal.template.infrastructure.exception.BaseException;
import org.personal.template.infrastructure.jwt.JWTUtil;
import org.personal.template.infrastructure.response.Code;
import org.personal.template.infrastructure.security.Role;
import org.personal.template.presentation.dto.LoginRequestDTO;
import org.personal.template.presentation.dto.RegisterAdminRequestDTO;
import org.personal.template.presentation.dto.RegisterRequestDTO;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JWTUtil jwtUtil;


	public Map<String, Object> login(LoginRequestDTO loginRequestDTO) {
		log.info("로그인 시도: {}", loginRequestDTO.getEmail());

		// 이메일로 사용자 조회
		User user = userRepository.findByEmail(loginRequestDTO.getEmail())
			.orElseThrow(() -> new BaseException(Code.SIGN001, "일치하는 이메일 없음"));

		// 비밀번호 검증
		if (!passwordEncoder.matches(loginRequestDTO.getPassword(), user.getPassword())) {
			throw new BaseException(Code.SIGN002, "비밀번호 불일치");
		}

		// JWT 토큰 생성
		String token = jwtUtil.createAccessToken(user.getEmail(), user.getUsername(), user.getRole());

		// 응답 데이터 구성
		Map<String, Object> responseData = new HashMap<>();
		// responseData.put("email", user.getEmail());
		// responseData.put("username", user.getUsername());
		// responseData.put("role", user.getRole().name());
		responseData.put("token", token);

		return responseData;
	}

	public User registerUser(RegisterRequestDTO requestDTO, Role role) {
		// 이메일 중복 검사
		if (userRepository.findByEmail(requestDTO.getEmail()).isPresent()) {
			throw new BaseException(Code.ALREADY_EXISTS, "이미 등록된 이메일입니다");
		}

		// 사용자 이름 중복 검사
		if (userRepository.findByUsername(requestDTO.getUsername()) != null) {
			throw new BaseException(Code.ALREADY_EXISTS, "이미 등록된 사용자 이름입니다");
		}

		// 새 사용자 생성
		User user = new User();
		user.setEmail(requestDTO.getEmail());
		user.setUsername(requestDTO.getUsername());
		user.setPassword(passwordEncoder.encode(requestDTO.getPassword()));
		user.setRole(role);

		return userRepository.save(user);
	}

	public User registerAdmin(RegisterAdminRequestDTO requestDTO, Role role) {
		// 관리자 코드 확인
		if(!("admincode".equals(requestDTO.getAdmincode()))){
			throw new BaseException(Code.SIGN006, "관리자 코드 불일치");
		}

		// 이메일 중복 검사
		if (userRepository.findByEmail(requestDTO.getEmail()).isPresent()) {
			throw new BaseException(Code.ALREADY_EXISTS, "이미 등록된 이메일입니다");
		}

		// 사용자 이름 중복 검사
		if (userRepository.findByUsername(requestDTO.getUsername()) != null) {
			throw new BaseException(Code.ALREADY_EXISTS, "이미 등록된 사용자 이름입니다");
		}

		// 새 사용자 생성
		User user = new User();
		user.setEmail(requestDTO.getEmail());
		user.setUsername(requestDTO.getUsername());
		user.setPassword(passwordEncoder.encode(requestDTO.getPassword()));
		user.setRole(role);

		return userRepository.save(user);
	}

}
