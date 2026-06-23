package com.fraudguard.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * User entity representing a user profile in the FraudGuard platform
 * Tracks learning progress, scores, and account information
 */
@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_email", columnList = "email", unique = true),
    @Index(name = "idx_username", columnList = "username", unique = true),
    @Index(name = "idx_region", columnList = "region"),
    @Index(name = "idx_total_score", columnList = "total_score")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @Column(length = 36)
    private String id;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(length = 20)
    private String phoneNumber;

    @Column(nullable = false, length = 50)
    private String region; // Western Cape, Gauteng, etc.

    // Performance metrics
    @Column(nullable = false)
    @Builder.Default
    private Integer totalScore = 0;

    @Column(nullable = false)
    @Builder.Default
    private Integer currentStreak = 0;

    @Column(nullable = false)
    @Builder.Default
    private Integer maxStreak = 0;

    @Column(nullable = false)
    @Builder.Default
    private Integer scenariosCompleted = 0;

    @Column(nullable = false)
    @Builder.Default
    private Integer correctAnswers = 0;

    // Account status
    @Column(nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isEmailVerified = false;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    // Audit
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        id = UUID.randomUUID().toString();
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Get user's full name
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }

    /**
     * Increment streak and update max streak if needed
     */
    public void incrementStreak() {
        currentStreak++;
        if (currentStreak > maxStreak) {
            maxStreak = currentStreak;
        }
    }

    /**
     * Reset streak to zero on incorrect answer
     */
    public void resetStreak() {
        currentStreak = 0;
    }
}
