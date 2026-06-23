package com.fraudguard.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * UserScenarioAttempt entity tracking each time a user attempts a scenario
 * Records correctness, time taken, and rewards earned
 */
@Entity
@Table(name = "user_scenario_attempts", indexes = {
    @Index(name = "idx_user_id", columnList = "user_id"),
    @Index(name = "idx_scenario_id", columnList = "scenario_id"),
    @Index(name = "idx_user_scenario", columnList = "user_id, scenario_id"),
    @Index(name = "idx_is_correct", columnList = "is_correct"),
    @Index(name = "idx_created_at", columnList = "created_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserScenarioAttempt {

    public enum Channel {
        MOBILE_APP,
        USSD,
        SMS
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", length = 36, nullable = false)
    private String userId;

    @Column(name = "scenario_id", length = 36, nullable = false)
    private String scenarioId;

    @Column(name = "hotspot_selected")
    private Integer hotspotSelected;

    @Column(nullable = false)
    private Boolean isCorrect;

    @Column(name = "answer_time_seconds")
    private Integer answerTimeSeconds;

    @Column(nullable = false)
    @Builder.Default
    private Integer pointsEarned = 0;

    @Column(precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal airtimeEarned = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    private Channel channel;

    @Column(columnDefinition = "JSON")
    private String deviceInfo;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
