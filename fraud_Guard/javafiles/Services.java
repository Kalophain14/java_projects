package com.fraudguard.service;

import com.fraudguard.dto.*;
import com.fraudguard.entity.*;
import com.fraudguard.exception.*;
import com.fraudguard.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * UserService - Handles user management and authentication
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Transactional
    public UserResponse registerUser(RegisterRequest request) {
        // Check if email already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ResourceAlreadyExistsException("Email already registered");
        }
        
        // Check if username already exists
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new ResourceAlreadyExistsException("Username already taken");
        }
        
        // Create user
        User user = User.builder()
                .email(request.getEmail())
                .username(request.getUsername())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .region(request.getRegion())
                .phoneNumber(request.getPhoneNumber())
                .build();
        
        user = userRepository.save(user);
        
        // Create wallet for user
        Wallet wallet = Wallet.builder()
                .userId(user.getId())
                .balanceUsd(BigDecimal.ZERO)
                .totalEarned(BigDecimal.ZERO)
                .totalRedeemed(BigDecimal.ZERO)
                .build();
        
        walletRepository.save(wallet);
        
        log.info("User registered successfully: {}", user.getUsername());
        return mapToUserResponse(user);
    }
    
    public User getUserById(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
    
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
    
    @Transactional
    public UserResponse updateUser(String userId, UpdateUserRequest request) {
        User user = getUserById(userId);
        
        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        if (request.getRegion() != null) {
            user.setRegion(request.getRegion());
        }
        if (request.getPhoneNumber() != null) {
            user.setPhoneNumber(request.getPhoneNumber());
        }
        
        user = userRepository.save(user);
        log.info("User updated: {}", userId);
        return mapToUserResponse(user);
    }
    
    @Transactional
    public void recordLastLogin(String userId) {
        User user = getUserById(userId);
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);
    }
    
    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .region(user.getRegion())
                .phoneNumber(user.getPhoneNumber())
                .totalScore(user.getTotalScore())
                .currentStreak(user.getCurrentStreak())
                .maxStreak(user.getMaxStreak())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}

