package org.bookie.test.auth;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.bookie.auth.LoggedUser;
import org.bookie.auth.OrganizationWebAuthenticationDetailsSource.OrganizationWebAuthenticationDetails;
import org.bookie.exception.UserNotFoundException;
import org.bookie.model.Organization;
import org.bookie.model.OrganizationUserRole;
import org.bookie.model.Role;
import org.bookie.model.User;
import org.bookie.repository.OrganizationUserRoleRepository;
import org.bookie.service.UserService;
import org.bookie.test.AbstractTest;
import org.bookie.util.password.PasswordPolicyException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

public class DatabaseAuthenticationProviderTest extends AbstractTest {

	private static final String PASSWORD = "123456";

	@Autowired
	private UserService userService;

	@Autowired
	private OrganizationUserRoleRepository organizationUserRoleRepository;

	@Autowired
	private AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource;

	@Autowired
	private ProviderManager providerManager;

	private Role role1, role2;
	private User user1, user2;
	private Organization org1, org2;

	private final Map<String, Map<String, Set<Role>>> orgUserRoles = new HashMap<>();

	@Before
	public void setup() throws PasswordPolicyException {
		this.org1 = this.createOrganization("org1");
		this.org2 = this.createOrganization("org2");

		this.role1 = this.createRole("one");
		this.role2 = this.createRole("two");
		this.user1 = this.createUser("uu1", PASSWORD);
		this.user2 = this.createUser("uu2", PASSWORD);

		this.addOrganizationUserRole(this.org1, this.user1, this.role1);
		this.addOrganizationUserRole(this.org1, this.user1, this.role2);
		this.addOrganizationUserRole(this.org1, this.user2, this.role2);
		this.addOrganizationUserRole(this.org2, this.user2, this.role1);
	}

