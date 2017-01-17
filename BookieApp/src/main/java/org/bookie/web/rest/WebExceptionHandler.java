package org.bookie.web.rest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by kvasnicka on 1/15/17.
 */
@ControllerAdvice
@RestController
public class WebExceptionHandler {
	private static Logger logger = LoggerFactory.getLogger(WebExceptionHandler.class);

	@ExceptionHandler(Exception.class)
	public void handleError404(final HttpServletRequest request, final HttpServletResponse response,
			final Exception e) {
		logger.error("Unhandled Exception", e);
		response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	}
}
