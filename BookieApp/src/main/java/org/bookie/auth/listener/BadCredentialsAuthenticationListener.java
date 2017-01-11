package org.bookie.auth.listener;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class BadCredentialsAuthenticationListener
		extends AbstractAuthenticationListener<AuthenticationFailureBadCredentialsEvent> {

	@Override
	public void onApplicationEvent(final AuthenticationFailureBadCredentialsEvent event) {
		final Authentication authentication = event.getAuthentication();
		if (authentication.getCredentials() != null
				&& !StringUtils.isEmpty(authentication.getCredentials().toString())) {
			this.userService.loginFail((String) event.getAuthentication().getPrincipal());
		}
	}

}
