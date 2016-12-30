package org.bookie.repository.test;

import org.bookie.configuration.DatasourceConfiguration;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Import;

@EnableAutoConfiguration
@Import(DatasourceConfiguration.class)
// @TestPropertySource(locations = "classpath:application-test.properties")
@SpringBootConfiguration
public class MainTestConfiguration {

}
