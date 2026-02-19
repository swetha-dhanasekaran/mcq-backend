package com.online.onlineAssessment.responseDto;


import com.online.onlineAssessment.dto.QuestionResultDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResultResponse {
    private Long resultId;
    private Long testId;
    private String topic;
    private Integer totalQuestions;
    private Integer correctAnswers;
    private Integer wrongAnswers;
    private Double scorePercentage;
    private String aiFeedback;
    private java.util.List<QuestionResultDTO> questionResults;
    private String message;
}
