package com.online.onlineAssessment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.online.onlineAssessment.entity.Result;

import java.util.List;
import java.util.Optional;

/**
 * Result Repository Interface
 * Provides database operations for Result entity
 */
@Repository
public interface ResultRepository extends JpaRepository<Result, Long> {
    
    /**
     * Find result by test ID
     * @param testId test's ID
     * @return Optional<Result>
     */
    Optional<Result> findByTest_TestId(Long testId);
    
    /**
     * Find all results for a user
     * @param userId user's ID
     * @return List of results
     */
    List<Result> findByTest_User_UserId(Long userId);
    
    /**
     * Find all results for a user ordered by date (newest first)
     * @param userId user's ID
     * @return List of results
     */
    List<Result> findByTest_User_UserIdOrderByCreatedAtDesc(Long userId);
    
    /**
     * Calculate average score for a user
     * @param userId user's ID
     * @return average score percentage
     */
    @Query("SELECT AVG(r.scorePercentage) FROM Result r WHERE r.test.user.userId = :userId")
    Double findAverageScoreByUserId(@Param("userId") Long userId);
    
    /**
     * Find results by topic
     * @param userId user's ID
     * @param topic test topic
     * @return List of results
     */
    @Query("SELECT r FROM Result r WHERE r.test.user.userId = :userId AND r.test.topic LIKE %:topic%")
    List<Result> findByUserIdAndTopic(@Param("userId") Long userId, @Param("topic") String topic);
}
