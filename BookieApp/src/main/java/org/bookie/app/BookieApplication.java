package org.bookie.app;

import org.bookie.conf.AppConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(AppConfiguration.class)
public class BookieApplication {

	public static void main(final String[] args) {
		SpringApplication.run(BookieApplication.class, args);
	}
}
