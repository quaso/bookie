package org.bookie.conf;

import com.allanditzel.springframework.security.web.csrf.CsrfTokenResponseHeaderBindingFilter;
import org.bookie.auth.DatabaseAuthenticationProvider;
import org.bookie.auth.NoAuthProvider;
import org.bookie.auth.OrganizationWebAuthenticationDetailsSource;
import org.bookie.web.rest.RESTAuthenticationEntryPoint;
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
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SpringSecurityConfiguration extends WebSecurityConfigurerAdapter {
	private static final String MBEAN_TOMCAT_SERVICE = "Tomcat:type=Service";

	@Autowired
	private Environment env;

	@Autowired
	private WebAuthenticationDetailsSource authenticationDetailsSource;

	@Autowired
	private RESTAuthenticationEntryPoint authenticationEntryPoint;

	@Override
	protected void configure(final HttpSecurity http) throws Exception {

		CsrfTokenResponseHeaderBindingFilter csrfTokenFilter = new CsrfTokenResponseHeaderBindingFilter();
		http.addFilterAfter(csrfTokenFilter, CsrfFilter.class);

		http.logout()
				.logoutRequestMatcher(new AntPathRequestMatcher("/api/logout", "GET"))
				.logoutSuccessUrl("/logged-out.html");

//		http.authorizeRequests()
//				.antMatchers("/logged-out.html").permitAll()
//				.antMatchers("/access-denied.html").permitAll()
//				.antMatchers("/api/logout").permitAll()
//				.antMatchers("/api/logged").permitAll();


		http.exceptionHandling().authenticationEntryPoint(authenticationEntryPoint);

		//TODO JWT
//        http.formLogin()
//                .loginProcessingUrl("/api/login/")
//                .successHandler(authenticationSuccessHandler)
//                .failureHandler(authenticationFailureHandler);
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
