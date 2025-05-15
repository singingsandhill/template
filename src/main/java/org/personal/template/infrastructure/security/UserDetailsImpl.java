package org.personal.template.infrastructure.security;

import java.util.Collection;
import java.util.Collections;

import org.personal.template.domain.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Getter;

@Getter
public class UserDetailsImpl implements UserDetails {

	private final User user;

	public UserDetailsImpl(User user) {
		this.user = user;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Collections.singletonList(new SimpleGrantedAuthority(user.getRole().name()));
	}

	@Override
	public String getPassword() {
		return null;
	}

	@Override
	public String getUsername() {
		return user.getEmail();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true; // 계정 만료 기능을 사용하지 않음
	}

	@Override
	public boolean isAccountNonLocked() {
		return true; // 계정 잠금 기능을 사용하지 않음
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true; // 비밀번호 만료 기능을 사용하지 않음
	}

	@Override
	public boolean isEnabled() {
		return true; // 계정 비활성화 기능을 사용하지 않음
	}

}
