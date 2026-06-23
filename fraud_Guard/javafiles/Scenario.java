package com.fraudguard.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Scenario entity representing a fraud scenario for interactive learning
 * Users complete scenarios and earn airtime rewards
 */
@Entity
@Table(name = "scenarios", indexes = {
    @Index(name = "idx_type", columnList = "scenario_type"),
    @Index(name = "idx_channel", columnList = "channel"),
    @Index(name = "idx_difficulty", columnList = "difficulty"),
    @Index(name = "idx_is_active", columnList = "is_active")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Scenario {

    public enum ScenarioType {
        PHISHING_EMAIL,
        FAKE_BANKING_APP,
        WHATSAPP_SCAM,
        SMS_PHISHING,
        VISHING_CALL
    }

    public enum Channel {
        MOBILE_APP,
        USSD,
        SMS
    }

    public enum Difficulty {
        BEGINNER,
        INTERMEDIATE,
        ADVANCED
    }

    @Id
    @Column(length = 36)
    private String id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ScenarioType scenarioType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Channel channel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Difficulty difficulty = Difficulty.BEGINNER;

    @Column(length = 500)
    private String imageUrl;

    @Column(length = 500)
    private String videoUrl;

    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String explanation;

    @Column(nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal rewardAmount = new BigDecimal("0.10");

    @Column(nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isFeatured = false;

    @Column(nullable = false)
    @Builder.Default
    private Integer difficultyLevel = 1;

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
}
