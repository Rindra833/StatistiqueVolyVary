package com.volyVary;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.volyVary.modele.Utilisateur;
import com.volyVary.repository.UtilisateurRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class StatistiqueWebIntegrationTests {

    private static final Pattern MOTIF_CSRF = Pattern.compile(
        "name=\"_csrf\" value=\"([^\"]+)\""
    );

    @Value("${local.server.port}")
    private int port;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private BCryptPasswordEncoder encodeurMotDePasse;

    /**
     * Démarre un vrai serveur, compile la JSP avec Jasper et vérifie que Chart.js est bien servi.
     * Le responsable Statistiques doit voir son propre menu sans liens d'administration.
     */
    @Test
    void afficheLeDashboardEtLaSidebarDuResponsableStatistiques() throws Exception {
        String motDePasse = "test-statistique-2026";
        Utilisateur utilisateur = creerUtilisateur("stat-", motDePasse, "Responsable Statistiques");

        try {
            HttpClient client = nouveauClient();
            HttpResponse<String> connexion = connecter(client, utilisateur.getNom(), motDePasse);
            assertTrue(connexion.headers().firstValue("Location").orElse("")
                .endsWith("/admin/statistiques"));

            HttpResponse<String> dashboard = envoyerGet(client, "/admin/statistiques");
            assertEquals(200, dashboard.statusCode());
            assertTrue(dashboard.body().contains("Statistique globale"));
            assertTrue(dashboard.body().contains("Responsable Statistiques"));
            assertFalse(dashboard.body().contains(">Utilisateurs</span>"));
            assertTrue(dashboard.headers().firstValue("Cache-Control").orElse("")
                .contains("no-store"));

            HttpResponse<String> graphique = envoyerGet(
                client,
                "/webjars/chart.js/4.4.1/dist/chart.umd.js"
            );
            assertEquals(200, graphique.statusCode());
            assertTrue(graphique.body().contains("Chart"));
        } finally {
            utilisateurRepository.deleteById(utilisateur.getId());
        }
    }

    /**
     * Vérifie simultanément la redirection adaptée au rôle et la protection contre une saisie
     * manuelle de l'URL statistique par un responsable d'un autre module.
     */
    @Test
    void interditLeDashboardAuResponsableTransaction() throws Exception {
        String motDePasse = "test-transaction-2026";
        Utilisateur utilisateur = creerUtilisateur("transaction-", motDePasse, "Responsable Transaction");

        try {
            HttpClient client = nouveauClient();
            HttpResponse<String> connexion = connecter(client, utilisateur.getNom(), motDePasse);
            assertTrue(connexion.headers().firstValue("Location").orElse("")
                .endsWith("/transactions"));

            HttpResponse<String> dashboard = envoyerGet(client, "/admin/statistiques");
            assertEquals(403, dashboard.statusCode());
        } finally {
            utilisateurRepository.deleteById(utilisateur.getId());
        }
    }

    /**
     * Crée un compte temporaire dans la base H2 réservée aux tests.
     */
    private Utilisateur creerUtilisateur(String prefixe, String motDePasse, String role) {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setNom(prefixe + UUID.randomUUID());
        utilisateur.setMdp(encodeurMotDePasse.encode(motDePasse));
        utilisateur.setRole(role);
        return utilisateurRepository.saveAndFlush(utilisateur);
    }

    /**
     * Construit un client HTTP qui conserve la session JSESSIONID sans suivre les redirections.
     */
    private HttpClient nouveauClient() {
        CookieManager cookies = new CookieManager(null, CookiePolicy.ACCEPT_ALL);
        return HttpClient.newBuilder()
            .cookieHandler(cookies)
            .followRedirects(HttpClient.Redirect.NEVER)
            .build();
    }

    /**
     * Reproduit le formulaire HTML de connexion, y compris le jeton CSRF rendu par Spring.
     */
    private HttpResponse<String> connecter(
        HttpClient client,
        String nom,
        String motDePasse
    ) throws Exception {
        HttpResponse<String> pageConnexion = envoyerGet(client, "/connexion");
        assertEquals(200, pageConnexion.statusCode());
        Matcher correspondance = MOTIF_CSRF.matcher(pageConnexion.body());
        assertTrue(correspondance.find());

        String formulaire = "nom=" + encoder(nom)
            + "&mdp=" + encoder(motDePasse)
            + "&_csrf=" + encoder(correspondance.group(1));
        HttpRequest requete = HttpRequest.newBuilder(adresse("/connexion"))
            .header("Content-Type", "application/x-www-form-urlencoded")
            .POST(HttpRequest.BodyPublishers.ofString(formulaire))
            .build();
        return client.send(requete, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * Envoie une requête GET vers le serveur de test.
     */
    private HttpResponse<String> envoyerGet(HttpClient client, String chemin) throws Exception {
        HttpRequest requete = HttpRequest.newBuilder(adresse(chemin)).GET().build();
        return client.send(requete, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * Construit une adresse locale à partir du port aléatoire choisi par Spring Boot.
     */
    private URI adresse(String chemin) {
        return URI.create("http://localhost:" + port + chemin);
    }

    /**
     * Encode une valeur selon le format application/x-www-form-urlencoded.
     */
    private String encoder(String valeur) {
        return URLEncoder.encode(valeur, StandardCharsets.UTF_8);
    }
}
