package org.bookie.exception;

public class OrganizationNotFoundException extends Exception {

	public OrganizationNotFoundException(final String organizationName) {
		super("Organization '" + organizationName + "' could not be found");
	}

}
