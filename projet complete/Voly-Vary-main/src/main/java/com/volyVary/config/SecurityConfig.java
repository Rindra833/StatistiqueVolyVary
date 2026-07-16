package com.volyVary.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import jakarta.servlet.DispatcherType;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    /**
     * Protège chaque module avec le rôle qui lui correspond. L'ordre des règles est important :
     * l'écran statistique, partagé entre l'administrateur et le responsable des statistiques, doit
     * être déclaré avant la règle générale réservant toutes les autres URL /admin à l'administrateur.
     */
    @Bean
    public SecurityFilterChain chaineDeSecurite(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(autorisations -> autorisations
                .dispatcherTypeMatchers(DispatcherType.FORWARD, DispatcherType.ERROR).permitAll()
                .requestMatchers(
                    "/",
                    "/connexion",
                    "/pages/login/index.html",
                    "/assets/**",
                    "/webjars/**",
                    "/favicon.ico",
                    "/error"
                ).permitAll()
                .requestMatchers("/admin/statistiques")
                    .hasAnyRole("Administrateur", "Responsable Statistiques")
                .requestMatchers(
                    "/admin/**",
                    "/pages/admin/livreurs/index.html",
                    "/pages/admin/fournitures/index.html",
                    "/pages/admin/utilisateurs/index.html",
                    "/pages/statistiques/index.html"
                ).hasRole("Administrateur")
                .requestMatchers("/transaction/**", "/transactions/**")
                    .hasAnyRole("Administrateur", "Responsable Transaction")
                .requestMatchers("/collectes/**")
                    .hasAnyRole("Administrateur", "Responsable Collecte")
                .requestMatchers("/transformation/**")
                    .hasAnyRole("Administrateur", "Responsable Transformation")
                .requestMatchers("/distribution/**")
                    .hasAnyRole("Administrateur", "Responsable Distribution")
                .requestMatchers("/client/**", "/clients/**")
                    .hasAnyRole(
                        "Administrateur",
                        "Responsable Transaction",
                        "Responsable Collecte",
                        "Responsable Distribution"
                    )
                .requestMatchers("/api/**").hasRole("Administrateur")
                .anyRequest().authenticated()
            )
            .formLogin(formulaire -> formulaire
                .loginPage("/connexion")
                .loginProcessingUrl("/connexion")
                .usernameParameter("nom")
                .passwordParameter("mdp")
                .successHandler((requete, reponse, authentification) -> reponse.sendRedirect(
                    requete.getContextPath() + determinerPageAccueil(authentification)
                ))
                .failureUrl("/connexion?erreur")
                .permitAll()
            )
            .logout(deconnexion -> deconnexion
                .logoutUrl("/deconnexion")
                .logoutSuccessUrl("/connexion?deconnexion")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .exceptionHandling(erreurs -> erreurs
                .accessDeniedPage("/acces-refuse")
            );

        return http.build();
    }

    /**
     * Choisit la première page utile après la connexion. Cette redirection évite d'envoyer tous les
     * employés vers une URL administrateur puis vers une erreur 403. Un rôle inconnu est envoyé vers
     * la page d'accès refusé afin de ne lui attribuer implicitement aucun module.
     */
    private String determinerPageAccueil(Authentication authentification) {
        if (possedeRole(authentification, "ROLE_Administrateur")
            || possedeRole(authentification, "ROLE_Responsable Statistiques")) {
            return "/admin/statistiques";
        }
        if (possedeRole(authentification, "ROLE_Responsable Transaction")) {
            return "/transactions";
        }
        if (possedeRole(authentification, "ROLE_Responsable Collecte")) {
            return "/collectes/valides";
        }
        if (possedeRole(authentification, "ROLE_Responsable Transformation")) {
            return "/transformation/lotPaddyTransforme";
        }
        if (possedeRole(authentification, "ROLE_Responsable Distribution")) {
            return "/distribution/listeFactures";
        }
        return "/acces-refuse";
    }

    /**
     * Recherche une autorité exacte dans la session Spring Security de l'utilisateur connecté.
     */
    private boolean possedeRole(Authentication authentification, String role) {
        return authentification.getAuthorities().stream()
            .anyMatch(autorite -> role.equals(autorite.getAuthority()));
    }

    @Bean
    public BCryptPasswordEncoder encodeurMotDePasse() {
        return new BCryptPasswordEncoder();
    }
}
