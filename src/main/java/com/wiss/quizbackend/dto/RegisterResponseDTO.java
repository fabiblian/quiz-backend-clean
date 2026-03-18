package com.wiss.quizbackend.dto;

/**
 * DTO für Registration Response.
 * <p>
 * Wichtig: KEIN Passwort zurückgeben!
 * Nur Informationen die der Client braucht.
 * </p>
 */
public class RegisterResponseDTO {
    private final Long id;
    private final String username;
    private final String email;
    private final String role;
    private final String message;

    // Constructor
    public RegisterResponseDTO(Long id, String username, String email, String role) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
        this.message = "Registration erfolgreich!";
    }

    // Getters (Setters optional für Response DTOs)
    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public String getMessage() { return message; }
}