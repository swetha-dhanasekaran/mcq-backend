package com.online.onlineAssessment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.online.onlineAssessment.entity.User;

import java.util.Optional;

/**
 * User Repository Interface
 * Provides database operations for User entity
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Find user by email
     * @param email user's email
     * @return Optional<User>
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Check if email already exists
     * @param email user's email
     * @return boolean
     */
    boolean existsByEmail(String email);
}
