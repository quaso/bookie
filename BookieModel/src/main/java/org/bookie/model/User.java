package org.bookie.model;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;
import org.bookie.exception.UserContactsBlankException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.CollectionUtils;

@Entity
@org.hibernate.annotations.Proxy(lazy = false)
@Table(name = "`User`")
public class User extends AbstractEntity implements UserDetails {

	public static final int MAX_INVALID_LOGINS = 3;

	public static final String ROLE_TOKEN_USED = "ROLE_TOKEN_USED";

	@Column(name = "username", nullable = false, unique = true)
	private String username;

	@Column(name = "name", nullable = true)
	private String name;

	@Column(name = "surname", nullable = false)
	private String surname;

	@Column(name = "phone", nullable = true)
	private String phone;

	@Column(name = "email", nullable = true)
	private String email;

	@Column(name = "enabled", nullable = false)
	private boolean enabled;

	@Column(name = "verified", nullable = false)
	private boolean verified = false;

	@Column(name = "password", nullable = false)
	private String password;

	@Column(name = "oneTimeToken", nullable = true)
	private String oneTimeToken;

	@Column(name = "failedLogins", nullable = true)
	private int failedLogins;

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "userId", referencedColumnName = "id", nullable = false), inverseJoinColumns = @JoinColumn(name = "roleId", referencedColumnName = "id", nullable = false))
	private Set<Role> roles;

	@Transient
	private boolean tokenUsed = false;

	@PrePersist
	public void prePersist() {
		this.failedLogins = 0;
		this.preUpdate();
	}

	@PreUpdate
	public void preUpdate() {
		if (StringUtils.isBlank(this.phone) && StringUtils.isBlank(this.email)) {
			throw new UserContactsBlankException();
		}
	}

	@Override
	public String getUsername() {
		return this.username;
	}

	public void setUsername(final String username) {
		this.username = username;
	}

	public void setName(final String value) {
		this.name = value;
	}

	public String getName() {
		return this.name;
	}

	public void setSurname(final String value) {
		this.surname = value;
	}

	public String getSurname() {
		return this.surname;
	}

	public void setPhone(final String value) {
		this.phone = value;
	}

	public String getPhone() {
		return this.phone;
	}

	public void setEmail(final String value) {
		this.email = value;
	}

	public String getEmail() {
		return this.email;
	}

	@Override
	public boolean isEnabled() {
		return this.enabled;
	}

	public void setEnabled(final boolean enabled) {
		this.enabled = enabled;
	}

	public void setVerified(final boolean value) {
		this.verified = value;
	}

	public boolean getVerified() {
		return this.verified;
	}

	@Override
	public String getPassword() {
		return this.password;
	}

	public void setPassword(final String password) {
		this.password = password;
	}

	public String getOneTimeToken() {
		return this.oneTimeToken;
	}

	public void setOneTimeToken(final String oneTimeToken) {
		this.oneTimeToken = oneTimeToken;
	}

	public Set<Role> getRoles() {
		if (this.roles == null) {
			this.roles = new HashSet<>();
		}
		return this.roles;
	}

	public void setRoles(final Set<Role> roles) {
		this.roles = roles;
	}

	public boolean isTokenUsed() {
		return this.tokenUsed;
	}

	public void setTokenUsed(final boolean tokenUsed) {
		this.tokenUsed = tokenUsed;
	}

	public int getFailedLogins() {
		return this.failedLogins;
	}

	public void setFailedLogins(final int failedLogins) {
		this.failedLogins = failedLogins;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		final Set<GrantedAuthority> result = new TreeSet<>(new Comparator<GrantedAuthority>() {

			@Override
			public int compare(final GrantedAuthority g1, final GrantedAuthority g2) {
				if (g2.getAuthority() == null) {
					return -1;
				}

				if (g1.getAuthority() == null) {
					return 1;
				}

				return g1.getAuthority().compareTo(g2.getAuthority());
			}
		});
		if (!CollectionUtils.isEmpty(this.roles)) {
			this.roles.forEach(r -> result.add(new SimpleGrantedAuthority(r.getName())));
		}
		if (this.tokenUsed) {
			result.add(new SimpleGrantedAuthority(ROLE_TOKEN_USED));
		}

		return Collections.unmodifiableCollection(result);
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		final int maxFailedLogins = this.tokenUsed ? 1 : MAX_INVALID_LOGINS;
		return this.failedLogins < maxFailedLogins;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("User [");
		if (this.username != null) {
			builder.append("username=").append(this.username).append(", ");
		}
		if (this.name != null) {
			builder.append("name=").append(this.name).append(", ");
		}
		if (this.surname != null) {
			builder.append("surname=").append(this.surname).append(", ");
		}
		if (this.phone != null) {
			builder.append("phone=").append(this.phone).append(", ");
		}
		if (this.email != null) {
			builder.append("email=").append(this.email).append(", ");
		}
		builder.append("enabled=").append(this.enabled).append(", verified=").append(this.verified)
				.append(", failedLogins=")
				.append(this.failedLogins).append(", ");
		if (this.roles != null) {
			builder.append("roles=").append(this.toString(this.roles)).append(", ");
		}
		builder.append("tokenUsed=").append(this.tokenUsed).append("]");
		return builder.toString();
	}

	private String toString(final Collection<?> collection) {
		final StringBuilder builder = new StringBuilder();
		builder.append("[");
		int i = 0;
		for (final Iterator<?> iterator = collection.iterator(); iterator.hasNext(); i++) {
			if (i > 0) {
				builder.append(", ");
			}
			builder.append(iterator.next());
		}
		builder.append("]");
		return builder.toString();
	}
}
