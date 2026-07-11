package com.volyVary;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class Application extends SpringBootServletInitializer {

	/**
	 * Démarre l'application avec le serveur Tomcat embarqué pendant le développement.
	 * Cette méthode permet de conserver la commande simple « mvn spring-boot:run ».
	 */
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	/**
	 * Indique à un serveur d'applications externe quelle classe Spring Boot doit charger
	 * lorsque le projet est déployé sous la forme d'une archive WAR.
	 */
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(Application.class);
	}

}
