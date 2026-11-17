package com.ait.app.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.ait.app.entity.User;
import com.ait.app.repository.UserRepository;

/**
 * Creates the first ADMIN account on startup, only when app.admin.email and
 * app.admin.password are configured (environment variables ADMIN_EMAIL and
 * ADMIN_PASSWORD, or the local profile). Admins cannot self-register, and without
 * one nobody can approve restaurants. An existing account is never modified.
 */
@Component
public class AdminBootstrap implements ApplicationRunner {

	private static final Logger log = LoggerFactory.getLogger(AdminBootstrap.class);

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	@Value("${app.admin.email:}")
	private String email;

	@Value("${app.admin.password:}")
	private String password;

	@Value("${app.admin.name:Administrator}")
	private String name;

	@Value("${app.admin.phone:9000000000}")
	private String phone;

	public AdminBootstrap(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public void run(ApplicationArguments args) {

		if (email == null || email.isBlank() || password == null || password.isBlank()) {
			return;
		}

		try {
			if (userRepository.findByEmail(email.trim()).isPresent()) {
				log.info("Admin bootstrap skipped: an account with the configured admin email already exists");
				return;
			}

			if (password.length() < 8 || password.length() > 72) {
				log.warn("Admin bootstrap skipped: app.admin.password must be 8 to 72 characters");
				return;
			}

			User admin = new User();
			admin.setName(name);
			admin.setEmail(email.trim());
			admin.setPassword(passwordEncoder.encode(password));
			admin.setPhonenumber(phone);
			admin.setRole("ADMIN");
			admin.setActive(true);

			userRepository.save(admin);

			log.info("Admin account created for the configured admin email");

		} catch (RuntimeException e) {
			// Never stop the application from starting because of this convenience.
			log.warn("Admin bootstrap failed: {}", e.getClass().getSimpleName());
		}
	}
}
