package org.bookie.repository.test;

import org.bookie.configuration.DatasourceConfiguration;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import com.fasterxml.jackson.databind.ObjectMapper;

@EnableAutoConfiguration
@Import(DatasourceConfiguration.class)
@SpringBootConfiguration
public class MainTestConfiguration {

	@Bean
	public ObjectMapper objectMapper() {
		return new ObjectMapper();
	}
}
