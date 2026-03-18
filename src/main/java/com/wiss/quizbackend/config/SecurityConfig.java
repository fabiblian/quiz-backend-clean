package com.wiss.quizbackend.config;

import com.wiss.quizbackend.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Security-Konfiguration für die Applikation.
 *
 * @Configuration: Markiert diese Klasse als Configurations-Klasse
 *                Spring scannt diese beim Start und führt alle @Bean Methoden aus
 *
 * @EnableWebSecurity: Aktiviert Spring Security für Web-Requests
 *                     ohne dies würde Spring Security nicht aktiv werden
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    // Constructor injection
    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    /**
     * PasswordEncoder Bean - wird überall in der App verwendet wo Passwörter
     * gehashed oder verifiziert werden müssen.
     *
     * @Bean: Spring erstellt EINE Instanz und verwendet sie überall
     *        (Singleton Pattern)
     *
     * @return PasswordEncoder Interface (nicht BCryptPasswordEncoder!)
     *         Warum? Flexibilität - könnte später zu Argon2 wechseln
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        // BCrypt mit Stärke 12
        // Stärke = 2^12 = 4096 Iterationen
        // Höher = sicherer aber langsamer (10-12 ist Standard 2024)
        return new BCryptPasswordEncoder(12);
    }

    /**
     * Security Filter Chain - definiert welche URLs geschützt sind
     * temporär: Alles erlauben (später mit JWT absichern)
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())  // CSRF für REST APIs deaktivieren
                // CORS: Cross-Origin Requests erlauben (Frontend auf Port 5173 darf Backend auf 8080 ansprechen)
                .cors(cors -> cors.configure(http))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()  // Registration/Login öffentlich
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**").permitAll() // Swagger öffentlich
                        // GEÄNDERT: Jetzt braucht jeder Request einen gültigen JWT Token!
                        // Vorher: permitAll() → Jeder durfte alles
                        // Jetzt: authenticated() → Nur eingeloggte User
                        .anyRequest().authenticated()
                )
                // Stateless Sessions: Spring speichert KEINE Session-Daten
                // Jeder Request muss JWT Token mitbringen (Token = Ausweis bei jeder Tür zeigen)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                // NEU: JWT Filter HINZUFÜGEN
                // der Filter wird VOR dem
                // UsernamePasswordAuthenticationFilter ausgeführt
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}