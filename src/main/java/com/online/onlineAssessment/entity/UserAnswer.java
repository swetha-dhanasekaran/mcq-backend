package com.online.onlineAssessment.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * UserAnswer Entity - Stores user's selected answers
 * Table: user_answer_tbl
 */
@Entity
@Table(name = "user_answer_tbl")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "answer_id")
    private Long answerId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Column(name = "user_answer", length = 1)
    private String userAnswer;

    @Column(name = "is_correct", nullable = false)
    private Boolean isCorrect;

    @Column(name = "answered_at")
    private LocalDateTime answeredAt;

    @PrePersist
    protected void onCreate() {
        answeredAt = LocalDateTime.now();
    }

    // Constructor for answer submission
    public UserAnswer(Question question, String userAnswer, Boolean isCorrect) {
        this.question = question;
        this.userAnswer = userAnswer;
        this.isCorrect = isCorrect;
    }
}
