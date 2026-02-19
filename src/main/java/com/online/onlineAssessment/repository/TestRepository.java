package com.online.onlineAssessment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.online.onlineAssessment.entity.Test;

import java.util.List;

/**
 * Test Repository Interface
 * Provides database operations for Test entity
 */
@Repository
public interface TestRepository extends JpaRepository<Test, Long> {
    
    /**
     * Find all tests by user ID
     * @param userId user's ID
     * @return List of tests
     */
    List<Test> findByUser_UserId(Long userId);
    
    /**
     * Find tests by user ID ordered by creation date (newest first)
     * @param userId user's ID
     * @return List of tests
     */
    List<Test> findByUser_UserIdOrderByCreatedAtDesc(Long userId);
    
    /**
     * Find tests by topic
     * @param topic test topic
     * @return List of tests
     */
    List<Test> findByTopicContainingIgnoreCase(String topic);
    
    /**
     * Count total tests by user
     * @param userId user's ID
     * @return count
     */
    long countByUser_UserId(Long userId);
}
