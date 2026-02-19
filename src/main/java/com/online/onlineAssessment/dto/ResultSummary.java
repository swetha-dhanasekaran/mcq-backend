package com.online.onlineAssessment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResultSummary {
    private Long testId;
    private String topic;
    private String difficulty;
    private Double scorePercentage;
    private java.time.LocalDateTime createdAt;
}