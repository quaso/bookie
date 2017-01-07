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

		private final String organizationName;

		public OrganizationWebAuthenticationDetails(final HttpServletRequest request) {
			super(request);
			this.organizationName = request.getHeader("organizationName");
		}

		public String getOrganizationName() {
			return this.organizationName;
		}

	}

}
