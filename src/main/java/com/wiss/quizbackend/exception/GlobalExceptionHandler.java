package com.wiss.quizbackend.exception;

import com.wiss.quizbackend.dto.ErrorResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ErrorResponseDTO> handleAccessDenied(
            AuthorizationDeniedException ex, WebRequest request) {

        ErrorResponseDTO error = new ErrorResponseDTO(
                "ACCESS_DENIED",
                "Zugriff verweigert. Sie haben nicht die erforderlichen Berechtigungen.",
                403,
                extractPath(request)
        );

        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(QuestionNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleQuestionNotFound(
            QuestionNotFoundException ex, WebRequest request){

        ErrorResponseDTO error = new ErrorResponseDTO(
                "QUESTION_NOT_FOUND",
                "Frage mit ID " + ex.getQuestionId() + " wurde nicht gefunden",
                404,
                extractPath(request)
        );

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidQuestionDataException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvalidQuestionData(
            InvalidQuestionDataException ex, WebRequest request){

        ErrorResponseDTO error = new ErrorResponseDTO(
                "INVALID_QUESTION_DATA",
                ex.getMessage(),
                400,
                extractPath(request)
        );

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleCategoryNotFound(
            CategoryNotFoundException ex, WebRequest request) {

        ErrorResponseDTO error = new ErrorResponseDTO(
                "INVALID_CATEGORY",
                "Kategorie '" + ex.getCategory() + "' ist nicht gültig. " +
                        "Verfügbare Kategorien: sports, games, movies, geography, science, history",
                400,
                extractPath(request)
        );

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DifficultyNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleDifficultyNotFound(
            DifficultyNotFoundException ex, WebRequest request) {

        ErrorResponseDTO error = new ErrorResponseDTO(
                "INVALID_DIFFICULTY",
                "Schwierigkeitsgrad '" + ex.getDiffculty() + "' ist nicht gültig. " +
                        "Verfügbare Schwierigkietsgrade: easy, medium, hard",
                400,
                extractPath(request)
        );

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDTO> handleIllegalArgument(
            IllegalArgumentException ex, WebRequest request) {

        ErrorResponseDTO error = new ErrorResponseDTO(
                "INVALID_INPUT",
                ex.getMessage(),
                400,
                extractPath(request)
        );

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGeneralException(
            Exception ex, WebRequest request) {

        // Log the actual exception for debugging (nicht an Frontend senden!)
        System.err.println("Unhandled exception: " + ex.getMessage());
        ex.printStackTrace();

        ErrorResponseDTO error = new ErrorResponseDTO(
                "INTERNAL_SERVER_ERROR",
                "Ein unerwarteter Fehler ist aufgetreten. Bitte versuchen Sie es später erneut.",
                500,
                extractPath(request)
        );

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidationErrors(
            MethodArgumentNotValidException ex, WebRequest request) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        StringBuilder message = new StringBuilder("Validierungsfehler: ");
        errors.forEach((field, error) -> message.append(field).append(" - ").append(error).append("; "));

        ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO(
                "VALIDATION_ERROR",
                message.toString(),
                400,
                extractPath(request)
        );
        return new ResponseEntity<>(errorResponseDTO, HttpStatus.BAD_REQUEST);
    }

    /**
     * Extrahiert den API-Pfad aus dem WebRequest.
     * @param request
     * @return
     */
    private String extractPath(WebRequest request){
        return request.getDescription(false).replace("uri=", "");
    }
}