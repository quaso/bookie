package org.bookie.conf;

import org.bookie.configuration.DatasourceConfiguration;
import org.bookie.service.SeasonService;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({ DatasourceConfiguration.class, SpringWsSecurityConfiguration.class })
@ComponentScan(basePackageClasses = SeasonService.class)
public class AppConfiguration {

}
