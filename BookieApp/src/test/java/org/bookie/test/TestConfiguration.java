package org.bookie.test;

import org.bookie.configuration.DatasourceConfiguration;
import org.bookie.service.SeasonService;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@EnableAutoConfiguration
@Import(DatasourceConfiguration.class)
@ComponentScan(basePackageClasses = SeasonService.class)
public class TestConfiguration {

}
