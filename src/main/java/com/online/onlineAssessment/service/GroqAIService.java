package com.online.onlineAssessment.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.online.onlineAssessment.dto.AIQuestion;
import com.online.onlineAssessment.responseDto.AIQuestionResponse;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

/**
 * Groq AI Service - FREE, FAST, UNLIMITED!
 * Uses Llama models via Groq Cloud
 */
@Service
@Slf4j
public class GroqAIService {

    @Value("${groq.api.key}")
    private String apiKey;

    private final String GROQ_URL = "https://api.groq.com/openai/v1/chat/completions";
    
    // Best models in order of preference
    private final String[] MODELS = {
        "llama-3.3-70b-versatile",  // Best quality
        "llama-3.1-70b-versatile",   // Backup
        "llama-3.1-8b-instant"       // Fast fallback
    };
    
    private int currentModelIndex = 0;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public GroqAIService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    @PostConstruct
    public void init() {
        log.info("=".repeat(60));
        log.info("🚀 Groq AI Service Initialized (FREE & UNLIMITED!)");
        log.info("API Key present: {}", apiKey != null && !apiKey.isEmpty());
        if (apiKey != null && apiKey.length() > 15) {
            log.info("API Key starts with: {}...", apiKey.substring(0, 15));
        }
        log.info("Available models: {}", String.join(", ", MODELS));
        log.info("=".repeat(60));
    }

    /**
     * Generate MCQ questions using Groq AI
     */
    public AIQuestionResponse generateQuestions(String topic, String difficulty, int numQuestions) {
        // Try each model until one succeeds
        for (int attempt = 0; attempt < MODELS.length; attempt++) {
            String model = MODELS[currentModelIndex];
            
            try {
                log.info("🔥 Generating {} questions on: {} ({})", numQuestions, topic, difficulty);
                log.info("Using model: {}", model);

                String prompt = buildQuestionGenerationPrompt(topic, difficulty, numQuestions);
                String aiResponse = callGroqAPI(model, prompt);
                AIQuestionResponse response = parseQuestionsFromResponse(aiResponse);

                log.info("✅ Successfully generated {} questions", response.getQuestions().size());
                return response;
                
            } catch (Exception e) {
                log.warn("❌ Model {} failed: {}", model, e.getMessage());
                currentModelIndex = (currentModelIndex + 1) % MODELS.length;
                
                if (attempt == MODELS.length - 1) {
                    log.error("🔴 All models failed!");
                    throw new RuntimeException("Failed to generate questions: " + e.getMessage(), e);
                }
            }
        }
        
        throw new RuntimeException("All Groq models failed");
    }

    /**
     * Generate personalized feedback using Groq AI
     */
    public String generateFeedback(String topic, int totalQuestions, int correctAnswers, double scorePercentage) {
        try {
            log.info("📝 Generating feedback for: {}, score: {}%", topic, scorePercentage);

            String prompt = buildFeedbackPrompt(topic, totalQuestions, correctAnswers, scorePercentage);
            String feedback = callGroqAPI(MODELS[0], prompt);

            log.info("✅ Feedback generated successfully");
            return feedback;
            
        } catch (Exception e) {
            log.error("❌ Error generating feedback: {}", e.getMessage());
            return generateDefaultFeedback(scorePercentage);
        }
    }

