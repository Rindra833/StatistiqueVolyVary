package com.volyVary;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.containsString;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import com.volyVary.model.Utilisateur;
import com.volyVary.repository.UtilisateurRepository;
import com.jayway.jsonpath.JsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ApiIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Test
    void leLoginCreeUneSessionUtilisableParLeCrud() throws Exception {
        String nom = "test-" + UUID.randomUUID();
        String motDePasse = "MotDePasseSecurise123";

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setNom(nom);
        utilisateur.setMdp(passwordEncoder.encode(motDePasse));
        utilisateur.setRole("Administrateur");
        utilisateurRepository.saveAndFlush(utilisateur);

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"nom":"%s","mdp":"%s"}
                    """.formatted(nom, motDePasse)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nom").value(nom))
            .andExpect(jsonPath("$.role").value("Administrateur"))
            .andReturn();

        MockHttpSession session = (MockHttpSession) loginResult.getRequest().getSession(false);

        MvcResult createResult = mockMvc.perform(post("/api/livreurs")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "nom":"Livreur de test",
                      "telephone":"0340000000",
                      "adresse":"Antananarivo",
                      "vehicule":"Moto",
                      "immatriculation":"TEST-001",
                      "disponibilite":"Disponible"
                    }
                    """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").isNumber())
            .andExpect(jsonPath("$.nom").value("Livreur de test"))
            .andReturn();

        Number createdId = JsonPath.read(
            createResult.getResponse().getContentAsString(),
            "$.id"
        );

        mockMvc.perform(get("/api/livreurs").session(session))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].nom").exists());

        mockMvc.perform(put("/api/livreurs/{id}", createdId.longValue())
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "nom":"Livreur modifié",
                      "telephone":"0340000001",
                      "adresse":"Ambohimangakely",
                      "vehicule":"Camionnette",
                      "immatriculation":"TEST-002",
                      "disponibilite":"Indisponible"
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nom").value("Livreur modifié"));

        mockMvc.perform(delete("/api/livreurs/{id}", createdId.longValue())
                .session(session))
            .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/livreurs/{id}", createdId.longValue())
                .session(session))
            .andExpect(status().isNotFound());

        mockMvc.perform(get("/pages/admin/livreurs/index.html").session(session))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("id=\"livreurs-table\"")))
            .andExpect(content().string(containsString("id=\"livreur-form\"")));

        mockMvc.perform(get("/pages/admin/fournitures/index.html").session(session))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("id=\"fournitures-table\"")))
            .andExpect(content().string(containsString("id=\"fourniture-form\"")));

        mockMvc.perform(get("/pages/admin/utilisateurs/index.html").session(session))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("id=\"utilisateurs-table\"")))
            .andExpect(content().string(containsString("id=\"utilisateur-form\"")));

        mockMvc.perform(post("/logout").session(session))
            .andExpect(status().is3xxRedirection());

        mockMvc.perform(get("/api/livreurs").session(session))
            .andExpect(status().isForbidden());
    }

    @Test
    void unMauvaisMotDePasseRetourne401() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"nom":"inconnu","mdp":"incorrect"}
                    """))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void lesPagesPubliquesEtProtegeesSontCorrectementSeparees() throws Exception {
        mockMvc.perform(get("/pages/login/index.html"))
            .andExpect(status().isOk());

        mockMvc.perform(get("/api/livreurs"))
            .andExpect(status().isForbidden());
    }
}
