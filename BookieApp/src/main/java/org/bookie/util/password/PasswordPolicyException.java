package org.bookie.util.password;

public class PasswordPolicyException extends Exception {

	public PasswordPolicyException() {
		super("Password does not meet required policy");
	}
}
