package com.volyVary;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "app.statistiques.donnees-demonstration=false")
class ApplicationTests {

	@Test
	/**
	 * Vérifie que toutes les dépendances Spring peuvent être créées et reliées sans erreur.
	 */
	void contextLoads() {
	}

}
