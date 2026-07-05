package com.Hasner.VolyVary.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Désactivé temporairement pour simplifier le formulaire JSP
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login", "/error", "/perform_login", "/perform_logout").permitAll() // Les pages publiques
                .anyRequest().authenticated()          // Le reste nécessite d'être connecté
            )
            .formLogin(form -> form
                .loginPage("/login")                   // URL de notre contrôleur
                .loginProcessingUrl("/perform_login")  // L'URL vers laquelle le formulaire JSP va POSTER
                .defaultSuccessUrl("/home", true)      // Redirection après connexion
                .failureUrl("/login?error=true")       // En cas d'erreur
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/perform_logout")
                .logoutSuccessUrl("/login?logout=true")
                .permitAll()
            );

        return http.build();
    }
}
