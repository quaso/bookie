package org.bookie.util.password;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordUtils {
	@Autowired
	private PasswordEncoder passwordEncoder;

	public String encodePassword(final String password) {
		return this.passwordEncoder.encode(password);
	}

	public String generatePassword() {
		return RandomStringUtils.random(10, 0, 0, true, true);
	}

	public void checkPasswordPolicy(final String password) throws PasswordPolicyException {
		if (StringUtils.isBlank(password)) {
			throw new PasswordPolicyException();
		}
	}
}
