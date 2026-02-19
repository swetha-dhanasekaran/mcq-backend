package com.online.onlineAssessment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenerateTestRequest {
    private String topic;
    private String difficulty; // Easy, Medium, Hard
    private Integer numQuestions;
}
