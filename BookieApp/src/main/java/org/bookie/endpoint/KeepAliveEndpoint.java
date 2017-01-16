package org.bookie.endpoint;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/keep-alive")
public class KeepAliveEndpoint {

	@RequestMapping(method = RequestMethod.GET, value = "/")
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public Object createOrganization() {
		final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return authentication != null ? authentication.getPrincipal() : null;
	}
}
