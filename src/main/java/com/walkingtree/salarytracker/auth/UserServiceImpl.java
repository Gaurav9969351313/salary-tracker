package com.walkingtree.salarytracker.auth;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository repo;

	@Autowired
	private PasswordEncoder encoder;

	@Override
	public User saveUser(User user) {
		user.setPassword(encoder.encode(user.getPassword()));
		return repo.save(user);
	}

	@Override
	public List<User> getAllUsers() {
		return repo.findAll();
	}

	@Override
	public User updateUser(long id, User user) {
		User user2 = findById(user.getId()).get();
		if (user2 != null) {
			user2.setEmail(user.getEmail());
			user2.setFirstName(user.getFirstName());
			user2.setLastName(user.getLastName());
			user2.setPhNumber(user.getPhNumber());
			user2.setRole(user.getRole());

		}
		return repo.save(user2);
	}

	@Override
	public Optional<User> findById(long id) {
		return repo.findById(id);
	}

	@Override
	public User findByEmail(String email) {
		return repo.findByEmail(email);
	}

	@Override
	public User updateUser(User user) {
		User user2 = findById(user.getId()).get();
		if (user2 != null) {
			user2.setEmail(user.getEmail());
			user2.setFirstName(user.getFirstName());
			user2.setLastName(user.getLastName());
			user2.setPassword(user.getPassword());

		}
		return repo.save(user);

	}

	@Override
	public void deleteUser(long id) {
		User user = findById(id).get();
		if (user != null) {
			repo.delete(user);
		}

	}
}
