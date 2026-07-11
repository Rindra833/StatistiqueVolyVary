package com.volyVary;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import com.volyVary.model.Livreur;
import com.volyVary.model.Utilisateur;
import com.volyVary.repository.LivreurRepository;
import com.volyVary.repository.UtilisateurRepository;

@SpringBootTest(properties = "app.statistiques.donnees-demonstration=false")
@AutoConfigureMockMvc
@Transactional
class MvcIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private LivreurRepository livreurRepository;

    @Autowired
    private BCryptPasswordEncoder encodeurMotDePasse;

    /**
     * Vérifie le parcours principal de l'application JSP : création d'un compte de test,
     * authentification par formulaire, affichage d'une vue avec Model, cycle CRUD utilisant
     * POST-Redirect-GET, puis déconnexion et invalidation de la session.
     */
    @Test
    void parcoursMvcCompletAvecSessionEtCrud() throws Exception {
        String nomUtilisateur = "test-" + UUID.randomUUID();
        String motDePasse = "MotDePasseSecurise123";
        enregistrerAdministrateur(nomUtilisateur, motDePasse);

        MvcResult resultatConnexion = mockMvc.perform(post("/connexion")
                .with(csrf())
                .param("nom", nomUtilisateur)
                .param("mdp", motDePasse))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/admin/livreurs"))
            .andExpect(authenticated().withUsername(nomUtilisateur))
            .andReturn();

        MockHttpSession session = (MockHttpSession) resultatConnexion.getRequest().getSession(false);

        mockMvc.perform(get("/admin/livreurs").session(session))
            .andExpect(status().isOk())
            .andExpect(view().name("livreurs/liste"))
            .andExpect(model().attributeExists("livreurs", "pageActive"));

        mockMvc.perform(post("/admin/livreurs/enregistrer")
                .session(session)
                .with(csrf())
                .param("nom", "Livreur JSP")
                .param("telephone", "0340000000")
                .param("adresse", "Antananarivo")
                .param("vehicule", "Moto")
                .param("immatriculation", "JSP-001")
                .param("disponibilite", "Disponible"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/admin/livreurs"))
            .andExpect(flash().attributeExists("messageSucces"));

        Livreur livreur = livreurRepository.findByNomContainingIgnoreCase("Livreur JSP").get(0);

        mockMvc.perform(post("/admin/livreurs/enregistrer")
                .session(session)
                .with(csrf())
                .param("id", livreur.getId().toString())
                .param("nom", "Livreur JSP modifié")
                .param("telephone", "0340000001")
                .param("adresse", "Ambohimangakely")
                .param("vehicule", "Camionnette")
                .param("immatriculation", "JSP-002")
                .param("disponibilite", "Indisponible"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/admin/livreurs"));

        mockMvc.perform(post("/admin/livreurs/{id}/supprimer", livreur.getId())
                .session(session)
                .with(csrf()))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/admin/livreurs"));

        mockMvc.perform(post("/deconnexion").session(session).with(csrf()))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/connexion?deconnexion"));

        mockMvc.perform(get("/admin/livreurs").session(session))
            .andExpect(status().is3xxRedirection());
    }

    /**
     * Confirme que la connexion est publique, que les pages d'administration sont protégées et
     * que les anciens chemins HTML sont maintenant résolus par les contrôleurs MVC vers les JSP.
     */
    @Test
    void vuesJspPubliquesProtegeesEtAliasCompatibles() throws Exception {
        mockMvc.perform(get("/connexion"))
            .andExpect(status().isOk())
            .andExpect(view().name("connexion"));

        mockMvc.perform(get("/admin/livreurs"))
            .andExpect(status().is3xxRedirection());

        mockMvc.perform(get("/pages/admin/livreurs/index.html"))
            .andExpect(status().is3xxRedirection());
    }

    /**
     * S'assure qu'un formulaire de modification envoyé sans jeton CSRF est refusé. Cette
     * protection est importante maintenant que les opérations sont réalisées par des POST HTML.
     */
    @Test
    void formulaireSansJetonCsrfEstRefuse() throws Exception {
        String nomUtilisateur = "test-csrf-" + UUID.randomUUID();
        String motDePasse = "MotDePasseSecurise123";
        enregistrerAdministrateur(nomUtilisateur, motDePasse);

        MvcResult resultatConnexion = mockMvc.perform(post("/connexion")
                .with(csrf())
                .param("nom", nomUtilisateur)
                .param("mdp", motDePasse))
            .andReturn();
        MockHttpSession session = (MockHttpSession) resultatConnexion.getRequest().getSession(false);

        mockMvc.perform(post("/admin/livreurs/enregistrer")
                .session(session)
                .param("nom", "Tentative sans CSRF"))
            .andExpect(status().isForbidden());
    }

    /**
     * Crée un administrateur temporaire avec un hash BCrypt. La transaction de test est annulée
     * à la fin de chaque méthode, aucune donnée de test n'est donc conservée dans PostgreSQL.
     */
    private void enregistrerAdministrateur(String nom, String motDePasse) {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setNom(nom);
        utilisateur.setMdp(encodeurMotDePasse.encode(motDePasse));
        utilisateur.setRole("Administrateur");
        utilisateurRepository.saveAndFlush(utilisateur);
    }
}