	private Authentication authenticate(final String username, final String password, final String organizationName) {
		final UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username,
				password);
		final MockHttpServletRequest request = new MockHttpServletRequest();
		request.addHeader(OrganizationWebAuthenticationDetails.HEADER_ORGANIZATION_NAME, organizationName);
		authRequest.setDetails(this.authenticationDetailsSource.buildDetails(request));
		return this.providerManager.authenticate(authRequest);
	}

	@Test
	public void testValidPassword() {
		Authentication authResult;
		Collection<Role> roles;

		authResult = this.authenticate(this.user1.getUsername(), PASSWORD, this.org1.getName());
		Assert.assertNotNull(authResult);
		Assert.assertTrue(authResult.isAuthenticated());
		roles = this.orgUserRoles.get(this.org1.getName()).get(this.user1.getUsername());
		Assert.assertEquals(roles.size(), authResult.getAuthorities().size());
		for (final Role role : roles) {
			Assert.assertTrue(
					authResult.getAuthorities().stream().anyMatch(ga -> ga.getAuthority().equals(role.getName())));
		}

		// user2 has just one role for org1
		authResult = this.authenticate(this.user2.getUsername(), PASSWORD, this.org1.getName());
		roles = this.orgUserRoles.get(this.org1.getName()).get(this.user2.getUsername());
		Assert.assertEquals(1, authResult.getAuthorities().size());
		for (final Role role : roles) {
			Assert.assertTrue(
					authResult.getAuthorities().stream().anyMatch(ga -> ga.getAuthority().equals(role.getName())));
		}

		// user1 has no roles for org2
		authResult = this.authenticate(this.user1.getUsername(), PASSWORD, this.org2.getName());
		roles = this.orgUserRoles.get(this.org2.getName()).get(this.user1.getUsername());
		Assert.assertEquals(0, authResult.getAuthorities().size());
	}

	@Test(expected = AuthenticationException.class)
	public void testWrongUsername() {
		this.authenticate(this.user1.getUsername() + "a", PASSWORD, this.org1.getName());
	}

	@Test
	public void testWrongPassword() throws UserNotFoundException {
		try {
			this.authenticate(this.user1.getUsername(), PASSWORD + "a", this.org1.getName());
			Assert.fail("No exception was thrown");
		} catch (final AuthenticationException ex) {
			final User dbUser1 = this.userService.findByUsername(this.user1.getUsername());
			Assert.assertEquals(1, dbUser1.getFailedLogins());
		}
	}

	@Test
	public void testLock() throws UserNotFoundException {
		for (int i = 1; i <= LoggedUser.MAX_INVALID_LOGINS + 2; i++) {
			try {
				this.authenticate(this.user1.getUsername(), PASSWORD + "a", this.org1.getName());
				Assert.fail("No exception was thrown");
			} catch (final LockedException ex) {
				final User dbUser1 = this.userService.findByUsername(this.user1.getUsername());
				Assert.assertEquals(LoggedUser.MAX_INVALID_LOGINS, dbUser1.getFailedLogins());
			} catch (final AuthenticationException ex) {
				final User dbUser1 = this.userService.findByUsername(this.user1.getUsername());
				Assert.assertEquals(i, dbUser1.getFailedLogins());
				Assert.assertTrue(i <= LoggedUser.MAX_INVALID_LOGINS);
			}
		}
		// test user2 is not affected
		this.authenticate(this.user2.getUsername(), PASSWORD, this.org1.getName());
		final User dbUser2 = this.userService.findByUsername(this.user2.getUsername());
		Assert.assertEquals(0, dbUser2.getFailedLogins());
	}

	@Test
	public void testLockUnlock() throws UserNotFoundException {
		User dbUser1;
		for (int i = 1; i <= LoggedUser.MAX_INVALID_LOGINS + 2; i++) {
			try {
				this.authenticate(this.user1.getUsername(), PASSWORD + "a", this.org1.getName());
				Assert.fail("No exception was thrown");
			} catch (final LockedException ex) {
				dbUser1 = this.userService.findByUsername(this.user1.getUsername());
				Assert.assertEquals(LoggedUser.MAX_INVALID_LOGINS, dbUser1.getFailedLogins());
			} catch (final AuthenticationException ex) {
				dbUser1 = this.userService.findByUsername(this.user1.getUsername());
				Assert.assertEquals(i, dbUser1.getFailedLogins());
				Assert.assertTrue(i <= LoggedUser.MAX_INVALID_LOGINS);
			}
		}
		this.userService.generatePassword(this.user1.getUsername());
		dbUser1 = this.userService.findByUsername(this.user1.getUsername());
		Assert.assertEquals(0, dbUser1.getFailedLogins());

		// test user2 is not affected
		this.authenticate(this.user2.getUsername(), PASSWORD, this.org1.getName());
		final User dbUser2 = this.userService.findByUsername(this.user2.getUsername());
		Assert.assertEquals(0, dbUser2.getFailedLogins());

		try {
			this.authenticate(this.user1.getUsername(), dbUser1.getPassword(), this.org1.getName());
		} catch (final AuthenticationException ex) {
			dbUser1 = this.userService.findByUsername(this.user1.getUsername());
			Assert.assertEquals(1, dbUser1.getFailedLogins());
		}

	}

	@Test
	public void testEmptyPassword() throws UserNotFoundException {
		try {
			this.authenticate(this.user1.getUsername(), "", this.org1.getName());
			Assert.fail("No exception was thrown");
		} catch (final AuthenticationException ex) {
			// user cannot be locked by empty password
			final User dbUser1 = this.userService.findByUsername(this.user1.getUsername());
			Assert.assertEquals(0, dbUser1.getFailedLogins());
		}
	}

	private User createUser(final String username, final String password) throws PasswordPolicyException {
		final User result = new User();
		result.setUsername(username);
		result.setName("n" + username);
		result.setSurname("surname");
		result.setPassword(password);
		result.setPhone("123");
		result.setEnabled(true);
		return this.userService.createUser(result);
	}

	private void addOrganizationUserRole(final Organization org, final User user, final Role role) {
		final OrganizationUserRole our = new OrganizationUserRole();
		our.setValues(org, user, role);
		this.organizationUserRoleRepository.save(our);
		if (!this.orgUserRoles.containsKey(org.getName())) {
			this.orgUserRoles.put(org.getName(), new HashMap<>());
		}
		if (!this.orgUserRoles.get(org.getName()).containsKey(user.getUsername())) {
			this.orgUserRoles.get(org.getName()).put(user.getUsername(), new HashSet<>());
		}
		this.orgUserRoles.get(org.getName()).get(user.getUsername()).add(role);
	}

}
