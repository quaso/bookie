package org.bookie.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.bookie.exception.NotFoundException;
import org.bookie.exception.RoleNotFoundException;
import org.bookie.exception.UserNotFoundException;
import org.bookie.model.Organization;
import org.bookie.model.OrganizationUserRole;
import org.bookie.model.Role;
import org.bookie.model.User;
import org.bookie.repository.OrganizationUserRoleRepository;
import org.bookie.repository.RoleRepository;
import org.bookie.repository.UserRepository;
import org.bookie.util.password.PasswordPolicyException;
import org.bookie.util.password.PasswordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
@Transactional
public class UserService {
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private OrganizationService organizationService;

	@Autowired
	private OrganizationUserRoleRepository organizationUserRoleRepository;

	@Autowired
	private PasswordUtils passwordUtils;

	public User createUser(final User user) throws PasswordPolicyException {
		this.setPasswordInternal(user, user.getPassword());
		return this.userRepository.save(user);
	}

	public void generatePassword(final String username) throws UserNotFoundException {
		final User user = this.findByUsername(username);
		final String pwd = this.passwordUtils.generatePassword();
		try {
			this.setPasswordInternal(user, pwd);
			this.userRepository.save(user);
			//TODO: send email with new password
		} catch (final PasswordPolicyException e) {
			// should not happen :)
		}
	}

	private void setPasswordInternal(final User user, final String password) throws PasswordPolicyException {
		this.passwordUtils.checkPasswordPolicy(password);
		user.setPassword(this.passwordUtils.encodePassword(password));
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
		final User user = this.findByUsername(username);
		user.setEnabled(false);
		this.updateUser(user);
	}

	public User findByUsername(final String username) throws UserNotFoundException {
		return this.userRepository.findByUsernameEqualsIgnoreCase(username)
				.orElseThrow(() -> new UserNotFoundException(null, username));
	}

	public void addUserToRole(final String userId, final String roleName, final String organizationName)
			throws NotFoundException {
		final User user = this.userRepository.findOne(userId);
		if (user == null) {
			throw new UserNotFoundException(userId, null);
		}
		final Role role = this.roleRepository.findByName(roleName);
		if (role == null) {
			throw new RoleNotFoundException(roleName);
		}
		final Organization org = this.organizationService.findByName(organizationName);
		final OrganizationUserRole entity = new OrganizationUserRole();
		entity.setValues(org, user, role);
		this.organizationUserRoleRepository.save(entity);
	}

	public void deleteUserFromRole(final String userId, final String roleName, final String organizationName) {
		this.organizationUserRoleRepository.deleteByUserIdAndRoleNameAndOrganizationName(userId, roleName,
				organizationName);
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
		try {
			final User user = this.findByUsername(username);
			user.setFailedLogins(user.getFailedLogins() + 1);
			this.userRepository.save(user);
		} catch (final UserNotFoundException e) {
		}
	}
}
