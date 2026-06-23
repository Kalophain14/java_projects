package com.fraudguard.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Wallet entity representing a user's reward balance
 * Tracks earned and redeemed airtime
 */
@Entity
@Table(name = "wallets", indexes = {
    @Index(name = "idx_user_id", columnList = "user_id", unique = true),
    @Index(name = "idx_balance", columnList = "balance_usd")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Wallet {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "user_id", length = 36, nullable = false, unique = true)
    private String userId;

    @Column(nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal balanceUsd = BigDecimal.ZERO;

    @Column(nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal totalEarned = BigDecimal.ZERO;

    @Column(nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal totalRedeemed = BigDecimal.ZERO;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isLocked = false;

    @Column(name = "locked_until")
    private LocalDateTime lockedUntil;

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
     * Add earned amount to wallet
     */
    public void addEarning(BigDecimal amount) {
        balanceUsd = balanceUsd.add(amount);
        totalEarned = totalEarned.add(amount);
    }

    /**
     * Deduct redeemed amount from wallet
     */
    public boolean deductRedemption(BigDecimal amount) {
        if (balanceUsd.compareTo(amount) < 0) {
            return false; // Insufficient balance
        }
        balanceUsd = balanceUsd.subtract(amount);
        totalRedeemed = totalRedeemed.add(amount);
        return true;
    }

    /**
     * Check if wallet has sufficient balance
     */
    public boolean hasSufficientBalance(BigDecimal amount) {
        return balanceUsd.compareTo(amount) >= 0;
    }

    /**
     * Lock wallet for concurrent payout prevention (30 seconds)
     */
    public void lockForPayout() {
        isLocked = true;
        lockedUntil = LocalDateTime.now().plusSeconds(30);
    }

    /**
     * Unlock wallet
     */
    public void unlock() {
        isLocked = false;
        lockedUntil = null;
    }

    /**
     * Check if wallet is currently locked
     */
    public boolean isCurrentlyLocked() {
        if (!isLocked) return false;
        if (lockedUntil != null && LocalDateTime.now().isAfter(lockedUntil)) {
            unlock();
            return false;
        }
        return isLocked;
    }
}
