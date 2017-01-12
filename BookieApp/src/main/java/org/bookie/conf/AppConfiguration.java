package org.bookie.conf;

import org.bookie.configuration.DatasourceConfiguration;
import org.bookie.endpoint.SeasonEndpoint;
import org.bookie.service.SeasonService;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({ DatasourceConfiguration.class, SpringSecurityConfiguration.class })
@ComponentScan(basePackageClasses = { SeasonService.class, SeasonEndpoint.class })
public class AppConfiguration {
}
