package org.bookie.auth;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

public class OrganizationWebAuthenticationDetailsSource extends WebAuthenticationDetailsSource {

	@Override
	public WebAuthenticationDetails buildDetails(final HttpServletRequest context) {
		return new OrganizationWebAuthenticationDetails(context);
	}

	@SuppressWarnings("serial")
	public class OrganizationWebAuthenticationDetails extends WebAuthenticationDetails {
		public static final String HEADER_ORGANIZATION_NAME = "organizationCode";

		private final String organizationCode;

		public OrganizationWebAuthenticationDetails(final HttpServletRequest request) {
			super(request);
			this.organizationCode = request.getHeader(HEADER_ORGANIZATION_NAME);
		}

		public String getOrganizationCode() {
			return this.organizationCode;
		}

	}

}
