package com.wiss.quizbackend.controller;

import com.wiss.quizbackend.dto.QuestionDTO;
import com.wiss.quizbackend.dto.QuestionFormDTO;
import com.wiss.quizbackend.entity.Question;
import com.wiss.quizbackend.exception.CategoryNotFoundException;
import com.wiss.quizbackend.exception.DifficultyNotFoundException;
import com.wiss.quizbackend.exception.InvalidQuestionDataException;
import com.wiss.quizbackend.exception.QuestionNotFoundException;
import com.wiss.quizbackend.service.QuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST-Controller für die Verwaltung von Quiz-Fragen.
 * <p>
 * Dieser Controller stellt HTTP-Endpoints für CRUD-Operationen auf Quiz-Fragen bereit.
 * Alle Endpoints arbeiten mit QuestionDTO-Objekten für die Datenübertragung.
 * </p>
 *
 * <p><strong>Verfügbare Operationen:</strong></p>
 * <ul>
 *   <li>Fragen abrufen (alle, nach ID, Kategorie, Schwierigkeit)</li>
 *   <li>Fragen erstellen, aktualisieren und löschen</li>
 *   <li>Erweiterte Funktionen: Suche, Filter, Statistiken</li>
 * </ul>
 *
 * @author Johnny Krup
 * @version 1.0
 * @since 2025-06-01
 * @see QuestionService
 * @see QuestionDTO
 */
@RestController
@RequestMapping("/api/questions")
@Tag(name="Questions", description = "CRUD Operations für Quiz-Fragen")
public class QuestionController {
    private final QuestionService service;

    /**
     * Erstellt einen neuen QuestionController mit dem angegebenen Service.
     *
     * @param service Der QuestionService für die Geschäftslogik
     */
    public QuestionController(QuestionService service) {
        this.service = service;
    }

    /**
     * Ruft alle verfügbaren Quiz-Fragen ab.
     *
     * @return Liste aller Fragen als DTOs
     */
    @GetMapping
    @Operation(
            summary = "Alle Fragen abrufen",
            description = "Gibt alle verfügbaren Quiz-Fragen zurück"
    )
    @ApiResponse(responseCode = "200", description = "Liste erfolgreich abgerufen")
    @PreAuthorize("hasAnyRole('ADMIN', 'PLAYER')")
    public List<QuestionDTO> getAllQuestions() {
        return service.getAllQuestionsAsDTO();
    }

    /**
     * Ruft alle verfügbaren Quiz-Fragen ab.
     *
     * @return Liste aller Fragen als DTOs
     */
    @GetMapping("/all")
    @Operation(
            summary = "Alle Fragen abrufen",
            description = "Gibt alle verfügbaren Quiz-Fragen zurück"
    )
    @ApiResponse(responseCode = "200", description = "Liste erfolgreich abgerufen")
    @PreAuthorize("hasAnyRole('ADMIN', 'PLAYER')")
    public List<QuestionFormDTO> getAllFormQuestions() {
        return service.getAllQuestionsAsFormDTO();
    }

    /**
     * Ruft eine spezifische Frage anhand ihrer ID ab.
     *
     * @param id Die eindeutige ID der gewünschten Frage
     * @return Die gefundene Frage als DTO
     * @throws QuestionNotFoundException wenn keine Frage mit der ID existiert
     * @throws IllegalArgumentException wenn die ID ungültig ist (null oder negativ)
     */
    @GetMapping("/{id}")
    @Operation(
            summary = "Frage nach ID abrufen",
            description = "Gibt eine spezifische Frage basierend auf der ID zurück"
    )
    @ApiResponse(responseCode = "200", description = "Frage gefunden")
    @ApiResponse(responseCode = "404", description = "Frage nicht gefunden")
    @ApiResponse(responseCode = "400", description = "Ungültige ID übergeben")
    @PreAuthorize("hasAnyRole('ADMIN', 'PLAYER')")
    public QuestionDTO getQuestionById(
            @Parameter(
                    description = "Die eindeutige ID der gewünschten Frage",
                    example = "1",
                    required = true
            )
            @PathVariable Long id) {
        return service.getQuestionByIdAsDTO(id);
    }