    /**
     * Call Groq API (OpenAI-compatible)
     */
    private String callGroqAPI(String model, String prompt) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model);
        
        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "user", "content", prompt));
        requestBody.put("messages", messages);
        
        requestBody.put("temperature", 0.7);
        requestBody.put("max_tokens", 4000);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        log.debug("📤 Calling Groq API...");

        ResponseEntity<String> response = restTemplate.exchange(
            GROQ_URL,
            HttpMethod.POST,
            request,
            String.class
        );

        if (response.getStatusCode() == HttpStatus.OK) {
            String extractedContent = extractContentFromResponse(response.getBody());
            log.debug("📥 Groq response received");
            return extractedContent;
        } else {
            throw new RuntimeException("Groq API returned error: " + response.getStatusCode());
        }
    }

    /**
     * Extract content from Groq response
     */
    private String extractContentFromResponse(String responseBody) throws Exception {
        JsonNode rootNode = objectMapper.readTree(responseBody);

        if (rootNode.has("choices")) {
            JsonNode choices = rootNode.get("choices");
            if (choices.isArray() && choices.size() > 0) {
                JsonNode message = choices.get(0).get("message");
                if (message != null && message.has("content")) {
                    return message.get("content").asText();
                }
            }
        }

        throw new RuntimeException("Unexpected Groq response format");
    }

    /**
     * Build prompt for question generation
     */
    private String buildQuestionGenerationPrompt(String topic, String difficulty, int numQuestions) {
        return String.format(
            """
            Generate exactly %d multiple-choice interview questions on: "%s"
            Difficulty: %s
            
            REQUIREMENTS:
            1. Professional technical interview questions
            2. Exactly 4 options (A, B, C, D)
            3. One correct answer (letter A, B, C, or D)
            4. Clear, concise explanation (1-2 sentences)
            5. Questions appropriate for %s difficulty level
            6. Variety in question types and concepts
            
            RETURN ONLY THIS JSON FORMAT (NO OTHER TEXT):
            [
              {
                "question": "What is the purpose of indexing in databases?",
                "optionA": "To slow down queries",
                "optionB": "To speed up data retrieval",
                "optionC": "To delete duplicate data",
                "optionD": "To compress the database",
                "correctAnswer": "B",
                "explanation": "Database indexes create data structures that allow faster lookups by reducing the number of records to scan."
              }
            ]
            
            CRITICAL:
            - Return ONLY the JSON array
            - NO ```json markers
            - NO explanatory text
            - Exactly %d questions
            """,
            numQuestions, topic, difficulty, difficulty, numQuestions
        );
    }

    /**
     * Build prompt for feedback generation
     */
    private String buildFeedbackPrompt(String topic, int totalQuestions, int correctAnswers, double scorePercentage) {
        int wrongAnswers = totalQuestions - correctAnswers;

        return String.format(
            """
            Provide personalized feedback for this test performance:
            
            Topic: %s
            Total Questions: %d
            Correct: %d
            Wrong: %d
            Score: %.1f%%
            
            Generate feedback with:
            1. Performance summary (1-2 sentences)
            2. Strengths shown
            3. Areas to improve
            4. 2-3 actionable study tips
            
            Keep it:
            - Motivating and constructive
            - Specific to the topic
            - Under 150 words
            - Professional tone
            
            Return ONLY the feedback text (no formatting).
            """,
            topic, totalQuestions, correctAnswers, wrongAnswers, scorePercentage
        );
    }

    /**
     * Parse questions from AI response
     */
    private AIQuestionResponse parseQuestionsFromResponse(String aiResponse) throws Exception {
        String cleanedResponse = aiResponse.trim();

        // Remove markdown if present
        if (cleanedResponse.startsWith("```json")) {
            cleanedResponse = cleanedResponse.substring(7);
        }
        if (cleanedResponse.startsWith("```")) {
            cleanedResponse = cleanedResponse.substring(3);
        }
        if (cleanedResponse.endsWith("```")) {
            cleanedResponse = cleanedResponse.substring(0, cleanedResponse.length() - 3);
        }
        cleanedResponse = cleanedResponse.trim();

        log.debug("Parsing response (first 100 chars): {}",
                cleanedResponse.substring(0, Math.min(100, cleanedResponse.length())));

        JsonNode questionsNode = objectMapper.readTree(cleanedResponse);
        List<AIQuestion> questions = new ArrayList<>();

        if (questionsNode.isArray()) {
            for (JsonNode questionNode : questionsNode) {
                try {
                    AIQuestion question = new AIQuestion();
                    question.setQuestion(questionNode.get("question").asText());
                    question.setOptionA(questionNode.get("optionA").asText());
                    question.setOptionB(questionNode.get("optionB").asText());
                    question.setOptionC(questionNode.get("optionC").asText());
                    question.setOptionD(questionNode.get("optionD").asText());
                    question.setCorrectAnswer(questionNode.get("correctAnswer").asText().toUpperCase());
                    question.setExplanation(questionNode.get("explanation").asText());

                    questions.add(question);
                } catch (Exception e) {
                    log.error("❌ Error parsing question: {}", e.getMessage());
                    throw new RuntimeException("Invalid question format", e);
                }
            }
        } else {
            throw new RuntimeException("Response is not a JSON array");
        }

        if (questions.isEmpty()) {
            throw new RuntimeException("No questions found in response");
        }

        AIQuestionResponse response = new AIQuestionResponse();
        response.setQuestions(questions);
        return response;
    }

    /**
     * Generate default feedback when AI fails
     */
    private String generateDefaultFeedback(double scorePercentage) {
        if (scorePercentage >= 80) {
            return "Excellent performance! You've demonstrated strong understanding of the topic. "
                    + "Keep up the great work and continue practicing to maintain your expertise.";
        } else if (scorePercentage >= 65) {
            return "Good job! You have a solid grasp of the fundamentals. "
                    + "Focus on reviewing the areas where you made mistakes to further improve your knowledge.";
        } else if (scorePercentage >= 50) {
            return "You're making progress! Review the explanations for incorrect answers carefully. "
                    + "Consider studying the topic more thoroughly and take practice tests to reinforce your learning.";
        } else {
            return "Keep practicing! This topic requires more study. "
                    + "Review the fundamentals, study the explanations provided, and don't hesitate to seek additional resources. "
                    + "Consistent practice will lead to improvement.";
        }
    }
}