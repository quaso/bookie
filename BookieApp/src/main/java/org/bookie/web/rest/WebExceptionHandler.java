package org.bookie.web.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by kvasnicka on 1/15/17.
 */
@ControllerAdvice
@RestController
public class WebExceptionHandler {

    private Logger logger = LoggerFactory.getLogger(WebExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public void handleError404(HttpServletRequest request, HttpServletResponse response, Exception e) {
        logger.error("Unhandled Exception", e);
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
}
