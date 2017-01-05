package org.bookie.test.conf;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.restdocs.JUnitRestDocumentation;

@SpringBootConfiguration
@EnableAutoConfiguration
@Import(TestConfiguration.class)
public class WebTestConfiguration {

	@Bean
	public JUnitRestDocumentation jUnitRestDocumentation() {
		return new JUnitRestDocumentation("target/generated-snippets");
	}

}
