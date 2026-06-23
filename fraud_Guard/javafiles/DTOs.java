package com.fraudguard.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

// ============================================================================
// AUTHENTICATION DTOs
// ============================================================================

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {
    @Email(message = "Email must be valid")
    private String email;
    
    @NotBlank
    @Size(min = 3, max = 20)
    private String username;
    
    @NotBlank
    @Size(min = 8)
    private String password;
    
    private String firstName;
    private String lastName;
    
    @NotBlank
    private String region;
    
    @Pattern(regexp = "^\\+?[0-9]{10,15}$")
    private String phoneNumber;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequest {
    @Email
    private String email;
    
    @NotBlank
    private String password;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private UserResponse user;
    private String tokenType;
    private Long expiresIn;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshTokenRequest {
    private String refreshToken;
}

// ============================================================================
// USER DTOs
// ============================================================================

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
    private String id;
    private String email;
    private String username;
    private String firstName;
    private String lastName;
    private String region;
    private String phoneNumber;
    private Integer totalScore;
    private Integer currentStreak;
    private Integer maxStreak;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateUserRequest {
    private String firstName;
    private String lastName;
    private String region;
    private String phoneNumber;
}

// ============================================================================
// SCENARIO DTOs
// ============================================================================

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScenarioResponse {
    private String id;
    private String title;
    private String description;
    private String scenarioType;
    private String channel;
    private String difficulty;
    private String imageUrl;
    private String videoUrl;
    private String explanation;
    private BigDecimal rewardAmount;
    private Boolean isActive;
    private Boolean isFeatured;
    private LocalDateTime createdAt;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateScenarioRequest {
    @NotBlank
    private String title;
    
    @NotBlank
    private String description;
    
    @NotBlank
    private String scenarioType;
    
    @NotBlank
    private String channel;
    
    @NotBlank
    private String difficulty;
    
    private String imageUrl;
    private String videoUrl;
    
    @NotBlank
    private String explanation;
    
    @DecimalMin("0.01")
    private BigDecimal rewardAmount;
}

// ============================================================================
// SCENARIO ANSWER DTOs
// ============================================================================

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubmitAnswerRequest {
    @NotNull
    private String scenarioId;
    
    @NotNull
    private Integer selectedHotspotId;
    
    private Integer answerTimeSeconds;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnswerResultResponse {
    private String scenarioId;
    private Boolean isCorrect;
    private String explanation;
    private BigDecimal rewardEarned;
    private Integer streakUpdated;
    private Integer totalScore;
    private Integer pointsEarned;
}

// ============================================================================
// WALLET DTOs
// ============================================================================

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WalletResponse {
    private String userId;
    private BigDecimal balanceUsd;
    private BigDecimal totalEarned;
    private BigDecimal totalRedeemed;
    private LocalDateTime lastUpdated;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestPayoutRequest {
    @DecimalMin("0.10")
    @DecimalMax("50.00")
    private BigDecimal amount;
    
    @Pattern(regexp = "^\\+?[0-9]{10,15}$")
    private String phoneNumber;
    
    @NotBlank
    private String provider; // MTN, VODACOM, CELL_C, TELKOM
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PayoutResponse {
    private String payoutId;
    private String status;
    private BigDecimal amount;
    private String phoneNumber;
    private String provider;
    private LocalDateTime createdAt;
    private LocalDateTime estimatedCompletion;
}

// ============================================================================
// TRANSACTION DTOs
// ============================================================================

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionResponse {
    private String id;
    private String userId;
    private String type; // EARNED, REDEEMED, REVERSED
    private BigDecimal amount;
    private String relatedScenarioId;
    private String relatedPayoutId;
    private String description;
    private LocalDateTime createdAt;
}

// ============================================================================
// LEADERBOARD DTOs
// ============================================================================

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaderboardEntryResponse {
    private Integer rank;
    private String userId;
    private String username;
    private Integer totalScore;
    private Integer currentStreak;
    private String region;
    private String badge; // ROOKIE, LEARNER, EXPERT, MASTER
}

// ============================================================================
// ERROR DTOs
// ============================================================================

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponse {
    private String error;
    private String message;
    private String path;
    private LocalDateTime timestamp;
    private int status;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ValidationErrorResponse {
    private String error;
    private String message;
    private java.util.Map<String, String> fieldErrors;
    private LocalDateTime timestamp;
    private int status;
}
