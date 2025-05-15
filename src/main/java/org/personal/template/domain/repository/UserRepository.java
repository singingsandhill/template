package org.personal.template.domain.repository;

import java.util.List;
import java.util.Optional;

import org.personal.template.domain.entity.User;

public interface UserRepository {
	User findByUsername(String username);

	Optional<User> findByEmail(String email);

	<S extends User> S save(S entity);

	<S extends User> List<S> saveAll(Iterable<S> entities);

	Optional<User> findById(Long id);

	boolean existsById(Long id);

	List<User> findAll();

	List<User> findAllById(Iterable<Long> ids);

	long count();

	void deleteById(Long id);

	void delete(User entity);

	void deleteAllById(Iterable<? extends Long> ids);

	void deleteAll(Iterable<? extends User> entities);

	void deleteAll();
}