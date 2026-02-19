package com.online.onlineAssessment.responseDto;

import com.online.onlineAssessment.dto.QuestionDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestResponse {
    private Long testId;
    private String topic;
    private String difficulty;
    private Integer numQuestions;
    private java.util.List<QuestionDTO> questions;
    private String message;
}

