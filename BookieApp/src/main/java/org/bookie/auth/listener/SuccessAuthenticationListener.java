package org.bookie.auth.listener;

import org.bookie.auth.LoggedUser;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

@Component
public class SuccessAuthenticationListener extends AbstractAuthenticationListener<AuthenticationSuccessEvent> {

	@Override
	public void onApplicationEvent(final AuthenticationSuccessEvent event) {
		this.userService.loginSuccess(((LoggedUser) event.getAuthentication().getPrincipal()).getDbUser());
	}
}
