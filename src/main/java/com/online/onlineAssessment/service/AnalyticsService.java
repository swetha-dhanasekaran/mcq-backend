package com.online.onlineAssessment.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.online.onlineAssessment.dto.ResultSummary;
import com.online.onlineAssessment.dto.TopicPerformance;
import com.online.onlineAssessment.entity.Result;
import com.online.onlineAssessment.repository.ResultRepository;
import com.online.onlineAssessment.repository.TestRepository;
import com.online.onlineAssessment.responseDto.PerformanceAnalyticsResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Analytics Service - Handles performance analytics and tracking
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AnalyticsService {

	private final ResultRepository resultRepository;
	private final TestRepository testRepository;

	/**
	 * Get comprehensive performance analytics for a user
	 */
	public PerformanceAnalyticsResponse getPerformanceAnalytics(Long userId) {
		log.info("Generating performance analytics for user: {}", userId);

		// Get all results for user
		List<Result> results = resultRepository.findByTest_User_UserIdOrderByCreatedAtDesc(userId);

		if (results.isEmpty()) {
			return createEmptyAnalytics(userId);
		}

		// Calculate statistics
		long totalTestsTaken = results.size();
		Double averageScore = resultRepository.findAverageScoreByUserId(userId);

		int totalQuestionsAttempted = results.stream().mapToInt(Result::getTotalQuestions).sum();

		int totalCorrectAnswers = results.stream().mapToInt(Result::getCorrectAnswers).sum();

		// Calculate topic-wise performance
		List<TopicPerformance> topicPerformance = calculateTopicPerformance(results);

		// Get recent test summaries
		List<ResultSummary> recentTests = results.stream().limit(10)
				.map(r -> new ResultSummary(r.getTest().getTestId(), r.getTest().getTopic(),
						r.getTest().getDifficulty(), r.getScorePercentage(), r.getCreatedAt()))
				.collect(Collectors.toList());

		return new PerformanceAnalyticsResponse(userId, totalTestsTaken, averageScore != null ? averageScore : 0.0,
				totalQuestionsAttempted, totalCorrectAnswers, topicPerformance, recentTests);
	}

	/**
	 * Calculate topic-wise performance
	 */
	private List<TopicPerformance> calculateTopicPerformance(List<Result> results) {
		// Group results by topic
		Map<String, List<Result>> topicGroups = results.stream()
				.collect(Collectors.groupingBy(r -> r.getTest().getTopic()));

		List<TopicPerformance> topicPerformanceList = new ArrayList<>();

		for (Map.Entry<String, List<Result>> entry : topicGroups.entrySet()) {
			String topic = entry.getKey();
			List<Result> topicResults = entry.getValue();

			long testsCount = topicResults.size();
			double averageScore = topicResults.stream().mapToDouble(Result::getScorePercentage).average().orElse(0.0);

			String performance = categorizePerformance(averageScore);

			topicPerformanceList.add(new TopicPerformance(topic, testsCount, averageScore, performance));
		}

		// Sort by average score descending
		topicPerformanceList.sort((a, b) -> Double.compare(b.getAverageScore(), a.getAverageScore()));

		return topicPerformanceList;
	}

	/**
	 * Categorize performance based on score
	 */
	private String categorizePerformance(double score) {
		if (score >= 80) {
			return "Excellent";
		} else if (score >= 65) {
			return "Good";
		} else if (score >= 50) {
			return "Average";
		} else {
			return "Needs Improvement";
		}
	}

	/**
	 * Create empty analytics response
	 */
	private PerformanceAnalyticsResponse createEmptyAnalytics(Long userId) {
		return new PerformanceAnalyticsResponse(userId, 0L, 0.0, 0, 0, new ArrayList<>(), new ArrayList<>());
	}

	/**
	 * Get topic-specific performance
	 */
	public List<TopicPerformance> getTopicPerformance(Long userId, String topic) {
		List<Result> results = resultRepository.findByUserIdAndTopic(userId, topic);
		return calculateTopicPerformance(results);
	}
}
