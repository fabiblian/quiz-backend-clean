package com.wiss.quizbackend.controller;

import com.wiss.quizbackend.entity.Question;
import com.wiss.quizbackend.repository.QuestionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet
        .AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request
        .MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result
        .MockMvcResultMatchers.*;

/**
 * Integration Tests für die Security der Question-Endpoints.
 * Diese Tests prüfen:
 * - Authentifizierung: Können nur eingeloggte User zugreifen?
 * - Autorisierung: Haben die Rollen die richtigen Berechtigungen?
 * - HTTP Status Codes: Werden die richtigen Statuscodes zurückgegeben?
 */
@SpringBootTest
@AutoConfigureMockMvc
public class QuestionControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private QuestionRepository questionRepository;

    @BeforeEach
    void setUp() {
        // Testdaten vorbereiten
        questionRepository.deleteAll();

        Question testQuestion = new Question(
                "Was ist 2 + 2?",
                "4",
                List.of("3", "5", "6"),
                "mathematics",
                "easy",
                null // <- createdBy = null für Test-Daten
        );
        questionRepository.save(testQuestion);
    }

    // ==================== Tests ohne Authentication ====================

    @Test
    void getAllQuestions_withoutAuth_shouldReturn401() throws Exception {
        // Versuche Fragen abzurufen OHNE eingeloggt zu sein
        mockMvc.perform(get("/api/questions"))
                .andExpect(status().isForbidden());  // 403 erwartet
    }

    @Test
    void createQuestion_withoutAuth_shouldReturn401() throws Exception {
        // Versuche Frage zu erstellen OHNE eingeloggt zu sein
        String questionJson = """
                {
                    "question": "Test Frage?",
                    "correctAnswer": "Test",
                    "incorrectAnswers": ["A", "B", "C"],
                    "category": "sports",
                    "difficulty": "easy"
                }
                """;

        mockMvc.perform(post("/api/questions/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(questionJson))
                .andExpect(status().isForbidden());  // 403 erwartet
    }

    // ==================== Tests als PLAYER ====================

    @Test
    @WithMockUser(username = "player1", roles = {"PLAYER"})
    void getAllQuestions_asPlayer_shouldReturn200() throws Exception {
        // PLAYER soll Fragen lesen können
        mockMvc.perform(get("/api/questions"))
                .andExpect(status().isOk())  // 200 OK erwartet
                .andExpect(jsonPath("$").isArray());  // Array zurückgegeben
    }

    @Test
    @WithMockUser(username = "player1", roles = {"PLAYER"})
    void getRandomQuestions_asPlayer_shouldReturn200() throws Exception {
        // PLAYER soll zufällige Fragen für Quiz abrufen können
        mockMvc.perform(get("/api/questions/random")
                        .param("category", "sports")
                        .param("limit", "5"))
                .andExpect(status().isOk());
    }


    @Test
    @WithMockUser(username = "player1", roles = {"PLAYER"})
    void createQuestion_asPlayer_shouldReturn403() throws Exception {
        // PLAYER soll KEINE Fragen erstellen können!
        String questionJson = """
                {
                    "question": "Wie heisst die Hauptstadt der Schweiz?",
                    "correctAnswer": "Bern",
                    "incorrectAnswers": ["Zürich", "Basel", "Genf"],
                    "category": "geography",
                    "difficulty": "easy"
                }
                """;

        mockMvc.perform(post("/api/questions/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(questionJson))
                .andExpect(status().isForbidden());  // 403 Forbidden erwartet!
    }

    @Test
    @WithMockUser(username = "player1", roles = {"PLAYER"})
    void deleteQuestion_asPlayer_shouldReturn403() throws Exception {
        // PLAYER soll KEINE Fragen löschen können!
        Long questionId = questionRepository.findAll().getFirst().getId();

        mockMvc.perform(delete("/api/questions/" + questionId))
                .andExpect(status().isForbidden());  // 403 Forbidden erwartet!
    }

    // ==================== Tests als ADMIN ====================

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getAllQuestions_asAdmin_shouldReturn200() throws Exception {
        // ADMIN soll Fragen lesen können
        mockMvc.perform(get("/api/questions"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void createQuestion_asAdmin_shouldReturn201() throws Exception {
        // ADMIN soll Fragen erstellen können!
        String questionJson = """
                {
                    "question": "Wie heisst die Hauptstadt von Frankreich?",
                    "correctAnswer": "Paris",
                    "incorrectAnswers": ["London", "Berlin", "Rom"],
                    "category": "geography",
                    "difficulty": "easy"
                }
                """;

        mockMvc.perform(post("/api/questions/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(questionJson))
                .andExpect(status().isCreated())  // 201 Created erwartet!
                .andExpect(jsonPath("$.question")
                        .value("Wie heisst die Hauptstadt von Frankreich?"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deleteQuestion_asAdmin_shouldReturn204() throws Exception {
        // ADMIN soll Fragen löschen können!
        Long questionId = questionRepository.findAll().getFirst().getId();

        mockMvc.perform(delete("/api/questions/" + questionId))
                .andExpect(status().isNoContent());  // 204 No Content erwartet!
    }

}