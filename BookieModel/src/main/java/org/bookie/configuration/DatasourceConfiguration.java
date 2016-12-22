package org.bookie.configuration;

import org.bookie.model.Role;
import org.bookie.repository.RoleRepository;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackageClasses = RoleRepository.class)
@EntityScan(basePackageClasses = Role.class)
public class DatasourceConfiguration {

}
