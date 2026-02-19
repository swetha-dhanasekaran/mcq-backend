package com.online.onlineAssessment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.online.onlineAssessment.entity.UserAnswer;

import java.util.List;
import java.util.Optional;

/**
 * UserAnswer Repository Interface
 * Provides database operations for UserAnswer entity
 */
@Repository
public interface UserAnswerRepository extends JpaRepository<UserAnswer, Long> {
    
    /**
     * Find user answer by question ID
     * @param questionId question's ID
     * @return Optional<UserAnswer>
     */
    Optional<UserAnswer> findByQuestion_QuestionId(Long questionId);
    
    /**
     * Find all user answers for a specific test
     * @param testId test's ID
     * @return List of user answers
     */
    List<UserAnswer> findByQuestion_Test_TestId(Long testId);
}
