package org.bookie.auth;

import java.util.HashSet;
import java.util.Set;

import org.bookie.model.Role;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

public class NoAuthProvider extends AbstractUserDetailsAuthenticationProvider {
	@Override
	protected void additionalAuthenticationChecks(final UserDetails userDetails,
			final UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
		// empty
	}

	@Override
	protected UserDetails retrieveUser(final String username, final UsernamePasswordAuthenticationToken authentication)
			throws AuthenticationException {
		final org.bookie.model.User dbUser = new org.bookie.model.User();
		dbUser.setUsername(username);
		dbUser.setEnabled(true);
		final Set<Role> roles = new HashSet<Role>();
		final Role role = new Role();
		role.setName("ROLE_SUPER_ADMIN");
		roles.add(role);
		return new LoggedUser(dbUser, roles);
	}

}
