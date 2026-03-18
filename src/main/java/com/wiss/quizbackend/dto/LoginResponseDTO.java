package com.wiss.quizbackend.dto;

/**
 * DTO für Login Response.
 * <p>
 * Enthält:
 * - JWT Token für Authentication
 * - User Informationen (ohne Passwort!)
 * - Token Type (Bearer)
 * - Expiration Time
 * </p>
 *
 * Wichtig:
 * - IMMUTABLE (alle Felder final)
 * - Kein Passwort zurückgeben (Security!)
 * - Token Type "Bearer" ist Standard für JWT
 */
public class LoginResponseDTO {

    private final String token;
    private final String tokenType = "Bearer";
    private final Long userId;
    private final String username;
    private final String email;
    private final String role;
    private final long expiresIn;  // in Millisekunden

    public LoginResponseDTO(String token, Long userId, String username, String email, String role, long expiresIn) {
        this.token = token;
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.role = role;
        this.expiresIn = expiresIn;
    }

    // Nur Getters (Immutable!)
    public String getToken() {
        return token;
    }

    public String getTokenType() {
        return tokenType;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    /**
     * toString mit HIDDEN Token (Security!)
     * Token sollte nicht geloggt werden - könnte gestohlen werden!
     */
    @Override
    public String toString() {
        return "LoginResponseDTO{" +
                "token='[HIDDEN]'" + '\'' +
                ", tokenType='" + tokenType + '\'' +
                ", userId=" + userId +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                ", expiresIn=" + expiresIn +
                '}';
    }
}