    /**
     * Ruft eine spezifische Frage anhand ihrer ID ab formatiert für das Frontend-Formular.
     *
     * @param id Die eindeutige ID der gewünschten Frage
     * @return Die gefundene Frage als FormDTO
     * @throws QuestionNotFoundException wenn keine Frage mit der ID existiert
     * @throws IllegalArgumentException wenn die ID ungültig ist (null oder negativ)
     */
    @GetMapping("/{id}/edit")
    @Operation(
            summary = "Frage nach ID abrufen Formular",
            description = "Gibt eine spezifische Frage basierend auf der ID zurück, optimiert für das Frontend-Formular"
    )
    @ApiResponse(responseCode = "200", description = "Frage gefunden")
    @ApiResponse(responseCode = "404", description = "Frage nicht gefunden")
    @ApiResponse(responseCode = "400", description = "Ungültige ID übergeben")
    @PreAuthorize("hasRole('ADMIN')") // ← NEU! Nur ADMIN sieht Edit-Form
    public QuestionFormDTO getQuestionByIdForEdit(@PathVariable Long id) {
        return service.getQuestionByIdAsFormDTO(id);
    }

    /**
     * Ruft alle Fragen einer bestimmten Kategorie ab.
     *
     * @param category Die Kategorie (z.B. "sports", "geography", "science")
     * @return Liste der Fragen der angegebenen Kategorie
     * @throws CategoryNotFoundException wenn die Kategorie ungültig ist
     */
    @GetMapping("/category/{category}")
    @Operation(
            summary = "Fragen nach Kategorie",
            description = "Gibt alle Fragen einer bestimmten Kategorie zurück"
    )
    @ApiResponse(responseCode = "200", description = "Ergebnisse nach Kategorie zurückgegeben")
    @PreAuthorize("hasAnyRole('ADMIN', 'PLAYER')")
    public List<QuestionDTO> getQuestionsByCategory(
            @Parameter(description = "Kategorie", example = "sports", required = true)
            @PathVariable String category) {
        return service.getQuestionsByCategoryAsDTO(category);
    }

    /**
     * Ruft alle Fragen einer bestimmten Schwierigkeit ab.
     *
     * @param difficulty Der Schwierigkeitsgrad ("easy", "medium", "hard")
     * @return Liste der Fragen mit der angegebenen Schwierigkeit
     * @throws DifficultyNotFoundException wenn der Schwierigkeitsgrad ungültig ist
     */
    @GetMapping("/difficulty/{difficulty}")
    @Operation(
            summary = "Fragen nach Schwierigkeit",
            description = "Gibt alle Fragen einer bestimmten Schwierigkeit zurück"
    )
    @ApiResponse(responseCode = "200", description = "Ergebnisse nach Schwierigkeit zurückgegeben")
    @PreAuthorize("hasAnyRole('ADMIN', 'PLAYER')")
    public List<QuestionDTO> getQuestionsByDifficulty(
            @Parameter(description = "Schwierigkeit", example = "easy", required = true)
            @PathVariable String difficulty) {
        return service.getQuestionsByDifficultyAsDTO(difficulty);
    }

    /**
     * Erstellt eine neue Quiz-Frage.
     * <p>
     * Validiert die Eingabedaten automatisch durch @Valid-Annotation
     * und gibt die erstellte Frage mit generierter ID zurück.
     * </p>
     *
     * @param questionDTO Die zu erstellende Frage (ID wird ignoriert)
     * @return Die erstellte Frage mit generierter ID*
     * @throws InvalidQuestionDataException bei ungültigen Daten
     */
    @PostMapping
    @Operation(
            summary = "Neue Frage erstellen",
            description = "Erstellt eine neue Quiz-Frage"
    )
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponse(responseCode = "201", description = "Frage erfolgreich erstellt")
    @ApiResponse(responseCode = "400", description = "Ungültige Eingabedaten")
    @PreAuthorize("hasRole('ADMIN')") // ← NEU! Nur Admins dürfen Fragen erstellen
    public QuestionDTO createQuestion(
            @Parameter(description = "Frage-Daten", required = true)
            @Valid @RequestBody QuestionDTO questionDTO){ // ← @Valid hinzufügen!
        return service.createQuestion(questionDTO);
    }

