package com.online.onlineAssessment.responseDto;

import com.online.onlineAssessment.dto.AIQuestion;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class AIQuestionResponse {
    private java.util.List<AIQuestion> questions;
}
