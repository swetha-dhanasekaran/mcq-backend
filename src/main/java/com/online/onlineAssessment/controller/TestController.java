package com.online.onlineAssessment.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.online.onlineAssessment.dto.GenerateTestRequest;
import com.online.onlineAssessment.dto.ResultSummary;
import com.online.onlineAssessment.responseDto.TestResponse;
import com.online.onlineAssessment.service.TestService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Test Controller
 * Endpoints: /api/tests/**
 */
@RestController
@RequestMapping("/api/tests")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class TestController {

    private final TestService testService;

    /**
     * Generate new test with AI questions
     * POST /api/tests/generate
     * Header: Authorization: Bearer {token}
     */
    @PostMapping("/generate")
    public ResponseEntity<TestResponse> generateTest(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody GenerateTestRequest request) {
        
        log.info("Generate test request for user: {}, topic: {}", 
            userId, request.getTopic());
        
        TestResponse response = testService.generateTest(userId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Get test by ID
     * GET /api/tests/{testId}
     */
    @GetMapping("/{testId}")
    public ResponseEntity<TestResponse> getTest(@PathVariable Long testId) {
        log.info("Get test request for test ID: {}", testId);
        TestResponse response = testService.getTest(testId);
        return ResponseEntity.ok(response);
    }

    /**
     * Get all tests for logged-in user
     * GET /api/tests/user/history
     */
    @GetMapping("/user/history")
    public ResponseEntity<List<ResultSummary>> getUserTests(
            @RequestHeader("X-User-Id") Long userId) {
        
        log.info("Get user tests request for user ID: {}", userId);
        List<ResultSummary> tests = testService.getUserTests(userId);
        return ResponseEntity.ok(tests);
    }
}