/**
 * ScenarioService - Handles scenario management
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ScenarioService {
    
    private final ScenarioRepository scenarioRepository;
    private final UserScenarioAttemptRepository attemptRepository;
    private final UserRepository userRepository;
    private final WalletService walletService;
    
    public Page<ScenarioResponse> getScenarios(Pageable pageable) {
        return scenarioRepository.findByIsActiveTrueOrderByCreatedAtDesc(pageable)
                .map(this::mapToScenarioResponse);
    }
    
    public Page<ScenarioResponse> getScenariosByDifficulty(
            Scenario.Difficulty difficulty, Pageable pageable) {
        return scenarioRepository.findByDifficultyAndIsActiveTrueOrderByCreatedAtDesc(difficulty, pageable)
                .map(this::mapToScenarioResponse);
    }
    
    public ScenarioResponse getScenarioById(String scenarioId) {
        Scenario scenario = scenarioRepository.findById(scenarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Scenario not found"));
        return mapToScenarioResponse(scenario);
    }
    
    @Transactional
    public AnswerResultResponse submitAnswer(String userId, SubmitAnswerRequest request) {
        // Get user and scenario
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        Scenario scenario = scenarioRepository.findById(request.getScenarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Scenario not found"));
        
        // Check if already answered (can answer once per scenario)
        long attemptCount = attemptRepository.countAttempts(userId, scenario.getId());
        if (attemptCount > 0) {
            throw new BadRequestException("You have already answered this scenario");
        }
        
        // Record the attempt
        // TODO: Implement hotspot logic - for now, assume hotspot 1 is correct
        boolean isCorrect = request.getSelectedHotspotId() == 1;
        
        UserScenarioAttempt attempt = UserScenarioAttempt.builder()
                .userId(userId)
                .scenarioId(scenario.getId())
                .hotspotSelected(request.getSelectedHotspotId())
                .isCorrect(isCorrect)
                .answerTimeSeconds(request.getAnswerTimeSeconds())
                .airtimeEarned(isCorrect ? scenario.getRewardAmount() : BigDecimal.ZERO)
                .pointsEarned(isCorrect ? 10 : 0)
                .build();
        
        attemptRepository.save(attempt);
        
        // Update user progress
        if (isCorrect) {
            user.incrementStreak();
            user.setCorrectAnswers(user.getCorrectAnswers() + 1);
            user.setTotalScore(user.getTotalScore() + 10);
            
            // Add reward to wallet
            walletService.addEarning(userId, scenario.getRewardAmount());
        } else {
            user.resetStreak();
        }
        
        user.setScenariosCompleted(user.getScenariosCompleted() + 1);
        userRepository.save(user);
        
        log.info("User {} answered scenario {}: {}", userId, scenario.getId(), isCorrect);
        
        return AnswerResultResponse.builder()
                .scenarioId(scenario.getId())
                .isCorrect(isCorrect)
                .explanation(scenario.getExplanation())
                .rewardEarned(isCorrect ? scenario.getRewardAmount() : BigDecimal.ZERO)
                .streakUpdated(user.getCurrentStreak())
                .totalScore(user.getTotalScore())
                .pointsEarned(isCorrect ? 10 : 0)
                .build();
    }
    
    private ScenarioResponse mapToScenarioResponse(Scenario scenario) {
        return ScenarioResponse.builder()
                .id(scenario.getId())
                .title(scenario.getTitle())
                .description(scenario.getDescription())
                .scenarioType(scenario.getScenarioType().name())
                .channel(scenario.getChannel().name())
                .difficulty(scenario.getDifficulty().name())
                .imageUrl(scenario.getImageUrl())
                .videoUrl(scenario.getVideoUrl())
                .explanation(scenario.getExplanation())
                .rewardAmount(scenario.getRewardAmount())
                .isActive(scenario.getIsActive())
                .isFeatured(scenario.getIsFeatured())
                .createdAt(scenario.getCreatedAt())
                .build();
    }
}

/**
 * WalletService - Handles wallet and reward management
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class WalletService {
    
    private final WalletRepository walletRepository;
    
    public WalletResponse getWallet(String userId) {
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found"));
        
        return mapToWalletResponse(wallet);
    }
    
    @Transactional
    public void addEarning(String userId, BigDecimal amount) {
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found"));
        
        wallet.addEarning(amount);
        walletRepository.save(wallet);
        log.info("Added earning to user {}: ${}", userId, amount);
    }
    
    @Transactional
    public PayoutResponse requestPayout(String userId, RequestPayoutRequest request) {
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found"));
        
        // Check if locked
        if (wallet.isCurrentlyLocked()) {
            throw new BadRequestException("Payout in progress. Please try again later.");
        }
        
        // Check balance
        if (!wallet.hasSufficientBalance(request.getAmount())) {
            throw new BadRequestException("Insufficient balance. Required: $" + request.getAmount());
        }
        
        // Lock wallet
        wallet.lockForPayout();
        walletRepository.save(wallet);
        
        // TODO: Queue payout to message broker for Africa's Talking integration
        
        log.info("Payout requested: userId={}, amount=${}, phone={}", 
                userId, request.getAmount(), request.getPhoneNumber());
        
        return PayoutResponse.builder()
                .payoutId(UUID.randomUUID().toString())
                .status("PROCESSING")
                .amount(request.getAmount())
                .phoneNumber(request.getPhoneNumber())
                .provider(request.getProvider())
                .createdAt(LocalDateTime.now())
                .estimatedCompletion(LocalDateTime.now().plusMinutes(5))
                .build();
    }
    
    private WalletResponse mapToWalletResponse(Wallet wallet) {
        return WalletResponse.builder()
                .userId(wallet.getUserId())
                .balanceUsd(wallet.getBalanceUsd())
                .totalEarned(wallet.getTotalEarned())
                .totalRedeemed(wallet.getTotalRedeemed())
                .lastUpdated(wallet.getUpdatedAt())
                .build();
    }
}
