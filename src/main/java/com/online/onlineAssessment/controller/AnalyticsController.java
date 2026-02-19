package com.online.onlineAssessment.controller;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.online.onlineAssessment.dto.TopicPerformance;
import com.online.onlineAssessment.responseDto.PerformanceAnalyticsResponse;
import com.online.onlineAssessment.service.AnalyticsService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Analytics Controller
 * Endpoints: /api/analytics/**
 */
@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    /**
     * Get comprehensive performance analytics
     * GET /api/analytics/performance
     */
    @GetMapping("/performance")
    public ResponseEntity<PerformanceAnalyticsResponse> getPerformanceAnalytics(
            @RequestHeader("X-User-Id") Long userId) {
        
        log.info("Get performance analytics for user ID: {}", userId);
        PerformanceAnalyticsResponse response = analyticsService.getPerformanceAnalytics(userId);
        return ResponseEntity.ok(response);
    }

    /**
     * Get topic-specific performance
     * GET /api/analytics/topic/{topic}
     */
    @GetMapping("/topic/{topic}")
    public ResponseEntity<List<TopicPerformance>> getTopicPerformance(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable String topic) {
        
        log.info("Get topic performance for user ID: {}, topic: {}", userId, topic);
        List<TopicPerformance> response = analyticsService.getTopicPerformance(userId, topic);
        return ResponseEntity.ok(response);
    }
}
