package com.ait.app.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.ait.app.security.JwtFilter;
import com.ait.app.security.RestSecurityHandlers;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtFilter jwtFilter,
			RestSecurityHandlers securityHandlers) throws Exception {

		http.csrf(AbstractHttpConfigurer::disable) // stateless API, token in header, no cookies
				.sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.exceptionHandling(
						e -> e.authenticationEntryPoint(securityHandlers).accessDeniedHandler(securityHandlers))
				.authorizeHttpRequests(a -> a

						.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

						.requestMatchers(HttpMethod.POST, "/api/users/register", "/api/users/login").permitAll()
						.requestMatchers("/actuator/health/**", "/actuator/info", "/actuator/prometheus").permitAll()
						// These must come before the public "/api/restaurants/*" rule below.
						.requestMatchers(HttpMethod.GET, "/api/restaurants/manage", "/api/restaurants/*/menu/manage")
						.hasAnyRole("PARTNER", "ADMIN")
						.requestMatchers(HttpMethod.PUT, "/api/restaurants/*/approve", "/api/restaurants/*/reject")
						.hasRole("ADMIN")
						.requestMatchers(HttpMethod.GET, "/api/orders/restaurant/*").hasAnyRole("PARTNER", "ADMIN")

						.requestMatchers(HttpMethod.GET, "/api/restaurants", "/api/restaurants/cuisine/*",
								"/api/restaurants/*", "/api/restaurants/*/menu", "/api/categories", "/api/cuisine-types",
								"/api/feedback/restaurant/*", "/api/prices/*")
						.permitAll()

						.requestMatchers("/api/roles/**", "/api/cuisine-types/**").hasRole("ADMIN")
						.requestMatchers(HttpMethod.POST, "/api/categories").hasRole("ADMIN")
						.requestMatchers(HttpMethod.PUT, "/api/categories/*").hasRole("ADMIN")
						.requestMatchers(HttpMethod.DELETE, "/api/categories/*").hasRole("ADMIN")
						.requestMatchers(HttpMethod.PUT, "/api/prices/delivery-rules").hasRole("ADMIN")

						.requestMatchers(HttpMethod.POST, "/api/restaurants").hasRole("PARTNER")
						.requestMatchers(HttpMethod.POST, "/api/restaurants/*/menu").hasRole("PARTNER")
						.requestMatchers(HttpMethod.PUT, "/api/restaurants/menu/*").hasRole("PARTNER")
						.requestMatchers(HttpMethod.DELETE, "/api/restaurants/menu/*").hasRole("PARTNER")
						.requestMatchers(HttpMethod.PUT, "/api/orders/*/status").hasAnyRole("PARTNER", "ADMIN")

						.anyRequest().authenticated())
				.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	/**
	 * JwtFilter is a @Component, which Spring Boot would also register as a plain
	 * servlet filter (running outside the security chain). Disable that: it must
	 * only run inside the security filter chain.
	 */
	@Bean
	public FilterRegistrationBean<JwtFilter> jwtFilterRegistration(JwtFilter jwtFilter) {
		FilterRegistrationBean<JwtFilter> registration = new FilterRegistrationBean<>(jwtFilter);
		registration.setEnabled(false);
		return registration;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
