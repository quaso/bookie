package org.bookie.web.filters;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import java.io.IOException;

/**
 * Created by kvasnicka on 1/13/17.
 */
public class UncaughtExceptionFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(UncaughtExceptionFilter.class);

    @Override
    public void destroy() {
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        try {
            chain.doFilter(request, response);
        } catch (Throwable ex) {
            logger.error("Unhandled Exception", ex);
            throw ex;
        }

    }
}