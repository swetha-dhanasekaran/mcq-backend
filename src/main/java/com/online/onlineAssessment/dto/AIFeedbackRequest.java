package com.online.onlineAssessment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
class AIFeedbackRequest {
    private String topic;
    private int totalQuestions;
    private int correctAnswers;
    private double scorePercentage;
}

