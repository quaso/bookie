package org.bookie.exception;

public class OrganizationNotFoundException extends NotFoundException {

	public OrganizationNotFoundException(final String organizationCode) {
		super("Organization '" + organizationCode + "' could not be found");
	}

}
