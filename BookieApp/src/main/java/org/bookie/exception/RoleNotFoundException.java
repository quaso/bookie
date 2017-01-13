package org.bookie.exception;

public class RoleNotFoundException extends NotFoundException {

	public RoleNotFoundException(final String roleName) {
		super("Role '" + roleName + "' could not be found");
	}

}
