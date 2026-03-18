package com.wiss.quizbackend.service;

import com.wiss.quizbackend.entity.AppUser;
import com.wiss.quizbackend.entity.Role;
import com.wiss.quizbackend.repository.AppUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Layer fÃ¼r User-Management.
 * <p>
 * Warum Service Layer?
 * - Trennung von Business Logic und Web Layer (Controller)
 * - Wiederverwendbar (kÃ¶nnte von REST, GraphQL, CLI genutzt werden)
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
     * - Explizit welche Dependencies benÃ¶tigt werden
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
     * 4. Gespeicherten User zurÃ¼ckgeben (mit ID!)
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

        // Speichern und zurÃ¼ckgeben
        // save() gibt den gespeicherten User MIT ID zurÃ¼ck
        return userRepository.save(newUser);
    }

    /**
     * Findet User by Username (fÃ¼r Login spÃ¤ter)
     */
    public Optional<AppUser> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Findet User by Email (fuer Login mit E-Mail)
     */
    public Optional<AppUser> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Authentifiziert User (Vorbereitung fÃ¼r Login)
     *
     * @return Optional.empty() wenn Login fehlschlÃ¤gt
     */
    public Optional<AppUser> authenticateUser(String username, String rawPassword) {
        // User suchen
        Optional<AppUser> userOpt = userRepository.findByUsername(username);

        if (userOpt.isPresent()) {
            AppUser user = userOpt.get();

            // Passwort prÃ¼fen (BCrypt macht das intern mit Salt)
            if (passwordEncoder.matches(rawPassword, user.getPassword())) {
                return userOpt;  // Login erfolgreich
            }
        }

        return Optional.empty();  // Login fehlgeschlagen
    }

    /**
     * Hilfsmethode: PrÃ¼ft ob Email valid ist
     */
    private boolean isValidEmail(String email) {
        return email != null &&
                email.contains("@") &&
                email.length() > 3;
    }
}
