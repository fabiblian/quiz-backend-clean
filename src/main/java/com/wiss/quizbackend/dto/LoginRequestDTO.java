package com.wiss.quizbackend.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO für Login Requests.
 * <p>
 * Zweck:
 * - Input von Login-Formular empfangen
 * - Basic Validation durchführen
 * - Flexibel: Username ODER Email akzeptieren
 * </p>
 * Warum "usernameOrEmail"?
 * - User können sich mit beidem einloggen
 * - Einfacher für den User (muss sich nicht merken, was er verwendet hat)
 * - Backend prüft dann, ob es Email oder Username ist
 */
public class LoginRequestDTO {

    @NotBlank(message = "Username oder Email ist erforderlich")
    private String usernameOrEmail;

    @NotBlank(message = "Passwort ist erforderlich")
    private String password;

    // Default Constructor für JSON Deserialization
    public LoginRequestDTO() {}

    // Constructor mit allen Feldern (für Tests)
    public LoginRequestDTO(String usernameOrEmail, String password) {
        this.usernameOrEmail = usernameOrEmail;
        this.password = password;
    }

    // Getters und Setters
    public String getUsernameOrEmail() {
        return usernameOrEmail;
    }



    public void setUsernameOrEmail(String usernameOrEmail) {
        this.usernameOrEmail = usernameOrEmail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * toString OHNE Passwort (Security!)
     * <p>
     * NIEMALS Passwörter in Logs!
     * </p>
     */
    @Override
    public String toString() {
        return "LoginRequestDTO{" +
                "usernameOrEmail='" + usernameOrEmail + '\'' +
                ", password='[HIDDEN]'" +
                '}';
    }
}