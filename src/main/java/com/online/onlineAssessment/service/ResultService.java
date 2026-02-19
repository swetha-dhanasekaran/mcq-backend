package com.online.onlineAssessment.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.online.onlineAssessment.dto.AnswerDTO;
import com.online.onlineAssessment.dto.QuestionResultDTO;
import com.online.onlineAssessment.dto.SubmitAnswersRequest;
import com.online.onlineAssessment.entity.Question;
import com.online.onlineAssessment.entity.Result;
import com.online.onlineAssessment.entity.Test;
import com.online.onlineAssessment.entity.UserAnswer;
import com.online.onlineAssessment.repository.QuestionRepository;
import com.online.onlineAssessment.repository.ResultRepository;
import com.online.onlineAssessment.repository.TestRepository;
import com.online.onlineAssessment.repository.UserAnswerRepository;
import com.online.onlineAssessment.responseDto.ResultResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResultService {

    private final TestRepository testRepository;
    private final QuestionRepository questionRepository;
    private final UserAnswerRepository userAnswerRepository;
    private final ResultRepository resultRepository;
    private final GroqAIService aiService;  // Use GroqAIService

    @Transactional
    public ResultResponse submitAnswers(SubmitAnswersRequest request) {
        log.info("Submitting answers for test: {}", request.getTestId());

        Test test = testRepository.findById(request.getTestId())
            .orElseThrow(() -> new RuntimeException("Test not found"));

        List<Question> questions = questionRepository.findByTest_TestId(request.getTestId());
        
        if (questions.isEmpty()) {
            throw new RuntimeException("No questions found for this test");
        }

        Map<Long, Question> questionMap = new HashMap<>();
        for (Question question : questions) {
            questionMap.put(question.getQuestionId(), question);
        }

        int correctCount = 0;
        int wrongCount = 0;
        List<UserAnswer> userAnswers = new ArrayList<>();

        for (AnswerDTO answerDTO : request.getAnswers()) {
            Question question = questionMap.get(answerDTO.getQuestionId());
            
            if (question == null) {
                log.warn("Question not found: {}", answerDTO.getQuestionId());
                continue;
            }

            boolean isCorrect = question.getCorrectAnswer()
                .equalsIgnoreCase(answerDTO.getUserAnswer());

            if (isCorrect) {
                correctCount++;
            } else {
                wrongCount++;
            }

            UserAnswer userAnswer = new UserAnswer();
            userAnswer.setQuestion(question);
            userAnswer.setUserAnswer(answerDTO.getUserAnswer());
            userAnswer.setIsCorrect(isCorrect);
            userAnswers.add(userAnswer);
        }

        userAnswerRepository.saveAll(userAnswers);

        int totalQuestions = questions.size();
        double scorePercentage = (double) correctCount / totalQuestions * 100;

        String aiFeedback = aiService.generateFeedback(
            test.getTopic(),
            totalQuestions,
            correctCount,
            scorePercentage
        );

        Result result = new Result();
        result.setTest(test);
        result.setTotalQuestions(totalQuestions);
        result.setCorrectAnswers(correctCount);
        result.setWrongAnswers(wrongCount);
        result.setScorePercentage(scorePercentage);
        result.setAiFeedback(aiFeedback);
        result = resultRepository.save(result);

        log.info("Test evaluated. Score: {}%, Correct: {}/{}", 
            String.format("%.2f", scorePercentage), correctCount, totalQuestions);

        List<QuestionResultDTO> questionResults = prepareQuestionResults(
            questions, 
            request.getAnswers()
        );

        return new ResultResponse(
            result.getResultId(),
            test.getTestId(),
            test.getTopic(),
            totalQuestions,
            correctCount,
            wrongCount,
            scorePercentage,
            aiFeedback,
            questionResults,
            "Test submitted successfully"
        );
    }

    public ResultResponse getResult(Long testId) {
        Result result = resultRepository.findByTest_TestId(testId)
            .orElseThrow(() -> new RuntimeException("Result not found for this test"));

        Test test = result.getTest();
        List<Question> questions = questionRepository.findByTest_TestId(testId);
        List<UserAnswer> userAnswers = userAnswerRepository.findByQuestion_Test_TestId(testId);

        Map<Long, UserAnswer> answerMap = userAnswers.stream()
            .collect(Collectors.toMap(
                ua -> ua.getQuestion().getQuestionId(),
                ua -> ua
            ));

        List<QuestionResultDTO> questionResults = questions.stream()
            .map(q -> {
                UserAnswer ua = answerMap.get(q.getQuestionId());
                return new QuestionResultDTO(
                    q.getQuestionId(),
                    q.getQuestionText(),
                    q.getOptionA(),
                    q.getOptionB(),
                    q.getOptionC(),
                    q.getOptionD(),
                    q.getCorrectAnswer(),
                    ua != null ? ua.getUserAnswer() : null,
                    ua != null ? ua.getIsCorrect() : false,
                    q.getExplanation()
                );
            })
            .collect(Collectors.toList());

        return new ResultResponse(
            result.getResultId(),
            test.getTestId(),
            test.getTopic(),
            result.getTotalQuestions(),
            result.getCorrectAnswers(),
            result.getWrongAnswers(),
            result.getScorePercentage(),
            result.getAiFeedback(),
            questionResults,
            "Result retrieved successfully"
        );
    }

    private List<QuestionResultDTO> prepareQuestionResults(
            List<Question> questions, 
            List<AnswerDTO> submittedAnswers) {
        
        Map<Long, String> answerMap = submittedAnswers.stream()
            .collect(Collectors.toMap(
                AnswerDTO::getQuestionId,
                AnswerDTO::getUserAnswer
            ));

        return questions.stream()
            .map(q -> {
                String userAnswer = answerMap.get(q.getQuestionId());
                boolean isCorrect = q.getCorrectAnswer().equalsIgnoreCase(userAnswer);
                
                return new QuestionResultDTO(
                    q.getQuestionId(),
                    q.getQuestionText(),
                    q.getOptionA(),
                    q.getOptionB(),
                    q.getOptionC(),
                    q.getOptionD(),
                    q.getCorrectAnswer(),
                    userAnswer,
                    isCorrect,
                    q.getExplanation()
                );
            })
            .collect(Collectors.toList());
    }
}