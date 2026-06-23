package com.fraudguard.controller;

import com.fraudguard.dto.*;
import com.fraudguard.security.JwtUtil;
import com.fraudguard.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * AuthController - Authentication endpoints (register, login, refresh)
 */
@RestController
@RequestMapping("/api/v1/auth")
@Slf4j
@RequiredArgsConstructor
public class AuthController {
    
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    
    /**
     * Register a new user
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        UserResponse user = userService.registerUser(request);
        
        // Generate tokens
        String accessToken = jwtUtil.generateAccessToken(user.getId(), user.getEmail());
        String refreshToken = jwtUtil.generateRefreshToken(user.getId());
        
        AuthResponse response = AuthResponse.builder()
                .user(user)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtUtil.getAccessTokenExpirationMs() / 1000)
                .build();
        
        log.info("User registered: {}", user.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * Login with email and password
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        // Authenticate user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        // Get user details
        UserResponse user = userService.mapToUserResponse(
                userService.getUserByEmail(request.getEmail())
        );
        
        // Generate tokens
        String accessToken = jwtUtil.generateAccessToken(user.getId(), user.getEmail());
        String refreshToken = jwtUtil.generateRefreshToken(user.getId());
        
        // Record last login
        userService.recordLastLogin(user.getId());
        
        AuthResponse response = AuthResponse.builder()
                .user(user)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtUtil.getAccessTokenExpirationMs() / 1000)
                .build();
        
        log.info("User logged in: {}", user.getEmail());
        return ResponseEntity.ok(response);
    }
    
    /**
     * Refresh access token
     */
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        String userId = jwtUtil.extractUserIdFromToken(request.getRefreshToken());
        
        if (!jwtUtil.validateToken(request.getRefreshToken())) {
            throw new InvalidTokenException("Refresh token is invalid or expired");
        }
        
        // Get user
        UserResponse user = userService.mapToUserResponse(userService.getUserById(userId));
        
        // Generate new access token
        String accessToken = jwtUtil.generateAccessToken(user.getId(), user.getEmail());
        
        AuthResponse response = AuthResponse.builder()
                .user(user)
                .accessToken(accessToken)
                .refreshToken(request.getRefreshToken())
                .tokenType("Bearer")
                .expiresIn(jwtUtil.getAccessTokenExpirationMs() / 1000)
                .build();
        
        return ResponseEntity.ok(response);
    }
}

/**
 * UserController - User profile endpoints
 */
@RestController
@RequestMapping("/api/v1/user")
@Slf4j
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    private final JwtUtil jwtUtil;
    
    /**
     * Get current user profile
     */
    @GetMapping("/profile")
    public ResponseEntity<UserResponse> getUserProfile() {
        String userId = getCurrentUserId();
        UserResponse user = userService.mapToUserResponse(userService.getUserById(userId));
        return ResponseEntity.ok(user);
    }
    
    /**
     * Update user profile
     */
    @PutMapping("/profile")
    public ResponseEntity<UserResponse> updateUserProfile(
            @Valid @RequestBody UpdateUserRequest request) {
        String userId = getCurrentUserId();
        UserResponse user = userService.updateUser(userId, request);
        return ResponseEntity.ok(user);
    }
    
    /**
     * Get user by ID (public)
     */
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUser(@PathVariable String userId) {
        UserResponse user = userService.mapToUserResponse(userService.getUserById(userId));
        return ResponseEntity.ok(user);
    }
    
    private String getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (String) auth.getPrincipal();
    }
}

/**
 * ScenarioController - Scenario and learning endpoints
 */
@RestController
@RequestMapping("/api/v1/scenarios")
@Slf4j
@RequiredArgsConstructor
public class ScenarioController {
    
    private final ScenarioService scenarioService;
    
    /**
     * Get list of scenarios (paginated)
     */
    @GetMapping
    public ResponseEntity<Page<ScenarioResponse>> getScenarios(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String difficulty) {
        
        Pageable pageable = PageRequest.of(page, size);
        
        Page<ScenarioResponse> scenarios;
        if (difficulty != null && !difficulty.isEmpty()) {
            scenarios = scenarioService.getScenariosByDifficulty(
                    Scenario.Difficulty.valueOf(difficulty.toUpperCase()), pageable);
        } else {
            scenarios = scenarioService.getScenarios(pageable);
        }
        
        return ResponseEntity.ok(scenarios);
    }
    
    /**
     * Get single scenario
     */
    @GetMapping("/{scenarioId}")
    public ResponseEntity<ScenarioResponse> getScenario(@PathVariable String scenarioId) {
        ScenarioResponse scenario = scenarioService.getScenarioById(scenarioId);
        return ResponseEntity.ok(scenario);
    }
    
    /**
     * Submit answer to a scenario
     */
    @PostMapping("/{scenarioId}/answer")
    public ResponseEntity<AnswerResultResponse> submitAnswer(
            @PathVariable String scenarioId,
            @Valid @RequestBody SubmitAnswerRequest request) {
        
        String userId = getCurrentUserId();
        request.setScenarioId(scenarioId);
        
        AnswerResultResponse result = scenarioService.submitAnswer(userId, request);
        return ResponseEntity.ok(result);
    }
    
    private String getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (String) auth.getPrincipal();
    }
}

/**
 * WalletController - Wallet and payout endpoints
 */
@RestController
@RequestMapping("/api/v1/wallet")
@Slf4j
@RequiredArgsConstructor
public class WalletController {
    
    private final WalletService walletService;
    
    /**
     * Get user's wallet balance
     */
    @GetMapping
    public ResponseEntity<WalletResponse> getWallet() {
        String userId = getCurrentUserId();
        WalletResponse wallet = walletService.getWallet(userId);
        return ResponseEntity.ok(wallet);
    }
    
    /**
     * Request airtime payout
     */
    @PostMapping("/payout")
    public ResponseEntity<PayoutResponse> requestPayout(
            @Valid @RequestBody RequestPayoutRequest request) {
        String userId = getCurrentUserId();
        PayoutResponse payout = walletService.requestPayout(userId, request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(payout);
    }
    
    private String getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (String) auth.getPrincipal();
    }
}

/**
 * HealthController - System health check
 */
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class HealthController {
    
    @GetMapping("/health")
    public ResponseEntity<?> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "timestamp", LocalDateTime.now()
        ));
    }
}
