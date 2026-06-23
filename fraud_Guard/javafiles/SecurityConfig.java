package com.fraudguard.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JwtUtil - JWT token generation and validation
 */
@Component
@Slf4j
public class JwtUtil {
    
    @Value("${jwt.secret:your-super-secret-jwt-key-change-in-production-min-32-chars}")
    private String jwtSecret;
    
    @Value("${jwt.expiration.ms:900000}")
    private long accessTokenExpirationMs; // 15 minutes default
    
    @Value("${jwt.refresh-expiration.ms:604800000}")
    private long refreshTokenExpirationMs; // 7 days default
    
    /**
     * Generate access token
     */
    public String generateAccessToken(String userId, String email) {
        return createToken(userId, email, accessTokenExpirationMs);
    }
    
    /**
     * Generate refresh token
     */
    public String generateRefreshToken(String userId) {
        return createToken(userId, userId, refreshTokenExpirationMs);
    }
    
    private String createToken(String userId, String subject, long expirationMs) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMs);
        
        return Jwts.builder()
                .subject(subject)
                .claim("userId", userId)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8)))
                .compact();
    }
    
    /**
     * Extract user ID from token
     */
    public String extractUserIdFromToken(String token) {
        return getClaimsFromToken(token).get("userId", String.class);
    }
    
    /**
     * Extract subject/email from token
     */
    public String extractSubjectFromToken(String token) {
        return getClaimsFromToken(token).getSubject();
    }
    
    /**
     * Validate token
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8)))
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.error("JWT validation error: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Check if token is expired
     */
    public boolean isTokenExpired(String token) {
        try {
            return getClaimsFromToken(token).getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }
    
    /**
     * Get all claims from token
     */
    private Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
    
    public long getAccessTokenExpirationMs() {
        return accessTokenExpirationMs;
    }
}

/**
 * JwtAuthenticationFilter - Intercepts requests and validates JWT tokens
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final JwtUtil jwtUtil;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                   HttpServletResponse response, 
                                   FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = extractToken(request);
            
            if (token != null && jwtUtil.validateToken(token)) {
                String userId = jwtUtil.extractUserIdFromToken(token);
                
                // Create authentication
                UsernamePasswordAuthenticationToken authentication = 
                        new UsernamePasswordAuthenticationToken(userId, null, new ArrayList<>());
                
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            log.error("JWT filter error: {}", e.getMessage());
        }
        
        filterChain.doFilter(request, response);
    }
    
    /**
     * Extract JWT token from Authorization header
     */
    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}

/**
 * SecurityConfig - Spring Security configuration
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {
    
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authz -> authz
                // Public endpoints
                .requestMatchers("/api/v1/auth/**").permitAll()
                .requestMatchers("/api/v1/health").permitAll()
                .requestMatchers("/swagger-ui/**").permitAll()
                .requestMatchers("/v3/api-docs/**").permitAll()
                .requestMatchers("/api/v1/ussd/**").permitAll() // USSD webhook
                // Protected endpoints
                .anyRequest().authenticated()
            )
            .exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setContentType("application/json");
                    response.setStatus(401);
                    response.getWriter().write("{\"error\":\"Unauthorized\",\"message\":\"" + authException.getMessage() + "\"}");
                })
            );
        
        // Add JWT filter
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    
    /**
     * Custom UserDetailsService for loading users by email
     */
    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return email -> {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
            
            return new org.springframework.security.core.userdetails.User(
                    user.getEmail(),
                    user.getPasswordHash(),
                    new ArrayList<>()
            );
        };
    }
}

/**
 * Custom exceptions for security/auth errors
 */
@Slf4j
public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException(String message) {
        super(message);
        log.warn("Invalid token: {}", message);
    }
}

@Slf4j
public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
        log.warn("Unauthorized: {}", message);
    }
}
