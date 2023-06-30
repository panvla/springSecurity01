package com.vladimirpandurov.springSecurity01B;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class SpringSecurity01BApplication {

	private static final int STRENGTH = 12 ;

	public static void main(String[] args) {
		SpringApplication.run(SpringSecurity01BApplication.class, args);
	}

	@Bean
	public BCryptPasswordEncoder encoder() {
		return new BCryptPasswordEncoder(STRENGTH);
	}

}