    /**
     * Erstellt eine neue Quiz-Frage.
     * <p>
     * Validiert die Eingabedaten automatisch durch @Valid-Annotation
     * und gibt die erstellte Frage mit generierter ID zurück.
     * </p>
     *
     * @param question die Frage-Daten aus dem Formular
     * @return Die erstellte Frage mit generierter ID*
     * @throws InvalidQuestionDataException bei ungültigen Daten
     */
    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Neue Frage erstellen über Frontend-Form")
    @ApiResponse(responseCode = "201", description = "Frage erfolgreich erstellt")
    @ApiResponse(responseCode = "400", description = "Ungültige Eingabedaten")
    @PreAuthorize("hasRole('ADMIN')") // ← NEU! Nur Admins dürfen Fragen erstellen
    public QuestionFormDTO createQuestionFromForm(
            @Parameter(description = "Frage-Daten", required = true)
            @Valid @RequestBody Question question) {
        return service.createQuestionFromForm(question);
    }


    /**
     * Aktualisiert eine bestehende Quiz-Frage.
     *
     * @param id Die ID der zu aktualisierenden Frage
     * @param questionDTO Die aktualisierten Frage-Daten
     * @return Die aktualisierte Frage
     * @throws QuestionNotFoundException wenn die Frage nicht existiert*
     */
    @PutMapping("/{id}")
    @Operation(
            summary = "Frage aktualisieren",
            description = "Aktualisiert eine bestehende Frage"
    )
    @ApiResponse(responseCode = "200", description = "Frage erfolgreich editiert")
    @ApiResponse(responseCode = "400", description = "Ungültige Eingabedaten")
    @PreAuthorize("hasRole('ADMIN')") // ← NEU! Nur Admins dürfen Fragen editieren
    public QuestionDTO updateQuestion(
            @Parameter(description = "ID der zu aktualisierenden Frage", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody QuestionDTO questionDTO){
        return service.updateQuestion(id, questionDTO);
    }

    /**
     * Aktualisiert eine bestehende Quiz-Frage.
     *
     * @param id Die ID der zu aktualisierenden Frage
     * @param question Die aktualisierten Frage-Daten des Formulars
     * @return Die aktualisierte Frage
     * @throws QuestionNotFoundException wenn die Frage nicht existiert*
     */
    @PutMapping("/{id}/update")
    @Operation(
            summary = "Frage aktualisieren über Formular",
            description = "Aktualisiert eine bestehende Frage über das Formular im Frontend"
    )
    @ApiResponse(responseCode = "200", description = "Frage erfolgreich editiert")
    @ApiResponse(responseCode = "400", description = "Ungültige Eingabedaten")
    @PreAuthorize("hasRole('ADMIN')") // ← NEU! Nur Admins dürfen Fragen editieren
    public QuestionFormDTO updateQuestionFromForm(
            @Parameter(description = "ID der zu aktualisierenden Frage", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody Question question) {
        return service.updateQuestionFromForm(id, question);
    }

    /**
     * Löscht eine Quiz-Frage permanent.
     * <p>
     * <strong>Warnung:</strong> Diese Aktion kann nicht rückgängig gemacht werden!
     * </p>
     *
     * @param id Die ID der zu löschenden Frage
     * @throws QuestionNotFoundException wenn die Frage nicht existiert
     */
    @DeleteMapping("/{id}")
    @Operation(
            summary = "Frage löschen",
            description = "Löscht eine Frage permanent. Diese Aktion kann nicht rückgängig gemacht werden!"
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiResponse(responseCode = "200", description = "Frage erfolgreich gelöscht")
    @ApiResponse(responseCode = "404", description = "Frage nicht gefunden")
    @ApiResponse(responseCode = "409", description = "Frage wird noch in aktiven Quiz verwendet")
    @PreAuthorize("hasRole('ADMIN')") // ← NEU! Nur Admins dürfen Fragen löschen
    public void deleteQuestion(
            @Parameter(description = "ID der zu löschenden Frage", example = "1")
            @PathVariable Long id) {
        service.deleteQuestion(id);
    }

    /**
     * Ruft Fragen basierend auf kombinierten Filtern ab.
     * <p>
     * Unterstützt Filterung nach Kategorie und/oder Schwierigkeit.
     * Wenn keine Filter angegeben werden, werden alle Fragen zurückgegeben.
     * </p>
     *
     * @param category Die Kategorie (optional)
     * @param difficulty Der Schwierigkeitsgrad (optional)
     * @return Liste der gefilterten Fragen
     */
    @GetMapping("/filter")
    @PreAuthorize("hasAnyRole('ADMIN', 'PLAYER')")
    public List<QuestionDTO> getQuestionsByFilter(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String difficulty) {

        if (category != null && difficulty != null) {
            return service.getQuestionsByCategoryAndDifficulty(category, difficulty);
        } else if (category != null) {
            return service.getQuestionsByCategoryAsDTO(category);
        } else if (difficulty != null) {
            return service.getQuestionsByDifficultyAsDTO(difficulty);
        } else {
            return service.getAllQuestionsAsDTO();
        }
    }

    /**
     * Durchsucht Fragen basierend auf einem Suchbegriff.
     * <p>
     * Sucht im Fragetext und in den Antworten nach dem angegebenen Begriff.
     * Die Suche ist case-insensitive.
     * </p>
     *
     * @param q Der Suchbegriff
     * @return Liste der Fragen, die den Suchbegriff enthalten
     * @throws IllegalArgumentException wenn der Suchbegriff leer ist
     */
    @GetMapping("/search")
    @Operation(
            summary = "Fragen durchsuchen",
            description = "Sucht Fragen basierend auf einem Suchbegriff"
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'PLAYER')")
    public List<QuestionDTO> searchQuestions(
            @Parameter(description = "Suchbegriff", example = "Schweiz")
            @RequestParam String q) {
        return service.searchQuestions(q);
    }

    /**
     * Zählt die Anzahl Fragen einer bestimmten Kategorie.
     *
     * @param category Die Kategorie
     * @return Anzahl der Fragen in der Kategorie
     * @throws CategoryNotFoundException wenn die Kategorie ungültig ist
     */
    @GetMapping("/stats/category/{category}")
    @Operation(
            summary = "Anzahl Fragen nach Kategorie",
            description = "Zählt die Anzahl Fragen einer bestimmten Kategorie zusammen"
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'PLAYER')")
    public long getQuestionCountByCategory(
            @Parameter(description = "Kategorie", example = "history")
            @PathVariable String category) {
        return service.getQuestionCountByCategory(category);
    }

    /**
     * Ruft eine zufällige Auswahl von Fragen einer Kategorie ab.
     * <p>
     * Nützlich für Quiz-Spiele, um verschiedene Fragen pro Runde zu erhalten.
     * </p>
     *
     * @param category Die Kategorie der Fragen
     * @param limit Die maximale Anzahl Fragen (Standard: 5)
     * @return Liste zufälliger Fragen der angegebenen Kategorie
     * @throws CategoryNotFoundException wenn die Kategorie ungültig ist
     * @throws IllegalArgumentException wenn limit kleiner als 1 ist
     */
    @GetMapping("/random")
    @Operation(
            summary = "Zufällige Anzahl Fragen nach Kategorie",
            description = "Gibt eine zufällige Anzahl an Fragen zurück"
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'PLAYER')")
    public List<QuestionDTO> getRandomQuestions(
            @Parameter(description = "Kategorie", example = "movies")
            @RequestParam String category,
            @Parameter(description = "Anzahl", example = "3")
            @RequestParam(defaultValue = "5") int limit) {
        if(category != null){
            return service.getRandomQuestionsByCategory(category, limit);
        } else {
            return service.getRandomQuestions(limit);
        }
    }

    /**
     * Zählt die Gesamtanzahl aller verfügbaren Fragen.
     *
     * @return Gesamtanzahl der Fragen in der Datenbank
     */
    @GetMapping("/count")
    @PreAuthorize("hasAnyRole('ADMIN', 'PLAYER')")
    @Operation(
            summary = "Anzahl aller Fragen",
            description = "Gibt die Anzahl aller verfügbaen Fragen zurück"
    )
    public long getQuestionsCount() {
        return service.getTotalQuestionsCount();
    }
}