package org.bookie.auth;

import org.apache.commons.lang3.StringUtils;
import org.bookie.model.User;
import org.bookie.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class DatabaseAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

	@Autowired
	private UserService userService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	protected void additionalAuthenticationChecks(final UserDetails userDetails,
			final UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
		Assert.isAssignable(User.class, userDetails.getClass());
		final User user = (User) userDetails;

		// check password
		if (authentication.getCredentials() == null
				|| StringUtils.isEmpty(authentication.getCredentials().toString())) {
			this.logger.debug("Authentication failed: no credentials provided");
			throw new BadCredentialsException(this.messages
					.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
		}

		final String presentedPassword = authentication.getCredentials().toString();
		final String oneTimeToken = user.getOneTimeToken();

		if (this.passwordEncoder.matches(presentedPassword, user.getPassword())) {
			// user authenticated with password
			// clear token if there is some
			if (!StringUtils.isEmpty(oneTimeToken)) {
				this.userService.clearToken(user);
			}
		} else {
			// user  NOT authenticated with password
			boolean error = true;

			if (!StringUtils.isEmpty(oneTimeToken)) {
				// check token
				user.setTokenUsed(true);
				if (this.passwordEncoder.matches(presentedPassword, user.getOneTimeToken())) {
					// one time token matches
					this.userService.clearToken(user);
					error = false;
				}
			}

			if (error) {
				this.logger.debug("Authentication failed: password does not match stored value");
				throw new BadCredentialsException(this.messages
						.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
			}
		}
	}

	@Override
	protected UserDetails retrieveUser(final String username, final UsernamePasswordAuthenticationToken authentication)
			throws AuthenticationException {
		return this.userService.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));
	}

}
