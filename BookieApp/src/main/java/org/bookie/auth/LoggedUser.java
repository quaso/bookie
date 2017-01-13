package org.bookie.auth;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.bookie.model.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;

public class LoggedUser implements UserDetails {
	public static final int MAX_INVALID_LOGINS = 3;
	public static final String ROLE_TOKEN_USED = "ROLE_TOKEN_USED";

	private static final GrantedAuthority AUTHORITY_TOKEN_USED = new SimpleGrantedAuthority(ROLE_TOKEN_USED);

	private final org.bookie.model.User dbUser;
	private boolean tokenUsed = false;
	private final Set<GrantedAuthority> authorities = new HashSet<>();

	public LoggedUser(final org.bookie.model.User dbUser, final Set<Role> rolesForOrganization) {
		Assert.notNull(dbUser);
		this.dbUser = dbUser;
		for (final Role role : rolesForOrganization) {
			this.authorities.add(new SimpleGrantedAuthority(role.getName()));
		}
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return this.authorities;
	}

	@Override
	public String getPassword() {
		return this.dbUser.getPassword();
	}

	@Override
	public String getUsername() {
		return this.dbUser.getUsername();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		final int maxFailedLogins = this.tokenUsed ? 1 : MAX_INVALID_LOGINS;
		return this.dbUser.getFailedLogins() < maxFailedLogins;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return this.dbUser.isEnabled();
	}

	public boolean isTokenUsed() {
		return this.tokenUsed;
	}

	public void setTokenUsed(final boolean tokenUsed) {
		this.tokenUsed = tokenUsed;
		if (tokenUsed) {
			this.authorities.add(AUTHORITY_TOKEN_USED);
		} else {
			this.authorities.remove(AUTHORITY_TOKEN_USED);
		}
	}

	public org.bookie.model.User getDbUser() {
		return this.dbUser;
	}

}
