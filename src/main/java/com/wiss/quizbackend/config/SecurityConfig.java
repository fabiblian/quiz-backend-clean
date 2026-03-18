package com.wiss.quizbackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security-Konfiguration für die Applikation.
 *
 * Diese Klasse definiert:
 * - Wie Passwörter gehashed werden (BCrypt)
 * - Welche URLs geschützt sind
 * - CORS und CSRF Einstellungen
 */
@Configuration  // Spring scannt diese Klasse beim Start
@EnableWebSecurity  // Aktiviert Spring Security

public class SecurityConfig {

    /**
     * PasswordEncoder Bean für BCrypt Hashing.
     *
     * Work Factor 12 bedeutet:
     * - 2^12 = 4096 Hash-Iterationen
     * - Ca. 250ms pro Passwort auf modernen CPUs
     * - Guter Kompromiss zwischen Sicherheit und Performance
     *
     * @return BCryptPasswordEncoder mit Stärke 12
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        // Work Factor: 10-12 ist Standard, 14+ für hochsensible Daten
        return new BCryptPasswordEncoder(12);
    }

    /**
     * Security Filter Chain Configuration.
     *
     * TEMPORÄR: Alle Requests erlauben für Entwicklung
     * SPÄTER: JWT Authentication hinzufügen
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http)
            throws Exception {
        http
                // CSRF für REST APIs deaktivieren
                // (verwenden JWT stattdessen)
                .csrf(csrf -> csrf.disable())

                // Request Authorization Rules
                .authorizeHttpRequests(auth -> auth
                        // Auth Endpoints müssen öffentlich sein
                        .requestMatchers("/api/auth/**").permitAll()
                        // Swagger UI für API Dokumentation
                        .requestMatchers("/v3/api-docs/**",
                                "/swagger-ui/**").permitAll()
                        // TEMPORÄR: Alle anderen Requests erlauben
                        // TODO: Nach JWT Implementation -> .authenticated()
                        .anyRequest().permitAll()
                );

        return http.build();
    }
}