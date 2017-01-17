package org.bookie.configuration;

import org.bookie.model.Role;
import org.bookie.repository.BookingRepositoryCustomImpl;
import org.bookie.repository.RoleRepository;
import org.bookie.util.BookingPatternConverter;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(basePackageClasses = RoleRepository.class)
@EntityScan(basePackageClasses = Role.class)
@EnableTransactionManagement
public class DatasourceConfiguration {

	@Bean
	public BookingRepositoryCustomImpl bookingRepositoryCustomImpl() {
		return new BookingRepositoryCustomImpl();
	}

	@Bean
	public BookingPatternConverter bookingPatternConverter() {
		return new BookingPatternConverter();
	}
}
