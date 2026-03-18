package com.wiss.quizbackend.controller;

import com.wiss.quizbackend.dto.LoginRequestDTO;
import com.wiss.quizbackend.dto.LoginResponseDTO;
import com.wiss.quizbackend.dto.RegisterRequestDTO;
import com.wiss.quizbackend.dto.RegisterResponseDTO;
import com.wiss.quizbackend.entity.AppUser;
import com.wiss.quizbackend.entity.Role;
import com.wiss.quizbackend.service.AppUserService;
import com.wiss.quizbackend.service.JwtService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.Option;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * REST Controller für Authentication.
 * <p>
 * Controller Layer Verantwortungen:
 * - HTTP Request/Response Handling
 * - Input Validation (@Valid)
 * - Status Codes
 * - Exception Handling
 * </p>
 *
 * @RestController = @Controller + @ResponseBody
 *                   Alle Methoden returnieren JSON
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173") // Für React Frontend
public class AuthController {

    private final AppUserService appUserService;
    private final JwtService jwtService;

    public AuthController(AppUserService appUserService, JwtService jwtService) {
        this.appUserService = appUserService;
        this.jwtService = jwtService;
    }

    /**
     * POST /api/auth/register
     *
     * @Valid triggert Bean Validation (siehe RegisterRequest)
     * @RequestBody parsed JSON zu Java Object
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequestDTO request) {
        try {
            // Service Layer aufrufen
            AppUser newUser = appUserService.registerUser(
                    request.getUsername(),
                    request.getEmail(),
                    request.getPassword(),
                    Role.PLAYER  // Default Role für neue User
            );

            // Response DTO erstellen
            RegisterResponseDTO response = new RegisterResponseDTO(
                    newUser.getId(),
                    newUser.getUsername(),
                    newUser.getEmail(),
                    newUser.getRole().name()
            );

            // HTTP 200 OK mit Response Body
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            // HTTP 400 Bad Request bei Validation Errors
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);

        } catch (Exception e) {
            // HTTP 500 bei unerwarteten Fehlern
            Map<String, String> error = new HashMap<>();
            error.put("error", "Registrierung fehlgeschlagen");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * POST /api/auth/login
     * <p>
     * Authentifiziert einen User und gibt JWT Token zurück.
     * Request Body Example:
     * {
     *   "usernameOrEmail": "maxmuster",
     *   "password": "test123"
     * }
     * </p>
     * <p>
     * Success Response (200):
     * {
     *   "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
     *   "tokenType": "Bearer",
     *   "userId": 1,
     *   "username": "maxmuster",
     *   "email": "max@example.com",
     *   "role": "PLAYER",
     *   "expiresIn": 86400000
     * }
     *</p>
     * <p>
     * Error Response (401):
     * {
     *   "error": "Ungültige Anmeldedaten"
     * }
     * </p>
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO requestDTO) {
        try {
            // 1. User finden (Username oder Email)
            Optional<AppUser> userOpt;

            // Prüfen, ob E-Mail oder Username
            if(requestDTO.getUsernameOrEmail().contains("@")) {
                // Hat @ → ist eine E-Mail
                userOpt = appUserService.findByEmail(requestDTO.getUsernameOrEmail());
            } else {
                // Hat KEIN @ → ist ein Username
                userOpt = appUserService.findByUsername(requestDTO.getUsernameOrEmail());
            }

            // User existiert nicht
            if(userOpt.isEmpty()) {
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Ungültige Anmeldedaten"));
            }

            AppUser user = userOpt.get();

            // 2. Passwort prüfen mit authenticateUser
            Optional<AppUser> authenticatedUser = appUserService
                    .authenticateUser(user.getUsername(), requestDTO.getPassword());

            if(authenticatedUser.isEmpty()) {
                // Passwort falsch
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Ungültige Anmeldedaten"));
            }

            // 3. JWT Token generieren
            String token = jwtService.generateToken(user.getUsername(), user.getRole().name());

            // 4. Response DTO erstellen
            LoginResponseDTO responseDTO = new LoginResponseDTO(
                    token,
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getRole().name(),
                    86400000L // 24 Stunden in ms
            );

            // 5. Success Response
            return ResponseEntity.ok(responseDTO);

        } catch (Exception e) {
            // Unerwarteter Fehler
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Ein Fehler ist aufgetreten: " + e.getMessage()));
        }
    }

    /**
     * GET /api/auth/test
     * Simpler Test-Endpoint
     */
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Auth Controller funktioniert!");
    }
}