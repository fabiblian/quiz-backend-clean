package com.wiss.quizbackend.entity;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "app_users")
public class AppUser implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;  // ACHTUNG: Wird in nächster Lektion gehashed!
    // NIE Passwörter im Klartext speichern!

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    // Konstruktoren
    public AppUser() {}

    public AppUser(String username, String email, String password, Role role) {
        // id wird NICHT gesetzt - JPA kümmert sich darum!
        // version wird NICHT gesetzt - JPA kümmert sich darum!
        // Regel, wenn JPA oder die Datenbank die Werte setzen, dann nicht im Konstruktor setzen!
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    // ========================================
    // UserDetails Methoden (PFLICHT für Spring Security!)
    // ========================================

    /**
     * Gibt die Rollen (Authorities) des Users zurück.
     * <p>
     * Spring Security Konvention:
     * - @PreAuthorize("hasRole('ADMIN')") sucht nach "ROLE_ADMIN"
     * - Unser Enum hat: Role.ADMIN
     * - Wir müssen "ROLE_" Prefix hinzufügen!
     * </p>
     * Analogie: Die Zugangsberechtigung auf dem Ausweis
     * - "Mitarbeiter" → darf in Büros
     * - "Security" → darf überall
     *
     * @return Collection mit einer Authority (z.B. "ROLE_ADMIN")
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // "ROLE_" Prefix für Spring Security hinzufügen
        // Aus Role.ADMIN wird "ROLE_ADMIN"
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    /**
     * Gibt das verschlüsselte Passwort zurück.
     * Wichtig: Das ist KEIN Klartext, sondern ein BCrypt Hash!
     * <p>
     * Analogie: Die Geheimzahl auf dem Ausweis-Chip
     * - Nicht lesbar für Menschen
     * - Nur das Lesegerät kann sie prüfen
     * </p>
     * @return BCrypt Hash des Passworts
     */
    @Override
    public String getPassword() {
        return password;
    }

    /**
     * Gibt den Benutzernamen zurück.
     * Analogie: Der Name auf dem Ausweis
     *
     * @return Username des Users
     */
    @Override
    public String getUsername() {
        return username;
    }

    /**
     * Ist das Konto nicht abgelaufen?
     * Wir implementieren KEINE Ablauf-Logik → immer true
     *
     * Analogie: "Gültig bis"-Datum auf dem Ausweis
     * - Wir haben kein Ablaufdatum → immer gültig
     *
     * @return true (Konto läuft nie ab)
     */
    @Override
    public boolean isAccountNonExpired() {
        return true; // Keine Ablauflogik implementiert
    }

    /**
     * Ist das Konto nicht gesperrt?
     * Wir implementieren KEINE Sperr-Logik → immer true
     *
     * Analogie: Ausweis wurde nicht gesperrt
     * - Kein "Gesperrt"-Stempel drauf
     *
     * @return true (Konto ist nie gesperrt)
     */
    @Override
    public boolean isAccountNonLocked() {
        return true; // Keine Sperrlogik implementiert
    }

    /**
     * Ist das Passwort nicht abgelaufen?
     * Wir implementieren KEINE Passwort-Rotation → immer true
     *
     * Analogie: Passwort muss nicht alle 90 Tage geändert werden
     *
     * @return true (Passwort läuft nie ab)
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Passwörter laufen nicht ab
    }

    /**
     * Ist das Konto aktiviert?
     * Wir implementieren KEINE E-Mail-Verifizierung → immer true
     *
     * Analogie: Ausweis ist sofort nach Ausstellung gültig
     * - Keine Aktivierung nötig
     *
     * @return true (Konto ist sofort aktiv)
     */
    @Override
    public boolean isEnabled() {
        return true; // Accounts sind sofort aktiv
    }



    // Getter und Setter

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}