package com.online.onlineAssessment.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.online.onlineAssessment.dto.SubmitAnswersRequest;
import com.online.onlineAssessment.responseDto.ResultResponse;
import com.online.onlineAssessment.service.ResultService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Result Controller
 * Endpoints: /api/results/**
 */
@RestController
@RequestMapping("/api/results")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class ResultController {

    private final ResultService resultService;

    /**
     * Submit answers for evaluation
     * POST /api/results/submit
     */
    @PostMapping("/submit")
    public ResponseEntity<ResultResponse> submitAnswers(
            @RequestBody SubmitAnswersRequest request) {
        
        log.info("Submit answers request for test ID: {}", request.getTestId());
        ResultResponse response = resultService.submitAnswers(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Get result by test ID
     * GET /api/results/test/{testId}
     */
    @GetMapping("/test/{testId}")
    public ResponseEntity<ResultResponse> getResult(@PathVariable Long testId) {
        log.info("Get result request for test ID: {}", testId);
        ResultResponse response = resultService.getResult(testId);
        return ResponseEntity.ok(response);
    }
}
