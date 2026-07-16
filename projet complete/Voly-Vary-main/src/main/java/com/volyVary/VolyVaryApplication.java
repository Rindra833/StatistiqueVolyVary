package com.volyVary;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class VolyVaryApplication extends SpringBootServletInitializer {

	/**
	 * Démarre l'application avec le serveur embarqué pendant le développement.
	 */
	public static void main(String[] args) {
		SpringApplication.run(VolyVaryApplication.class, args);
	}

	/**
	 * Indique la classe principale lorsqu'un serveur charge l'archive WAR contenant les JSP.
	 */
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(VolyVaryApplication.class);
	}
}
