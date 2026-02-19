package com.online.onlineAssessment.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.online.onlineAssessment.dto.AIQuestion;
import com.online.onlineAssessment.dto.GenerateTestRequest;
import com.online.onlineAssessment.dto.QuestionDTO;
import com.online.onlineAssessment.dto.ResultSummary;
import com.online.onlineAssessment.entity.Question;
import com.online.onlineAssessment.entity.Test;
import com.online.onlineAssessment.entity.User;
import com.online.onlineAssessment.repository.QuestionRepository;
import com.online.onlineAssessment.repository.TestRepository;
import com.online.onlineAssessment.repository.UserRepository;
import com.online.onlineAssessment.responseDto.AIQuestionResponse;
import com.online.onlineAssessment.responseDto.TestResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class TestService {

    private final TestRepository testRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final GroqAIService aiService;  // Use GroqAIService

    @Transactional
    public TestResponse generateTest(Long userId, GenerateTestRequest request) {
        log.info("Generating test for user: {}, topic: {}, difficulty: {}", 
            userId, request.getTopic(), request.getDifficulty());

        validateTestRequest(request);

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        Test test = new Test();
        test.setUser(user);
        test.setTopic(request.getTopic());
        test.setDifficulty(request.getDifficulty());
        test.setNumQuestions(request.getNumQuestions());
        test = testRepository.save(test);

        AIQuestionResponse aiResponse = aiService.generateQuestions(
            request.getTopic(),
            request.getDifficulty(),
            request.getNumQuestions()
        );

        List<Question> questions = new ArrayList<>();
        for (AIQuestion aiQuestion : aiResponse.getQuestions()) {
            Question question = new Question();
            question.setTest(test);
            question.setQuestionText(aiQuestion.getQuestion());
            question.setOptionA(aiQuestion.getOptionA());
            question.setOptionB(aiQuestion.getOptionB());
            question.setOptionC(aiQuestion.getOptionC());
            question.setOptionD(aiQuestion.getOptionD());
            question.setCorrectAnswer(aiQuestion.getCorrectAnswer());
            question.setExplanation(aiQuestion.getExplanation());
            questions.add(question);
        }

        questions = questionRepository.saveAll(questions);

        log.info("Test generated successfully. Test ID: {}, Questions: {}", 
            test.getTestId(), questions.size());

        List<QuestionDTO> questionDTOs = questions.stream()
            .map(q -> new QuestionDTO(
                q.getQuestionId(),
                q.getQuestionText(),
                q.getOptionA(),
                q.getOptionB(),
                q.getOptionC(),
                q.getOptionD(),
                null,
                null
            ))
            .collect(Collectors.toList());

        return new TestResponse(
            test.getTestId(),
            test.getTopic(),
            test.getDifficulty(),
            test.getNumQuestions(),
            questionDTOs,
            "Test generated successfully"
        );
    }

    public TestResponse getTest(Long testId) {
        Test test = testRepository.findById(testId)
            .orElseThrow(() -> new RuntimeException("Test not found"));

        List<Question> questions = questionRepository.findByTest_TestId(testId);

        List<QuestionDTO> questionDTOs = questions.stream()
            .map(q -> new QuestionDTO(
                q.getQuestionId(),
                q.getQuestionText(),
                q.getOptionA(),
                q.getOptionB(),
                q.getOptionC(),
                q.getOptionD(),
                null,
                null
            ))
            .collect(Collectors.toList());

        return new TestResponse(
            test.getTestId(),
            test.getTopic(),
            test.getDifficulty(),
            test.getNumQuestions(),
            questionDTOs,
            "Test retrieved successfully"
        );
    }

    public List<ResultSummary> getUserTests(Long userId) {
        List<Test> tests = testRepository.findByUser_UserIdOrderByCreatedAtDesc(userId);

        return tests.stream()
            .map(test -> new ResultSummary(
                test.getTestId(),
                test.getTopic(),
                test.getDifficulty(),
                test.getResult() != null ? test.getResult().getScorePercentage() : null,
                test.getCreatedAt()
            ))
            .collect(Collectors.toList());
    }

    private void validateTestRequest(GenerateTestRequest request) {
        if (request.getTopic() == null || request.getTopic().trim().isEmpty()) {
            throw new IllegalArgumentException("Topic is required");
        }

        if (request.getDifficulty() == null || 
            !List.of("Easy", "Medium", "Hard").contains(request.getDifficulty())) {
            throw new IllegalArgumentException("Difficulty must be Easy, Medium, or Hard");
        }

        if (request.getNumQuestions() == null || 
            request.getNumQuestions() < 1 || 
            request.getNumQuestions() > 50) {
            throw new IllegalArgumentException("Number of questions must be between 1 and 50");
        }
    }
}