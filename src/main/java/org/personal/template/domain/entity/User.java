package org.personal.template.domain.entity;

import org.personal.template.infrastructure.security.Role;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class User {

	private Long uuid;

	private String email;

	private String password;

	private Role role;

	private String username;
}
