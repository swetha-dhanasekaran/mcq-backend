package com.online.onlineAssessment.responseDto;

import com.online.onlineAssessment.dto.ResultSummary;
import com.online.onlineAssessment.dto.TopicPerformance;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PerformanceAnalyticsResponse {
    private Long userId;
    private Long totalTestsTaken;
    private Double averageScore;
    private Integer totalQuestionsAttempted;
    private Integer totalCorrectAnswers;
    private java.util.List<TopicPerformance> topicWisePerformance;
    private java.util.List<ResultSummary> recentTests;
}