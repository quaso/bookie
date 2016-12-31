package org.bookie.service;

import java.util.List;

import org.bookie.exception.UserNotFoundException;
import org.bookie.model.User;
import org.bookie.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

	public User createUser(final User user) {
		// TODO: add more here :)
		this.userRepository.save(user);
		return user;
	}

	public User findById(final String id) {
		return this.userRepository.findOne(id);
	}

	public List<User> findByNameSurname(final String str) {
		return this.userRepository.findByNameContainingIgnoreCaseOrSurnameContainingIgnoreCase(str, str);
	}

	public void updateUser(final User user) throws UserNotFoundException {
		Assert.notNull(user, "User cannot be empty");
		Assert.notNull(user.getId(), "UserId cannot be empty");
		if (!this.userRepository.exists(user.getId())) {
			throw new UserNotFoundException(user.getId());
		}
		this.userRepository.save(user);
	}
}
