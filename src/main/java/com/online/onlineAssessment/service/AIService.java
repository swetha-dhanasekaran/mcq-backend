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

import lombok.extern.slf4j.Slf4j;

/**
 * AI Service - Handles AI API integration for question generation and feedback
 * Supports both OpenAI and Claude APIs
 */
@Slf4j
@Service
public class AIService {
	/*
	 * 
	 * @Value("${ai.api.key}") private String apiKey;
	 * 
	 * @Value("${ai.api.url}") private String apiUrl;
	 * 
	 * @Value("${ai.model:gpt-4}") private String model;
	 * 
	 * private final RestTemplate restTemplate; private final ObjectMapper
	 * objectMapper;
	 * 
	 * // CORRECT: Single constructor that initializes dependencies public
	 * AIService() { this.restTemplate = new RestTemplate(); this.objectMapper = new
	 * ObjectMapper(); }
	 * 
	 *//**
		 * Generate MCQ questions using AI
		 */
	/*
	 * public AIQuestionResponse generateQuestions(String topic, String difficulty,
	 * int numQuestions) { try { String prompt =
	 * buildQuestionGenerationPrompt(topic, difficulty, numQuestions); String
	 * aiResponse = callAIAPI(prompt); return
	 * parseQuestionsFromResponse(aiResponse); } catch (Exception e) {
	 * log.error("Error generating questions from AI: {}", e.getMessage()); throw
	 * new RuntimeException("Failed to generate questions from AI", e); } }
	 * 
	 *//**
		 * Generate personalized feedback using AI
		 */
	/*
	 * public String generateFeedback(String topic, int totalQuestions, int
	 * correctAnswers, double scorePercentage) { try { String prompt =
	 * buildFeedbackPrompt(topic, totalQuestions, correctAnswers, scorePercentage);
	 * return callAIAPI(prompt); } catch (Exception e) {
	 * log.error("Error generating feedback from AI: {}", e.getMessage()); return
	 * "Great effort! Keep practicing to improve your performance."; } }
	 * 
	 *//**
		 * Build prompt for question generation
		 */
	/*
	 * private String buildQuestionGenerationPrompt(String topic, String difficulty,
	 * int numQuestions) { return String.format(""" Generate %d multiple-choice
	 * interview questions on the topic: %s Difficulty level: %s
	 * 
	 * Requirements: 1. Each question should be relevant for technical interviews 2.
	 * Include 4 options (A, B, C, D) 3. Specify the correct answer (A, B, C, or D)
	 * 4. Provide a brief explanation for the correct answer 5. Questions should be
	 * challenging but fair
	 * 
	 * Return ONLY a valid JSON array in this exact format: [ { "question":
	 * "Question text here?", "optionA": "First option", "optionB": "Second option",
	 * "optionC": "Third option", "optionD": "Fourth option", "correctAnswer": "B",
	 * "explanation": "Explanation for the correct answer" } ]
	 * 
	 * DO NOT include any text before or after the JSON array. """, numQuestions,
	 * topic, difficulty); }
	 * 
	 *//**
		 * Build prompt for feedback generation
		 */
	/*
	 * private String buildFeedbackPrompt(String topic, int totalQuestions, int
	 * correctAnswers, double scorePercentage) { int wrongAnswers = totalQuestions -
	 * correctAnswers;
	 * 
	 * return String.format(""" Analyze this test performance and provide
	 * personalized feedback:
	 * 
	 * Topic: %s Total Questions: %d Correct Answers: %d Wrong Answers: %d Score
	 * Percentage: %.2f%%
	 * 
	 * Provide: 1. Performance analysis (2-3 sentences) 2. Specific areas of
	 * strength 3. Areas needing improvement 4. 2-3 actionable study recommendations
	 * 
	 * Keep the feedback motivating, constructive, and professional. Limit response
	 * to 150 words. """, topic, totalQuestions, correctAnswers, wrongAnswers,
	 * scorePercentage); }
	 * 
	 *//**
		 * Call AI API (OpenAI or Claude)
		 */
	/*
	 * private String callAIAPI(String prompt) throws Exception { HttpHeaders
	 * headers = new HttpHeaders();
	 * headers.setContentType(MediaType.APPLICATION_JSON);
	 * headers.set("Authorization", "Bearer " + apiKey);
	 * 
	 * Map<String, Object> requestBody = new HashMap<>(); requestBody.put("model",
	 * model); requestBody.put("messages", List.of( Map.of("role", "user",
	 * "content", prompt) )); requestBody.put("temperature", 0.7);
	 * requestBody.put("max_tokens", 2000);
	 * 
	 * HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody,
	 * headers);
	 * 
	 * log.info("Calling AI API: {} with model: {}", apiUrl, model);
	 * 
	 * ResponseEntity<String> response = restTemplate.exchange( apiUrl,
	 * HttpMethod.POST, request, String.class );
	 * 
	 * if (response.getStatusCode() == HttpStatus.OK) { return
	 * extractContentFromResponse(response.getBody()); } else { throw new
	 * RuntimeException("AI API returned error: " + response.getStatusCode()); } }
	 * 
	 *//**
		 * Extract content from AI response
		 */
	/*
	 * private String extractContentFromResponse(String responseBody) throws
	 * Exception { JsonNode rootNode = objectMapper.readTree(responseBody);
	 * 
	 * // For OpenAI format if (rootNode.has("choices")) { return
	 * rootNode.get("choices").get(0) .get("message").get("content").asText(); }
	 * 
	 * // For Claude format if (rootNode.has("content")) { JsonNode contentArray =
	 * rootNode.get("content"); if (contentArray.isArray() && contentArray.size() >
	 * 0) { return contentArray.get(0).get("text").asText(); } }
	 * 
	 * throw new RuntimeException("Unexpected AI response format"); }
	 * 
	 *//**
		 * Parse questions from AI response
		 */
	/*
	 * private AIQuestionResponse parseQuestionsFromResponse(String aiResponse)
	 * throws Exception { // Clean the response - remove markdown code blocks if
	 * present String cleanedResponse = aiResponse.trim(); if
	 * (cleanedResponse.startsWith("```json")) { cleanedResponse =
	 * cleanedResponse.substring(7); } if (cleanedResponse.startsWith("```")) {
	 * cleanedResponse = cleanedResponse.substring(3); } if
	 * (cleanedResponse.endsWith("```")) { cleanedResponse =
	 * cleanedResponse.substring(0, cleanedResponse.length() - 3); } cleanedResponse
	 * = cleanedResponse.trim();
	 * 
	 * log.info("Parsing AI response: {}", cleanedResponse);
	 * 
	 * // Parse JSON array JsonNode questionsNode =
	 * objectMapper.readTree(cleanedResponse); List<AIQuestion> questions = new
	 * ArrayList<>();
	 * 
	 * if (questionsNode.isArray()) { for (JsonNode questionNode : questionsNode) {
	 * AIQuestion question = new AIQuestion();
	 * question.setQuestion(questionNode.get("question").asText());
	 * question.setOptionA(questionNode.get("optionA").asText());
	 * question.setOptionB(questionNode.get("optionB").asText());
	 * question.setOptionC(questionNode.get("optionC").asText());
	 * question.setOptionD(questionNode.get("optionD").asText());
	 * question.setCorrectAnswer(questionNode.get("correctAnswer").asText());
	 * question.setExplanation(questionNode.get("explanation").asText());
	 * 
	 * questions.add(question); } }
	 * 
	 * AIQuestionResponse response = new AIQuestionResponse();
	 * response.setQuestions(questions); return response; }
	 */}