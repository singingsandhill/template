package org.personal.template.infrastructure.security;

import org.personal.template.infrastructure.jwt.JWTUtil;
import org.personal.template.infrastructure.jwt.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
@RequiredArgsConstructor
public class GlobalSecurityConfig {

	private final JWTUtil jwtUtil;
	private final UserDetailsServiceImpl userDetailsService;
	private final GlobalSecurityContextFilter globalSecurityContextFilter;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
		return configuration.getAuthenticationManager();
	}

	@Bean
	public JwtAuthenticationFilter jwtAuthenticationFilter() throws Exception {
		JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtUtil);
		filter.setAuthenticationManager(authenticationManager(null));
		filter.setFilterProcessesUrl("/api/login"); // 로그인 URL 설정
		return filter;
	}

	@Bean
	public SecurityFilterChain globalSecurityFilterChain(HttpSecurity http) throws Exception {
		http
			.csrf(csrf -> csrf.disable())
			.formLogin(formLogin -> formLogin.disable())  // 로그인 페이지 비활성화
			.httpBasic(httpBasic -> httpBasic.disable())  // HTTP 기본 인증 비활성화
			.logout(logout -> logout.disable())  // 로그아웃 기능 비활성화

			.anonymous(an -> an.disable())

			.exceptionHandling(exc -> exc
				// 인증 없을 땐 401
				.authenticationEntryPoint((req, res, ex) ->
					res.sendError(HttpServletResponse.SC_UNAUTHORIZED))
				// 권한 부족일 땐 403
				.accessDeniedHandler((req, res, ex) ->
					res.sendError(HttpServletResponse.SC_FORBIDDEN))
			)

			// URL 권한 설정
			.authorizeHttpRequests(auth -> auth
				// Swagger UI 접근 허용
				.requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**", "/docs/**").permitAll()
				// 로그인 및 회원가입 접근 허용
				.requestMatchers("/api/user/login", "/api/user/register", "/api/user/register/admin").permitAll()
				.requestMatchers("/api/admin/**").hasRole("ADMIN")
				.anyRequest().authenticated())

			.sessionManagement(session -> session
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

			.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
			.addFilterBefore(globalSecurityContextFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}
}
