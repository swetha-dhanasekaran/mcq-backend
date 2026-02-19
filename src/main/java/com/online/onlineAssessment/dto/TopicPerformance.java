package com.online.onlineAssessment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopicPerformance {
    private String topic;
    private Long testsCount;
    private Double averageScore;
    private String performance; // Excellent, Good, Average, Needs Improvement
}