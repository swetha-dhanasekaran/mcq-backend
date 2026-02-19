package com.online.onlineAssessment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
class AIQuestionRequest {
    private String topic;
    private String difficulty;
    private int numQuestions;
}