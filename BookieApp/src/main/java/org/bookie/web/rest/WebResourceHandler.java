package org.bookie.web.rest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.nio.file.Files;
import java.util.Objects;

/**
 * Created by kvasnicka on 1/13/17.
 */
@ControllerAdvice
@RestController
public class WebResourceHandler {

    private static final String FRONTEND_RESOURCE_DIR = "web";

    private Logger logger = LoggerFactory.getLogger(WebResourceHandler.class);

    @ExceptionHandler(NoHandlerFoundException.class)
    public void handleError404(HttpServletRequest request, HttpServletResponse response, NoHandlerFoundException e) {
        // index.html redirect
        try {
            if ("GET".equalsIgnoreCase(e.getHttpMethod())) {
                String findPath = e.getRequestURL();
                if (!"".equals(request.getContextPath())) {
                    if (Objects.equals(findPath, request.getContextPath())) {
                        response.sendRedirect(request.getContextPath() + "/");
                        response.flushBuffer();
                        return;
                    }
                    findPath = StringUtils.replaceOnce(findPath, request.getContextPath(), "");
                }
                File fileToResponse = new ClassPathResource("/" + FRONTEND_RESOURCE_DIR + findPath).getFile();
                if (fileToResponse.exists() && fileToResponse.isFile() && fileToResponse.canRead()) {
                    response.setContentType(Files.probeContentType(fileToResponse.toPath()));
                    response.setCharacterEncoding("UTF-8");
                    response.setStatus(HttpStatus.OK.value());
                    Files.copy(fileToResponse.toPath(), response.getOutputStream());
                    return;
                }

                // Default INDEX.HTML
                fileToResponse = new ClassPathResource("/" + FRONTEND_RESOURCE_DIR + "/index.html").getFile();
                String content = new String(Files.readAllBytes(fileToResponse.toPath()));
                if (!"".equals(request.getContextPath())) {
                    String rootURL = request.getContextPath();
                    if (!rootURL.endsWith("/")) {
                        rootURL = rootURL + "/";
                    }
                    content = content.replaceFirst("rootURL%22%3A%22.*?%22%2C%22", "rootURL%22%3A%22" + rootURL + "%22%2C%22");
                    content = content.replaceAll("href=\"\\/assets", "href=\"" + rootURL + "assets");
                    content = content.replaceAll("src=\"\\/assets", "src=\"" + rootURL + "assets");
                }

//                content = processEmberConfig(content);

                response.setContentType(Files.probeContentType(fileToResponse.toPath()));
                response.setCharacterEncoding("UTF-8");
                response.setStatus(HttpStatus.OK.value());
                response.getWriter().println(content);
                response.getWriter().flush();
            }
        } catch (Exception ee) {
            logger.warn(ee.getMessage(), ee);
            response.setStatus(HttpStatus.NOT_FOUND.value());
        }

    }

 /*   private String processEmberConfig(String content) {

        String CONFIG_START = "config/environment\" content=\"";

        int start = content.indexOf(CONFIG_START);

        if (start < 0) {
            logger.error("cannot process ember config - cannot find environment config in index.html file");
            return content;
        }

        start += CONFIG_START.length();

        int end = content.indexOf("\" />", start);
        String configJsonEncoded = content.substring(start, end);

        String configJson;
        try {
            configJson = URLDecoder.decode(configJsonEncoded, "UTF-8");

            EmberConfigManipulationUtil.EmberConfig emberConfig = EmberConfigManipulationUtil.parseConfig(configJson)
                    .addConfig("serverNode", FrontendLoggingUtil.getServerNode());


            String modifiedConfig = emberConfig.toJsonString();

            return content.replace(configJsonEncoded, URLEncoder.encode(modifiedConfig, "UTF-8"));
        } catch (IOException e) {
            logger.error("cannot process ember config", e);
            return content;
        }


    }*/
}
