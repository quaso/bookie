package org.bookie.test.auth;

import javax.transaction.Transactional;

import org.bookie.model.Role;
import org.bookie.model.User;
import org.bookie.repository.RoleRepository;
import org.bookie.service.UserService;
import org.bookie.test.conf.TestConfiguration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestConfiguration.class)
@ActiveProfiles({ "test", "dbAuth" })
@Transactional
public class DatabaseAuthenticationProviderTest {

	private static final String PASSWORD = "123456";

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private UserService userService;

	@Autowired
	private ProviderManager providerManager;

	private Role r1, r2;
	private User user;

	@Before
	public void setup() {
		this.r1 = this.createRole("one");
		this.r2 = this.createRole("two");
		this.user = this.createUser("uu", PASSWORD, this.r1, this.r2);
	}

	@Test
	public void testValidPassword() {
		final Authentication authentication = new UsernamePasswordAuthenticationToken(this.user.getUsername(),
				PASSWORD);
		final Authentication authResult = this.providerManager.authenticate(authentication);
		Assert.assertNotNull(authResult);
		Assert.assertTrue(authResult.isAuthenticated());
		Assert.assertEquals(this.user.getRoles().size(), authResult.getAuthorities().size());
		for (final Role role : this.user.getRoles()) {
			Assert.assertTrue(
					authResult.getAuthorities().stream().anyMatch(ga -> ga.getAuthority().equals(role.getName())));
		}
	}

	@Test
	public void testValidToken() {
		final String token = this.userService.createToken(this.user);
		final Authentication authentication = new UsernamePasswordAuthenticationToken(this.user.getUsername(), token);
		final Authentication authResult = this.providerManager.authenticate(authentication);
		Assert.assertNotNull(authResult);
		Assert.assertTrue(authResult.isAuthenticated());
		Assert.assertEquals(this.user.getRoles().size() + 1, authResult.getAuthorities().size());
		for (final Role role : this.user.getRoles()) {
			Assert.assertTrue(
					authResult.getAuthorities().stream().anyMatch(ga -> ga.getAuthority().equals(role.getName())));
		}
		Assert.assertTrue(
				authResult.getAuthorities().stream().anyMatch(ga -> ga.getAuthority().equals(User.ROLE_TOKEN_USED)));
		// check if token was cleared
		final User user2 = this.userService.findById(this.user.getId());
		Assert.assertNull(user2.getOneTimeToken());
	}

	@Test
	public void testValidPasswordAndToken() {
		this.userService.createToken(this.user);
		final Authentication authentication = new UsernamePasswordAuthenticationToken(this.user.getUsername(),
				PASSWORD);
		final Authentication authResult = this.providerManager.authenticate(authentication);
		Assert.assertNotNull(authResult);
		Assert.assertTrue(authResult.isAuthenticated());
		Assert.assertEquals(this.user.getRoles().size(), authResult.getAuthorities().size());
		for (final Role role : this.user.getRoles()) {
			Assert.assertTrue(
					authResult.getAuthorities().stream().anyMatch(ga -> ga.getAuthority().equals(role.getName())));
		}
		// check if token was cleared
		final User user2 = this.userService.findById(this.user.getId());
		Assert.assertNull(user2.getOneTimeToken());
	}

	@Test(expected = AuthenticationException.class)
	public void testWrongUsername() {
		final Authentication authentication = new UsernamePasswordAuthenticationToken(this.user.getUsername() + "a",
				PASSWORD);
		this.providerManager.authenticate(authentication);
	}

	@Test
	public void testWrongPassword() {
		final Authentication authentication = new UsernamePasswordAuthenticationToken(this.user.getUsername(),
				PASSWORD + "a");

		for (int i = 1; i <= User.MAX_INVALID_LOGINS + 2; i++) {
			try {
				this.providerManager.authenticate(authentication);
				Assert.fail("No exception was thrown");
			} catch (final LockedException ex) {
				final User user2 = this.userService.findByUsername(this.user.getUsername()).get();
				Assert.assertEquals(User.MAX_INVALID_LOGINS, user2.getFailedLogins());
			} catch (final AuthenticationException ex) {
				final User user2 = this.userService.findByUsername(this.user.getUsername()).get();
				Assert.assertEquals(i, user2.getFailedLogins());
				Assert.assertTrue(i <= User.MAX_INVALID_LOGINS);
			}
		}
	}

	@Test
	public void testEmptyPassword() {
		final Authentication authentication = new UsernamePasswordAuthenticationToken(this.user.getUsername(), "");
		try {
			this.providerManager.authenticate(authentication);
			Assert.fail("No exception was thrown");
		} catch (final AuthenticationException ex) {
			// user cannot be locked by empty password
			final User user2 = this.userService.findByUsername(this.user.getUsername()).get();
			Assert.assertEquals(0, user2.getFailedLogins());
		}
	}

	@Test
	public void testWrongToken() {
		final String token = this.userService.createToken(this.user);
		final Authentication authentication = new UsernamePasswordAuthenticationToken(this.user.getUsername(),
				token + "a");
		try {
			this.providerManager.authenticate(authentication);
			Assert.fail("No exception was thrown");
		} catch (final AuthenticationException ex) {
			final User user2 = this.userService.findByUsername(this.user.getUsername()).get();
			Assert.assertEquals(1, user2.getFailedLogins());
		}

		// user should be locked after first unsuccessfull attempt
		try {
			this.providerManager.authenticate(authentication);
		} catch (final LockedException ex) {
			final User user2 = this.userService.findByUsername(this.user.getUsername()).get();
			Assert.assertEquals(1, user2.getFailedLogins());
		}
	}

	private Role createRole(final String name) {
		final Role role = new Role();
		role.setName(name);
		this.roleRepository.save(role);
		return role;
	}

	private User createUser(final String username, final String password, final Role... roles) {
		final User result = new User();
		result.setUsername(username);
		result.setName("name");
		result.setSurname("surname");
		result.setPhone("123");
		result.setPassword(password);
		result.setEnabled(true);
		for (final Role role : roles) {
			result.getRoles().add(role);
		}
		this.userService.createUser(result);
		return result;
	}

}
