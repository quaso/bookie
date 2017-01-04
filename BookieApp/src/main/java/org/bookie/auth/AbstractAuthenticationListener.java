package org.bookie.auth;

import org.bookie.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

public abstract class AbstractAuthenticationListener<T extends ApplicationEvent> implements ApplicationListener<T> {

	@Autowired
	protected UserService userService;

	@Override
	public abstract void onApplicationEvent(T event);

}
