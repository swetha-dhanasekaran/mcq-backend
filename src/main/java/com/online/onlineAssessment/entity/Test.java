package com.online.onlineAssessment.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Test Entity - Stores test metadata
 * Table: test_tbl
 */
@Entity
@Table(name = "test_tbl")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Test {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "test_id")
    private Long testId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "topic", nullable = false, length = 200)
    private String topic;

    @Column(name = "difficulty", nullable = false, length = 20)
    private String difficulty;

    @Column(name = "num_questions", nullable = false)
    private Integer numQuestions;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // One test can have multiple questions
    @OneToMany(mappedBy = "test", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Question> questions;

    // One test can have one result
    @OneToOne(mappedBy = "test", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Result result;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Constructor for test creation
    public Test(User user, String topic, String difficulty, Integer numQuestions) {
        this.user = user;
        this.topic = topic;
        this.difficulty = difficulty;
        this.numQuestions = numQuestions;
    }
}
