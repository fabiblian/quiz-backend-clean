package com.wiss.quizbackend.service;

import com.wiss.quizbackend.entity.AppUser;
import com.wiss.quizbackend.entity.Role;
import com.wiss.quizbackend.repository.AppUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Layer für User-Management.
 * <p>
 * Warum Service Layer?
 * - Trennung von Business Logic und Web Layer (Controller)
 * - Wiederverwendbar (könnte von REST, GraphQL, CLI genutzt werden)
 * - Transactions-Management
 * - Einfacher zu testen (Unit-Tests)
 * </p>
 * @Service: Spring erstellt automatisch eine Instanz (Component Scanning)
 * @Transactional: Alle Methoden laufen in einer DB-Transaktion
 */
@Service
@Transactional
public class AppUserService {

    // Dependencies via Constructor Injection (Best Practice!)
    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructor Injection statt @Autowired auf Felder
     * Warum?
     * - Explizit welche Dependencies benötigt werden
     * - Einfacher zu testen (Mock-Objekte im Test)
     * - Immutable (final fields)
     */
    public AppUserService(AppUserRepository userRepository,
                          PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Registriert einen neuen User.
     * <p>
     * Business Logic:
     * 1. Validierung (Username/Email unique?)
     * 2. Password hashen
     * 3. User speichern
     * 4. Gespeicherten User zurückgeben (mit ID!)
     * </p>
     */
    public AppUser registerUser(String username, String email,
                                String rawPassword, Role role) {

        // Validierung: Username bereits vergeben?
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException(
                    "Username '" + username + "' ist bereits vergeben"
            );
        }

        // Validierung: E-Mail bereits registriert?
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException(
                    "Email '" + email + "' ist bereits registriert"
            );
        }

        // Passwort hashen (NIE raw password speichern!)
        String hashedPassword = passwordEncoder.encode(rawPassword);

        // User Entity erstellen
        AppUser newUser = new AppUser(username, email, hashedPassword, role);

        // Speichern und zurückgeben
        // save() gibt den gespeicherten User MIT ID zurück
        return userRepository.save(newUser);
    }

    /**
     * Findet User by Username (für Login später)
     */
    public Optional<AppUser> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Authentifiziert User (Vorbereitung für Login)
     *
     * @return Optional.empty() wenn Login fehlschlägt
     */
    public Optional<AppUser> authenticateUser(String username, String rawPassword) {
        // User suchen
        Optional<AppUser> userOpt = userRepository.findByUsername(username);

        if (userOpt.isPresent()) {
            AppUser user = userOpt.get();

            // Passwort prüfen (BCrypt macht das intern mit Salt)
            if (passwordEncoder.matches(rawPassword, user.getPassword())) {
                return userOpt;  // Login erfolgreich
            }
        }

        return Optional.empty();  // Login fehlgeschlagen
    }

    /**
     * Hilfsmethode: Prüft ob Email valid ist
     */
    private boolean isValidEmail(String email) {
        return email != null &&
                email.contains("@") &&
                email.length() > 3;
    }
}