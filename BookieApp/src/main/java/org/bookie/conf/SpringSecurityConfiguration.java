package org.bookie.conf;

import org.bookie.auth.DatabaseAuthenticationProvider;
import org.bookie.auth.NoAuthProvider;
import org.bookie.auth.OrganizationWebAuthenticationDetailsSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

@Configuration
@EnableWebSecurity
public class SpringSecurityConfiguration extends WebSecurityConfigurerAdapter {
	private static final String MBEAN_TOMCAT_SERVICE = "Tomcat:type=Service";

	@Autowired
	private Environment env;

	@Autowired
	private WebAuthenticationDetailsSource authenticationDetailsSource;

	@Override
	protected void configure(final HttpSecurity http) throws Exception {
		http.csrf().disable().authorizeRequests()
				.antMatchers("/**").not().authenticated();
		//				.antMatchers("/**").permitAll();
		// TODO: add here some config :)

		//		http.csrf().disable().authorizeRequests()
		// .antMatchers("/" +
		// AppStatusEndpoint.ENDPOINT_NAME).not().authenticated() //
		// expose /appStatus for not authenticated clients
		// .antMatchers("/" +
		// AppStatusEndpoint.ENDPOINT_NAME).permitAll() // expose
		// /appStatus for all authenticated clients regardless of role
		// //.antMatchers("/configprops/**", "/info/**", "/metrics/**",
		// "/health/**", "/jolokia/**", "/env/**", "/dump/**",
		// "/trace/**") .hasRole("managementGrp")
		// .antMatchers("/health", "/info", "/metrics",
		// "/env").hasRole("managementGrp")
		// .antMatchers(ENDPOINT_READ_DOCUMENT).not().authenticated()
		// .antMatchers(ENDPOINT_READ_DOCUMENT).permitAll()
		//				.antMatchers("/**").hasRole("SUPER_ADMIN").anyRequest().authenticated().and().httpBasic()

		// IMPORTANT
		//				.authenticationDetailsSource(this.authenticationDetailsSource);
	}

	//	@Override
	//	protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
	//		final String[] activeProfiles = this.env.getActiveProfiles();
	//		if (!ArrayUtils.contains(activeProfiles, "dbAuth")) {
	//			auth.inMemoryAuthentication().withUser("admin").password("admin").roles("admin", "managementGrp")
	//					.and().withUser("user").password("user").roles("servicesGrp");
	//		} else {
	// this adds db authentication provider
	//		super.configure(auth);
	//		}
	//
	//		if (Registry.getRegistry(null, null).findManagedBean(MBEAN_TOMCAT_SERVICE) != null) {
	//			Registry.getRegistry(null, null).unregisterComponent(MBEAN_TOMCAT_SERVICE);
	//		}
	//	}

	@Bean
	public WebAuthenticationDetailsSource authenticationDetailsSource() {
		return new OrganizationWebAuthenticationDetailsSource();
	}

	@Configuration
	@Profile("!dbAuth")
	public static class NoAuthenticationProviderConfiguration {

		@Bean
		public AuthenticationProvider noAuthenticationProvider() {
			//			return new NullAuthenticationProvider();
			return new NoAuthProvider();
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
