package org.bookie.conf;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.tomcat.util.modeler.Registry;
import org.bookie.auth.DatabaseAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.authentication.AuthenticationManagerBeanDefinitionParser.NullAuthenticationProvider;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SpringWsSecurityConfiguration extends WebSecurityConfigurerAdapter {
	private static final String MBEAN_TOMCAT_SERVICE = "Tomcat:type=Service";

	@Autowired
	private Environment env;

	@Override
	protected void configure(final HttpSecurity http) throws Exception {
		//TODO: add here some config :)

		//		http.csrf()
		//				.disable()
		//				.authorizeRequests()
		//				.antMatchers("/" + AppStatusEndpoint.ENDPOINT_NAME).not().authenticated() // expose /appStatus for not authenticated clients
		//				.antMatchers("/" + AppStatusEndpoint.ENDPOINT_NAME).permitAll() // expose /appStatus for all authenticated clients regardless of role
		//				//.antMatchers("/configprops/**", "/info/**", "/metrics/**", "/health/**", "/jolokia/**", "/env/**",						"/dump/**", "/trace/**")				.hasRole("managementGrp")
		//				.antMatchers("/health", "/info", "/metrics", "/env").hasRole("managementGrp")
		//				.antMatchers(ENDPOINT_READ_DOCUMENT).not().authenticated()
		//				.antMatchers(ENDPOINT_READ_DOCUMENT).permitAll()
		//				.antMatchers("/**").hasRole("servicesGrp")
		//				.anyRequest().authenticated()
		//				.and()
		//				.httpBasic();
	}

	@Override
	protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
		final String[] activeProfiles = this.env.getActiveProfiles();
		if (!ArrayUtils.contains(activeProfiles, "dbAuth")) {
			auth
					.inMemoryAuthentication()
					.withUser("admin").password("admin").roles("servicesGrp", "managementGrp")
					.and().withUser("user").password("user").roles("servicesGrp");
		} else {
			// this adds db authentication provider
			super.configure(auth);
		}

		if (Registry.getRegistry(null, null).findManagedBean(MBEAN_TOMCAT_SERVICE) != null) {
			Registry.getRegistry(null, null).unregisterComponent(MBEAN_TOMCAT_SERVICE);
		}
	}

	@Configuration
	@Profile("!dbAuth")
	public static class NoAuthenticationProviderConfiguration {

		@Bean
		public AuthenticationProvider nullAuthenticationProvider() {
			return new NullAuthenticationProvider();
		}

		@Bean
		public PasswordEncoder passwordEncoder() {
			return NoOpPasswordEncoder.getInstance();
		}
	}

	@Configuration
	@Profile("dbAuth")
	@ComponentScan(basePackageClasses = DatabaseAuthenticationProvider.class)
	public static class DatabaseAuthenticationProviderConfiguration {
		@Bean
		public PasswordEncoder passwordEncoder() {
			return new BCryptPasswordEncoder(10);
		}
	}
}
