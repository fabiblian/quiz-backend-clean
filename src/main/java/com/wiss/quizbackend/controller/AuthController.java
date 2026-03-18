package com.wiss.quizbackend.controller;

import com.wiss.quizbackend.dto.RegisterRequestDTO;
import com.wiss.quizbackend.dto.RegisterResponseDTO;
import com.wiss.quizbackend.entity.AppUser;
import com.wiss.quizbackend.entity.Role;
import com.wiss.quizbackend.service.AppUserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

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

    public AuthController(AppUserService appUserService) {
        this.appUserService = appUserService;
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
     * GET /api/auth/test
     * Simpler Test-Endpoint
     */
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Auth Controller funktioniert!");
    }
}