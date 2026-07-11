package com.volyVary.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import jakarta.servlet.DispatcherType;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Configure la sécurité de l'application JSP. La page de connexion et les fichiers
     * statiques restent publics, tandis que les écrans d'administration demandent le rôle
     * Administrateur. Spring Security traite directement le formulaire de connexion,
     * conserve l'utilisateur dans la session et vérifie automatiquement les jetons CSRF.
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
                    "/favicon.ico",
                    "/error"
                ).permitAll()
                .requestMatchers(
                    "/admin/**",
                    "/pages/admin/livreurs/index.html",
                    "/pages/admin/fournitures/index.html",
                    "/pages/admin/utilisateurs/index.html"
                ).hasRole("Administrateur")
                .anyRequest().authenticated()
            )
            .formLogin(formulaire -> formulaire
                .loginPage("/connexion")
                .loginProcessingUrl("/connexion")
                .usernameParameter("nom")
                .passwordParameter("mdp")
                .defaultSuccessUrl("/admin/livreurs", true)
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
     * Fournit l'encodeur BCrypt utilisé à la création ou à la modification des comptes.
     * Les mots de passe ne sont ainsi jamais enregistrés en clair dans PostgreSQL.
     */
    @Bean
    public BCryptPasswordEncoder encodeurMotDePasse() {
        return new BCryptPasswordEncoder();
    }
}
