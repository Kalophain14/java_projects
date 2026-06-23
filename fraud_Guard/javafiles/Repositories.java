package com.fraudguard.repository;

import com.fraudguard.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * User Repository - Database access for User entity
 */
@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    Page<User> findByRegionOrderByTotalScoreDesc(String region, Pageable pageable);
    Page<User> findAllByOrderByTotalScoreDesc(Pageable pageable);
    long countByIsActiveTrue();
}

/**
 * Scenario Repository - Database access for Scenario entity
 */
@Repository
public interface ScenarioRepository extends JpaRepository<Scenario, String> {
    Page<Scenario> findByIsActiveTrueOrderByCreatedAtDesc(Pageable pageable);
    Page<Scenario> findByScenarioTypeAndIsActiveTrueOrderByCreatedAtDesc(
        Scenario.ScenarioType type, Pageable pageable);
    Page<Scenario> findByDifficultyAndIsActiveTrueOrderByCreatedAtDesc(
        Scenario.Difficulty difficulty, Pageable pageable);
    List<Scenario> findByChannelAndIsActiveTrue(Scenario.Channel channel);
    List<Scenario> findByIsFeaturedTrueAndIsActiveTrueOrderByCreatedAtDesc();
}

/**
 * Wallet Repository - Database access for Wallet entity
 */
@Repository
public interface WalletRepository extends JpaRepository<Wallet, String> {
    Optional<Wallet> findByUserId(String userId);
}

/**
 * UserScenarioAttempt Repository - Database access for attempt tracking
 */
@Repository
public interface UserScenarioAttemptRepository extends JpaRepository<UserScenarioAttempt, Long> {
    Page<UserScenarioAttemptRepository> findByUserIdOrderByCreatedAtDesc(String userId, Pageable pageable);
    
    @Query("SELECT COUNT(a) FROM UserScenarioAttempt a WHERE a.userId = :userId AND a.scenarioId = :scenarioId")
    long countAttempts(String userId, String scenarioId);
    
    @Query("SELECT COUNT(a) FROM UserScenarioAttempt a WHERE a.userId = :userId AND a.isCorrect = true")
    long countCorrectAnswers(String userId);
    
    @Query("SELECT AVG(CAST(a.answerTimeSeconds AS double)) FROM UserScenarioAttempt a WHERE a.userId = :userId")
    Double getAverageAnswerTime(String userId);
}
