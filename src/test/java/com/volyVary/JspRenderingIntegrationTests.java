package com.volyVary;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

import com.volyVary.model.Utilisateur;
import com.volyVary.repository.UtilisateurRepository;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = "app.statistiques.donnees-demonstration=false"
)
class JspRenderingIntegrationTests {

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
     * Démarre un vrai Tomcat sur un port aléatoire, compile les JSP avec Jasper, effectue une
     * connexion protégée par CSRF puis ouvre chaque liste et formulaire. Le compte temporaire
     * utilisé pour cette vérification est systématiquement supprimé dans le bloc finally.
     */
    @Test
    void toutesLesJspSontCompileesEtAccessibles() throws Exception {
        String nom = "test-jsp-" + UUID.randomUUID();
        String motDePasse = "MotDePasseSecurise123";
        Utilisateur utilisateur = creerAdministrateur(nom, motDePasse);

        try {
            CookieManager gestionnaireCookies = new CookieManager(null, CookiePolicy.ACCEPT_ALL);
            HttpClient client = HttpClient.newBuilder()
                .cookieHandler(gestionnaireCookies)
                .followRedirects(HttpClient.Redirect.NEVER)
                .build();

            HttpResponse<String> pageConnexion = envoyerGet(client, "/connexion");
            assertEquals(200, pageConnexion.statusCode());
            assertTrue(pageConnexion.body().contains("chaîne de valeur"));
            String jetonCsrf = extraireJetonCsrf(pageConnexion.body());

            HttpResponse<String> feuilleConnexion = envoyerGet(client, "/assets/css/connexion.css");
            assertEquals(200, feuilleConnexion.statusCode());
            assertTrue(
                feuilleConnexion.headers().firstValue("Content-Type").orElse("").contains("text/css")
            );
            assertTrue(feuilleConnexion.body().contains(".login-page"));

            HttpResponse<String> bibliothequeGraphique = envoyerGet(
                client,
                "/webjars/chart.js/4.4.1/dist/chart.umd.js"
            );
            assertEquals(200, bibliothequeGraphique.statusCode());
            assertTrue(bibliothequeGraphique.body().contains("Chart"));

            String formulaireConnexion = "nom=" + encoder(nom)
                + "&mdp=" + encoder(motDePasse)
                + "&_csrf=" + encoder(jetonCsrf);
            HttpRequest requeteConnexion = HttpRequest.newBuilder(adresse("/connexion"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(formulaireConnexion))
                .build();
            HttpResponse<String> resultatConnexion = client.send(
                requeteConnexion,
                HttpResponse.BodyHandlers.ofString()
            );
            assertEquals(302, resultatConnexion.statusCode());

            HttpResponse<String> pageLivreurs = verifierPage(
                client,
                "/admin/livreurs",
                "Registre des livreurs"
            );
            assertTrue(pageLivreurs.body().contains("D&eacute;connexion"));
            verifierPage(client, "/admin/livreurs/nouveau", "Ajouter un livreur");
            verifierPage(client, "/admin/fournitures", "Gestion du stock");
            verifierPage(client, "/admin/fournitures/nouvelle", "Ajouter une fourniture");
            verifierPage(client, "/admin/utilisateurs", "Gestion des comptes");
            verifierPage(client, "/admin/utilisateurs/nouveau", "Créer un compte");
            verifierPage(client, "/admin/statistiques", "Statistique globale");
        } finally {
            utilisateurRepository.deleteById(utilisateur.getId());
        }
    }

    /**
     * Crée et valide immédiatement un compte Administrateur persistant afin que le serveur HTTP,
     * exécuté dans un autre thread, puisse le charger pendant l'authentification réelle.
     */
    private Utilisateur creerAdministrateur(String nom, String motDePasse) {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setNom(nom);
        utilisateur.setMdp(encodeurMotDePasse.encode(motDePasse));
        utilisateur.setRole("Administrateur");
        return utilisateurRepository.saveAndFlush(utilisateur);
    }

    /**
     * Envoie une requête GET avec le client qui conserve le cookie JSESSIONID entre les appels.
     */
    private HttpResponse<String> envoyerGet(HttpClient client, String chemin) throws Exception {
        HttpRequest requete = HttpRequest.newBuilder(adresse(chemin)).GET().build();
        return client.send(requete, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * Vérifie qu'une JSP protégée répond avec succès et contient un texte représentatif de la vue.
     * Une erreur de compilation Jasper produirait ici un code HTTP 500 et ferait échouer le test.
     */
    private HttpResponse<String> verifierPage(
        HttpClient client,
        String chemin,
        String texteAttendu
    ) throws Exception {
        HttpResponse<String> reponse = envoyerGet(client, chemin);
        assertEquals(200, reponse.statusCode(), "Échec du rendu JSP : " + chemin);
        assertTrue(reponse.body().contains(texteAttendu), "Contenu JSP absent : " + chemin);
        return reponse;
    }

    /**
     * Extrait le jeton CSRF rendu dans le formulaire JSP afin de reproduire exactement le POST
     * effectué par un navigateur réel.
     */
    private String extraireJetonCsrf(String contenuHtml) {
        Matcher correspondance = MOTIF_CSRF.matcher(contenuHtml);
        assertTrue(correspondance.find(), "Le jeton CSRF est absent de la JSP de connexion.");
        return correspondance.group(1);
    }

    /**
     * Construit une adresse HTTP locale à partir du port aléatoire attribué par Spring Boot.
     */
    private URI adresse(String chemin) {
        return URI.create("http://localhost:" + port + chemin);
    }

    /**
     * Encode une valeur de formulaire selon le format application/x-www-form-urlencoded.
     */
    private String encoder(String valeur) {
        return URLEncoder.encode(valeur, StandardCharsets.UTF_8);
    }
}
