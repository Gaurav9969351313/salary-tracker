package com.walkingtree.salarytracker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.walkingtree.salarytracker.auth.CustomSuccessHandler;
import com.walkingtree.salarytracker.auth.CustomUserDetailsService;
import com.walkingtree.salarytracker.auth.OAuthSuccessHandler;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

	private final OAuthSuccessHandler handler;
	private final CustomUserDetailsService customUserDetailService;
	private final CustomSuccessHandler customSuccessHandler;

	@Bean
	public AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
		daoAuthenticationProvider.setUserDetailsService(customUserDetailService);
		daoAuthenticationProvider.setPasswordEncoder(encoder());
		return daoAuthenticationProvider;
	}

	@Bean
	public PasswordEncoder encoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
		httpSecurity.csrf(csrf -> csrf
				.ignoringRequestMatchers("/h2-console/**")
				.disable());

		httpSecurity.headers(headers -> headers
				.frameOptions(frameOptions -> frameOptions.disable()));

		httpSecurity.authorizeHttpRequests(authorize -> {
			authorize.requestMatchers("/user/**").hasAnyAuthority("USER", "OAUTH2_USER")
					.requestMatchers("/admin/**").hasAnyAuthority("ADMIN", "OAUTH2_ADMIN")
					.requestMatchers("/h2-console/**", "/css/**", "/js/**", "/images/**", "/favicon.ico")
					.permitAll()
					.anyRequest().permitAll();
		});

		httpSecurity.formLogin(login -> {
			login.loginPage("/")
					.loginProcessingUrl("/authenticate")
					.successHandler(customSuccessHandler)
					.usernameParameter("email")
					.passwordParameter("password");
		});

		httpSecurity.logout(logout -> {
			logout.logoutUrl("/logout")
					.logoutSuccessUrl("/?logout=true");

		});
		
		httpSecurity.oauth2Login(oauth -> {
			oauth.loginPage("/")
					.successHandler(handler);

		});

		return httpSecurity.build();

	}

}
