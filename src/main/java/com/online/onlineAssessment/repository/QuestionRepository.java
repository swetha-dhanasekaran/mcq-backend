package com.online.onlineAssessment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.online.onlineAssessment.entity.Question;

import java.util.List;

/**
 * Question Repository Interface
 * Provides database operations for Question entity
 */
@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    
    /**
     * Find all questions by test ID
     * @param testId test's ID
     * @return List of questions
     */
    List<Question> findByTest_TestId(Long testId);
    
    /**
     * Count questions by test ID
     * @param testId test's ID
     * @return count
     */
    long countByTest_TestId(Long testId);
}
