package org.personal.template.domain.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.personal.template.domain.entity.User;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryUserRepository implements UserRepository {
	private final Map<Long, User> users = new ConcurrentHashMap<>();
	private final Map<String, User> userByEmail = new ConcurrentHashMap<>();
	private final Map<String, User> userByUsername = new ConcurrentHashMap<>();
	private final AtomicLong idGenerator = new AtomicLong(1L);

	@Override
	public User findByUsername(String username) {
		return userByUsername.get(username);
	}

	@Override
	public Optional<User> findByEmail(String email) {
		return Optional.ofNullable(userByEmail.get(email));
	}

	@Override
	public <S extends User> S save(S user) {
		if (user.getUuid() == null) {
			user.setUuid(idGenerator.getAndIncrement());
		}

		users.put(user.getUuid(), user);
		userByEmail.put(user.getEmail(), user);
		userByUsername.put(user.getUsername(), user);

		return user;
	}

	@Override
	public <S extends User> List<S> saveAll(Iterable<S> entities) {
		List<S> result = new ArrayList<>();
		for (S entity : entities) {
			result.add(save(entity));
		}
		return result;
	}

	@Override
	public Optional<User> findById(Long id) {
		return Optional.ofNullable(users.get(id));
	}

	@Override
	public boolean existsById(Long id) {
		return users.containsKey(id);
	}

	@Override
	public List<User> findAll() {
		return new ArrayList<>(users.values());
	}

	@Override
	public List<User> findAllById(Iterable<Long> ids) {
		List<User> result = new ArrayList<>();
		for (Long id : ids) {
			findById(id).ifPresent(result::add);
		}
		return result;
	}

	@Override
	public long count() {
		return users.size();
	}

	@Override
	public void deleteById(Long id) {
		User user = users.remove(id);
		if (user != null) {
			userByEmail.remove(user.getEmail());
			userByUsername.remove(user.getUsername());
		}
	}

	@Override
	public void delete(User entity) {
		deleteById(entity.getUuid());
	}

	@Override
	public void deleteAllById(Iterable<? extends Long> ids) {
		for (Long id : ids) {
			deleteById(id);
		}
	}

	@Override
	public void deleteAll(Iterable<? extends User> entities) {
		for (User entity : entities) {
			delete(entity);
		}
	}

	@Override
	public void deleteAll() {
		users.clear();
		userByEmail.clear();
		userByUsername.clear();
	}

}
