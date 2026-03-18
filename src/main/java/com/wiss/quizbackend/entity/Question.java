package com.wiss.quizbackend.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "questions")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 128)
    private String question;

    @Column(name = "correct_answer", nullable = false)
    private String correctAnswer;

    @ElementCollection
    @CollectionTable(name = "question_incorrect_answers", joinColumns = @JoinColumn(name = "question_id"))
    @Column(name = "incorrect_answer")
    private List<String> incorrectAnswers;

    @Column(nullable = false, length = 64)
    private String category;

    @Column(nullable = false, length = 32)
    private String difficulty;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by_user_id", nullable = false)
    private AppUser createdBy;

    public Question() {
    }

    // Backward-compatible constructor for existing call sites without createdBy.
    public Question(String question, String correctAnswer,
                    List<String> incorrectAnswers, String category,
                    String difficulty) {
        this(question, correctAnswer, incorrectAnswers, category, difficulty, null);
    }

    public Question(String question, String correctAnswer,
                    List<String> incorrectAnswers, String category,
                    String difficulty, AppUser createdBy) {
        this.question = question;
        this.correctAnswer = correctAnswer;
        this.incorrectAnswers = incorrectAnswers;
        this.category = category;
        this.difficulty = difficulty;
        this.createdBy = createdBy;
    }

    // Backward-compatible constructor for existing call sites without createdBy.
    public Question(Long id, String question, String correctAnswer,
                    List<String> incorrectAnswers, String category,
                    String difficulty) {
        this(id, question, correctAnswer, incorrectAnswers, category, difficulty, null);
    }

    public Question(Long id, String question, String correctAnswer,
                    List<String> incorrectAnswers, String category,
                    String difficulty, AppUser createdBy) {
        this.id = id;
        this.question = question;
        this.correctAnswer = correctAnswer;
        this.incorrectAnswers = incorrectAnswers;
        this.category = category;
        this.difficulty = difficulty;
        this.createdBy = createdBy;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public List<String> getIncorrectAnswers() {
        return incorrectAnswers;
    }

    public void setIncorrectAnswers(List<String> incorrectAnswers) {
        this.incorrectAnswers = incorrectAnswers;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public AppUser getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(AppUser createdBy) {
        this.createdBy = createdBy;
    }
}