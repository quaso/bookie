package org.bookie.web.rest;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.file.Files;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.bookie.exception.OrganizationNotFoundException;
import org.bookie.model.Organization;
import org.bookie.model.Season;
import org.bookie.model.SeasonDetails;
import org.bookie.service.OrganizationService;
import org.bookie.service.SeasonService;
import org.bookie.web.utils.EmberConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.NoHandlerFoundException;

/**
 * Created by kvasnicka on 1/13/17.
 */
@ControllerAdvice
@RestController
public class WebResourceHandler {
	private static final Logger logger = LoggerFactory.getLogger(WebResourceHandler.class);

	private static final String FRONTEND_RESOURCE_DIR = "/web";
	private static final String CONFIG_START = "config/environment\" content=\"";

	@Autowired
	private SeasonService seasonService;

	@Autowired
	private OrganizationService organizationService;

	@ExceptionHandler(NoHandlerFoundException.class)
	public void handleError404(final HttpServletRequest request, final HttpServletResponse response,
			final NoHandlerFoundException e) {
		// index.html redirect
		try {
			if ("GET".equalsIgnoreCase(e.getHttpMethod())) {
				final String requestUrl = e.getRequestURL();
				/*   if (!"".equals(request.getContextPath())) {
				    if (Objects.equals(requestUrl, request.getContextPath())) {
				        response.sendRedirect(request.getContextPath() + "/");
				        response.flushBuffer();
				        return;
				    }
				    requestUrl = StringUtils.replaceOnce(requestUrl, request.getContextPath(), "");
				}*/

				//process resource file
				File fileToResponse = new ClassPathResource(FRONTEND_RESOURCE_DIR + requestUrl).getFile();
				if (fileToResponse.exists() && fileToResponse.isFile() && fileToResponse.canRead()) {
					Files.copy(fileToResponse.toPath(), response.getOutputStream());
				} else {
					// Default INDEX.HTML
					final String organizationCode = request.getParameter("org");

					fileToResponse = new ClassPathResource(FRONTEND_RESOURCE_DIR + "/index.html").getFile();
					String content = new String(Files.readAllBytes(fileToResponse.toPath()));
					if (!"".equals(request.getContextPath())) {
						final String rootURL = StringUtils.appendIfMissing(request.getContextPath(), "/", "/");
						content = content.replaceFirst("rootURL%22%3A%22.*?%22%2C%22",
								"rootURL%22%3A%22" + rootURL + "%22%2C%22");
						content = content.replaceAll("href=\"\\/assets", "href=\"" + rootURL + "assets");
						content = content.replaceAll("src=\"\\/assets", "src=\"" + rootURL + "assets");
					}

					content = this.processEmberConfig(content, organizationCode);

					response.getWriter().println(content);
					response.getWriter().flush();
				}

				response.setCharacterEncoding("UTF-8");
				response.setStatus(HttpStatus.OK.value());
				response.setContentType(Files.probeContentType(fileToResponse.toPath()));
			}
		} catch (final Exception ex) {
			logger.warn(ex.getMessage(), ex);
			response.setStatus(HttpStatus.NOT_FOUND.value());
		}

	}

	private String processEmberConfig(final String content, final String organizationCode)
			throws OrganizationNotFoundException {
		final String result = content;

		final String configJsonEncoded = StringUtils.substringBetween(content, CONFIG_START, "\" />");
		if (configJsonEncoded == null) {
			logger.error("cannot process ember config - cannot find environment config in index.html file");
		} else {
			try {
				final String configJson = URLDecoder.decode(configJsonEncoded, "UTF-8");

				final EmberConfig emberConfig = EmberConfig.parseConfig(configJson)
						.addConfig("bookieConfig", this.generateBookieConfig(organizationCode));

				final String modifiedConfig = emberConfig.toJsonString();

				return content.replace(configJsonEncoded, URLEncoder.encode(modifiedConfig, "UTF-8"));
			} catch (final IOException e) {
				logger.error("cannot process ember config", e);
			}
		}
		return result;
	}

	private BookieConfig generateBookieConfig(final String organizationCode) throws OrganizationNotFoundException {
		final BookieConfig config = new BookieConfig();

		final Organization organization = this.organizationService.findByCode(organizationCode);
		config.setOrganization(organization);

		final SeasonDetails seasonDetails = this.seasonService.getDetailsCurrent(organization.getCode());
		config.setSeason(seasonDetails.getSeason());
		//TODO places        config.setPlaces(seasonDetails.getPlaces());

		return config;
	}

	private static class BookieConfig {
		private Organization organization;
		private Season season;
		//TODO places

		public Season getSeason() {
			return this.season;
		}

		public void setSeason(final Season season) {
			this.season = season;
		}

		public Organization getOrganization() {
			return this.organization;
		}

		public void setOrganization(final Organization organization) {
			this.organization = organization;
		}
	}

}
