package org.bookie.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.commons.lang3.RandomStringUtils;
import org.bookie.exception.UserNotFoundException;
import org.bookie.model.Role;
import org.bookie.model.User;
import org.bookie.repository.OrganizationUserRoleRepository;
import org.bookie.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
@Transactional
public class UserService {
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private OrganizationUserRoleRepository organizationUserRoleRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public User createUser(final User user) {
		// TODO: add more here ? :)
		this.setPasswordInternal(user, user.getPassword());
		return this.userRepository.save(user);
	}

	public String resetPassword(final User user) {
		final String pwd = RandomStringUtils.random(10, 0, 0, true, true);
		this.setPasswordInternal(user, pwd);
		this.userRepository.save(user);
		return pwd;
	}

	private void setPasswordInternal(final User user, final String password) {
		user.setPassword(this.passwordEncoder.encode(password));
		user.setFailedLogins(0);
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
			throw new UserNotFoundException(user.getId(), null);
		}
		this.userRepository.save(user);
	}

	public void disableUser(final String username) throws UserNotFoundException {
		final User user = this.findByUsername(username).orElseThrow(() -> new UserNotFoundException(null, username));
		user.setEnabled(false);
		this.updateUser(user);
	}

	public Optional<User> findByUsername(final String username) {
		return this.userRepository.findByUsernameEqualsIgnoreCase(username);
	}

	public Set<Role> findRolesForUserOrganization(final User user, final String organizationName) {
		return this.organizationUserRoleRepository.getByUserIdAndOrganizationName(user.getId(), organizationName)
				.stream().map(our -> our.getRole()).collect(Collectors.toSet());
	}

	public void loginSuccess(final User user) {
		if (user.getFailedLogins() > 0) {
			user.setFailedLogins(0);
			this.userRepository.save(user);
		}
	}

	public void loginFail(final String username) {
		final Optional<User> optionalUser = this.findByUsername(username);
		if (optionalUser.isPresent()) {
			final User user = optionalUser.get();
			user.setFailedLogins(user.getFailedLogins() + 1);
			this.userRepository.save(user);
		}
	}
}
