package main.java.com.paine.core.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private UserDetailsService userDetailsService;

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		// @formatter:off
		
		http
			.authorizeRequests()
			.antMatchers("/login", "/logout/**").permitAll()
			.anyRequest().authenticated()
			.and()
			.formLogin()
            .loginPage("/login")
            .defaultSuccessUrl("/ingresar1")
            .permitAll()
            .and().csrf().disable();		

		// @formatter:on
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService);
	}


	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/css/**", "/fonts/**", "/images/**", "/js/**");
	}

	// @Autowired
	// public void configureGlobal(AuthenticationManagerBuilder auth) throws
	// Exception {
	// auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
	// }

	// @Bean
	// public PasswordEncoder passwordEncoder() {
	// PasswordEncoder encoder = new BCryptPasswordEncoder();
	// return encoder;
	// }
	//
	// @Autowired
	// private DataSource dataSource;
	//
	// public PersistentTokenRepository persistentTokenRepository() {
	// JdbcTokenRepositoryImpl db = new JdbcTokenRepositoryImpl();
	// db.setDataSource(dataSource);
	// return db;
	// }
}
