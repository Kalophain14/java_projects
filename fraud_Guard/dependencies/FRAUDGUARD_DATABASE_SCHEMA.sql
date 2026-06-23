-- FraudGuard Academy Database Schema (MySQL 8.0+)
-- This schema defines the core data model for the fraud prevention learning platform
-- Created: 2024-01-01
-- Version: 1.0.0

-- ============================================================================
-- Database Initialization
-- ============================================================================

CREATE DATABASE IF NOT EXISTS fraudguard_db
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE fraudguard_db;

-- ============================================================================
-- 1. USER MANAGEMENT TABLES
-- ============================================================================

CREATE TABLE IF NOT EXISTS users (
    id CHAR(36) PRIMARY KEY COMMENT 'UUID primary key',
    email VARCHAR(255) NOT NULL UNIQUE COMMENT 'User email (login identifier)',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT 'Unique username for display',
    password_hash VARCHAR(255) NOT NULL COMMENT 'Bcrypt hashed password',
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    phone_number VARCHAR(20) COMMENT 'Phone number for USSD/SMS',
    region VARCHAR(50) NOT NULL COMMENT 'Geographic region for leaderboard filtering',
    
    -- Performance metrics
    total_score INT DEFAULT 0 COMMENT 'Total cumulative score',
    current_streak INT DEFAULT 0 COMMENT 'Current consecutive correct answers',
    max_streak INT DEFAULT 0 COMMENT 'Highest streak achieved',
    scenarios_completed INT DEFAULT 0 COMMENT 'Total scenarios answered',
    correct_answers INT DEFAULT 0 COMMENT 'Total correct answers',
    
    -- Account status
    is_active BOOLEAN DEFAULT TRUE,
    is_email_verified BOOLEAN DEFAULT FALSE,
    last_login TIMESTAMP NULL,
    
    -- Metadata
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- Indexes for performance
    KEY idx_email (email),
    KEY idx_username (username),
    KEY idx_region (region),
    KEY idx_created_at (created_at),
    KEY idx_total_score (total_score DESC),
    
    COMMENT 'Core user profiles for the platform'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- User audit trail for compliance
CREATE TABLE IF NOT EXISTS user_audit_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id CHAR(36) NOT NULL,
    action VARCHAR(100) NOT NULL COMMENT 'LOGIN, LOGOUT, PROFILE_UPDATE, PASSWORD_CHANGE, etc.',
    ip_address VARCHAR(45),
    user_agent VARCHAR(500),
    details JSON,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    KEY idx_user_id (user_id),
    KEY idx_created_at (created_at),
    
    COMMENT 'Audit log for user actions and security events'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================================
-- 2. FRAUD SCENARIO TABLES
-- ============================================================================

CREATE TABLE IF NOT EXISTS scenarios (
    id CHAR(36) PRIMARY KEY COMMENT 'UUID primary key',
    title VARCHAR(255) NOT NULL,
    description LONGTEXT NOT NULL,
    scenario_type ENUM(
        'PHISHING_EMAIL',
        'FAKE_BANKING_APP',
        'WHATSAPP_SCAM',
        'SMS_PHISHING',
        'VISHING_CALL'
    ) NOT NULL COMMENT 'Type of fraud being taught',
    
    channel ENUM('MOBILE_APP', 'USSD', 'SMS') NOT NULL COMMENT 'Where scenario is delivered',
    difficulty ENUM('BEGINNER', 'INTERMEDIATE', 'ADVANCED') DEFAULT 'BEGINNER',
    
    -- Content assets
    image_url VARCHAR(500) COMMENT 'URL to scenario screenshot/image',
    video_url VARCHAR(500) COMMENT 'Optional video explaining the fraud',
    
    -- Learning content
    explanation LONGTEXT NOT NULL COMMENT 'Educational explanation for correct answer',
    learning_objectives JSON COMMENT 'Array of learning points',
    
    -- Reward
    reward_amount DECIMAL(10,2) DEFAULT 0.10 COMMENT 'Airtime reward in USD',
    
    -- Admin controls
    is_active BOOLEAN DEFAULT TRUE,
    is_featured BOOLEAN DEFAULT FALSE COMMENT 'Featured on home screen',
    difficulty_level INT DEFAULT 1 COMMENT 'Numeric difficulty (1-10) for sorting',
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    KEY idx_type (scenario_type),
    KEY idx_channel (channel),
    KEY idx_difficulty (difficulty),
    KEY idx_is_active (is_active),
    KEY idx_created_at (created_at),
    
    COMMENT 'Fraud scenarios for interactive learning'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Interactive hotspots for mobile scenarios
CREATE TABLE IF NOT EXISTS scenario_hotspots (
    id INT AUTO_INCREMENT PRIMARY KEY,
    scenario_id CHAR(36) NOT NULL,
    hotspot_label VARCHAR(255) NOT NULL COMMENT 'Text label on clickable area',
    hotspot_position JSON COMMENT 'x,y coordinates for UI rendering',
    is_correct_answer BOOLEAN NOT NULL COMMENT 'Whether clicking this is the correct response',
    explanation VARCHAR(500) COMMENT 'Feedback when user selects this hotspot',
    order_position INT DEFAULT 0 COMMENT 'Display order',
    
    FOREIGN KEY (scenario_id) REFERENCES scenarios(id) ON DELETE CASCADE,
    KEY idx_scenario_id (scenario_id),
    KEY idx_is_correct (is_correct_answer),
    
    COMMENT 'Interactive hotspots within fraud scenarios'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- USSD text-based scenario content
CREATE TABLE IF NOT EXISTS ussd_scenarios (
    id CHAR(36) PRIMARY KEY,
    scenario_id CHAR(36) NOT NULL UNIQUE,
    ussd_text LONGTEXT NOT NULL COMMENT 'Scenario text for feature phones (max 160 chars per message)',
    menu_options JSON NOT NULL COMMENT 'Array of USSD menu options',
    timeout_seconds INT DEFAULT 180,
    
    FOREIGN KEY (scenario_id) REFERENCES scenarios(id) ON DELETE CASCADE,
    KEY idx_scenario_id (scenario_id),
    
    COMMENT 'USSD-specific content for feature phone delivery'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================================
-- 3. USER INTERACTION TABLES
-- ============================================================================

CREATE TABLE IF NOT EXISTS user_scenario_attempts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id CHAR(36) NOT NULL,
    scenario_id CHAR(36) NOT NULL,
    
    -- Answer tracking
    hotspot_selected INT COMMENT 'Hotspot ID user clicked',
    is_correct BOOLEAN NOT NULL,
    answer_time_seconds INT COMMENT 'Time taken to answer',
    
    -- Reward tracking
    points_earned INT DEFAULT 0,
    airtime_earned DECIMAL(10,2) DEFAULT 0 COMMENT 'In USD',
    
    -- Metadata
    channel ENUM('MOBILE_APP', 'USSD', 'SMS'),
    device_info JSON,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (scenario_id) REFERENCES scenarios(id) ON DELETE RESTRICT,
    
    KEY idx_user_id (user_id),
    KEY idx_scenario_id (scenario_id),
    KEY idx_user_scenario (user_id, scenario_id),
    KEY idx_is_correct (is_correct),
    KEY idx_created_at (created_at),
    
    COMMENT 'User attempts at each scenario'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- User learning progress and milestones
CREATE TABLE IF NOT EXISTS user_progress (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id CHAR(36) NOT NULL UNIQUE,
    
    -- Progress metrics
    total_scenarios_attempted INT DEFAULT 0,
    total_scenarios_completed INT DEFAULT 0,
    total_correct INT DEFAULT 0,
    accuracy_percentage DECIMAL(5,2) DEFAULT 0.00,
    
    -- Streaks
    current_streak INT DEFAULT 0,
    streak_started_at TIMESTAMP NULL,
    max_streak INT DEFAULT 0,
    max_streak_date TIMESTAMP NULL,
    
    -- Learning level
    user_level ENUM('ROOKIE', 'LEARNER', 'EXPERT', 'MASTER') DEFAULT 'ROOKIE',
    level_progress_percentage INT DEFAULT 0,
    
    -- Daily metrics
    last_activity_date DATE,
    days_active INT DEFAULT 0 COMMENT 'Total days user has been active',
    current_week_points INT DEFAULT 0,
    current_month_points INT DEFAULT 0,
    
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    KEY idx_accuracy (accuracy_percentage DESC),
    KEY idx_level (user_level),
    
    COMMENT 'Aggregated user learning progress'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================================
-- 4. WALLET & TRANSACTION TABLES (Financial)
-- ============================================================================

CREATE TABLE IF NOT EXISTS wallets (
    id CHAR(36) PRIMARY KEY,
    user_id CHAR(36) NOT NULL UNIQUE,
    
    -- Balance tracking
    balance_usd DECIMAL(10,2) DEFAULT 0.00 COMMENT 'Current balance in USD',
    total_earned DECIMAL(10,2) DEFAULT 0.00 COMMENT 'Lifetime earnings',
    total_redeemed DECIMAL(10,2) DEFAULT 0.00 COMMENT 'Lifetime payouts',
    
    -- Lock for concurrent payout prevention
    is_locked BOOLEAN DEFAULT FALSE,
    locked_until TIMESTAMP NULL,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    KEY idx_user_id (user_id),
    KEY idx_balance (balance_usd DESC),
    
    COMMENT 'User reward wallet balances'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS transactions (
    id CHAR(36) PRIMARY KEY,
    user_id CHAR(36) NOT NULL,
    wallet_id CHAR(36) NOT NULL,
    
    -- Transaction type
    transaction_type ENUM('EARNED', 'REDEEMED', 'BONUS', 'REVERSAL', 'ADJUSTMENT') NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    
    -- Related references
    scenario_id CHAR(36) COMMENT 'If earned from scenario',
    payout_id CHAR(36) COMMENT 'If redeemed via payout',
    
    -- Metadata
    description VARCHAR(500),
    status ENUM('COMPLETED', 'PENDING', 'FAILED') DEFAULT 'COMPLETED',
    reference_number VARCHAR(100),
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (wallet_id) REFERENCES wallets(id) ON DELETE CASCADE,
    FOREIGN KEY (scenario_id) REFERENCES scenarios(id) ON DELETE SET NULL,
    
    KEY idx_user_id (user_id),
    KEY idx_type (transaction_type),
    KEY idx_created_at (created_at),
    KEY idx_user_type (user_id, transaction_type),
    
    COMMENT 'Transaction ledger for wallet operations'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================================
-- 5. PAYOUT TABLES (Africa's Talking Integration)
-- ============================================================================

CREATE TABLE IF NOT EXISTS payouts (
    id CHAR(36) PRIMARY KEY,
    user_id CHAR(36) NOT NULL,
    wallet_id CHAR(36) NOT NULL,
    
    -- Payout details
    amount DECIMAL(10,2) NOT NULL COMMENT 'Amount in USD',
    phone_number VARCHAR(20) NOT NULL,
    provider ENUM('MTN', 'VODACOM', 'CELL_C', 'TELKOM') NOT NULL,
    
    -- Status tracking
    status ENUM('PENDING', 'PROCESSING', 'COMPLETED', 'FAILED', 'REVERSED') DEFAULT 'PENDING',
    
    -- Africa's Talking integration
    africas_talking_id VARCHAR(100) COMMENT 'TransactionId from Africa\'s Talking',
    carrier_reference VARCHAR(100),
    
    -- Error handling
    error_code VARCHAR(50),
    error_message VARCHAR(500),
    retry_count INT DEFAULT 0,
    max_retries INT DEFAULT 3,
    
    -- Metadata
    requested_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (wallet_id) REFERENCES wallets(id) ON DELETE CASCADE,
    
    KEY idx_user_id (user_id),
    KEY idx_status (status),
    KEY idx_phone_number (phone_number),
    KEY idx_requested_at (requested_at),
    KEY idx_status_created (status, requested_at),
    
    COMMENT 'Airtime payout requests to Africa\'s Talking'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Payout event log for troubleshooting
CREATE TABLE IF NOT EXISTS payout_events (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    payout_id CHAR(36) NOT NULL,
    
    event_type ENUM(
        'CREATED',
        'QUEUED',
        'SUBMITTED',
        'CALLBACK_RECEIVED',
        'COMPLETED',
        'FAILED',
        'RETRY_SCHEDULED',
        'REVERSED'
    ) NOT NULL,
    
    status_code VARCHAR(50),
    message TEXT,
    metadata JSON,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (payout_id) REFERENCES payouts(id) ON DELETE CASCADE,
    KEY idx_payout_id (payout_id),
    KEY idx_created_at (created_at),
    
    COMMENT 'Event log for payout tracking and debugging'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================================
-- 6. USSD SESSION TABLES (Feature Phone Sessions)
-- ============================================================================

CREATE TABLE IF NOT EXISTS ussd_sessions (
    id VARCHAR(100) PRIMARY KEY COMMENT 'SessionId from Africa\'s Talking',
    user_id CHAR(36),
    
    phone_number VARCHAR(20) NOT NULL,
    network_code VARCHAR(10),
    
    -- Session state
    menu_depth INT DEFAULT 0 COMMENT 'Current step in USSD menu flow',
    session_state JSON COMMENT 'Session context (selected scenario, answer, etc.)',
    
    -- Scenario context
    current_scenario_id CHAR(36),
    
    is_active BOOLEAN DEFAULT TRUE,
    
    started_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_activity TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    ended_at TIMESTAMP NULL,
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
    FOREIGN KEY (current_scenario_id) REFERENCES scenarios(id) ON DELETE SET NULL,
    
    KEY idx_phone_number (phone_number),
    KEY idx_user_id (user_id),
    KEY idx_started_at (started_at),
    
    COMMENT 'Active USSD session tracking'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================================
-- 7. LEADERBOARD & RANKINGS TABLES
-- ============================================================================

CREATE TABLE IF NOT EXISTS leaderboard_snapshots (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id CHAR(36) NOT NULL,
    
    rank_global INT,
    rank_region VARCHAR(50),
    
    total_score INT NOT NULL,
    current_streak INT,
    correct_answers INT,
    
    badge ENUM('ROOKIE', 'LEARNER', 'EXPERT', 'MASTER'),
    
    snapshot_date DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    
    KEY idx_user_id (user_id),
    KEY idx_rank_global (rank_global),
    KEY idx_snapshot_date (snapshot_date),
    KEY idx_region (rank_region),
    
    COMMENT 'Daily leaderboard snapshots for historical tracking'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================================
-- 8. PARTNER & SPONSORSHIP TABLES (B2B2C)
-- ============================================================================

CREATE TABLE IF NOT EXISTS bank_partners (
    id CHAR(36) PRIMARY KEY,
    bank_name VARCHAR(255) NOT NULL UNIQUE,
    bank_logo_url VARCHAR(500),
    
    -- API credentials (encrypted in application)
    api_key_encrypted VARCHAR(500),
    webhook_url VARCHAR(500),
    
    -- Budget & tracking
    monthly_reward_budget DECIMAL(10,2),
    spent_this_month DECIMAL(10,2) DEFAULT 0,
    
    is_active BOOLEAN DEFAULT TRUE,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    KEY idx_bank_name (bank_name),
    KEY idx_is_active (is_active),
    
    COMMENT 'Partner banks sponsoring the learning platform'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================================
-- 9. ANALYTICS & REPORTING TABLES
-- ============================================================================

CREATE TABLE IF NOT EXISTS analytics_daily (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    
    date_key DATE NOT NULL,
    
    -- User metrics
    total_users INT DEFAULT 0,
    new_users INT DEFAULT 0,
    active_users INT DEFAULT 0,
    
    -- Engagement metrics
    scenarios_completed INT DEFAULT 0,
    total_points_earned DECIMAL(10,2) DEFAULT 0,
    total_airtime_redeemed DECIMAL(10,2) DEFAULT 0,
    
    -- Channel metrics
    mobile_app_users INT DEFAULT 0,
    ussd_users INT DEFAULT 0,
    sms_users INT DEFAULT 0,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    UNIQUE KEY idx_date (date_key),
    
    COMMENT 'Daily aggregated analytics'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================================
-- 10. CONFIGURATION TABLES
-- ============================================================================

CREATE TABLE IF NOT EXISTS system_config (
    config_key VARCHAR(100) PRIMARY KEY,
    config_value LONGTEXT,
    description VARCHAR(500),
    is_encrypted BOOLEAN DEFAULT FALSE,
    
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    COMMENT 'System configuration key-value store'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Insert default configuration
INSERT INTO system_config (config_key, config_value, description) VALUES
('africas_talking_api_key', '', 'API key for Africa\'s Talking (encrypted)', TRUE),
('africas_talking_api_username', 'fraudguard_sandbox', 'API username for Africa\'s Talking', FALSE),
('min_payout_amount', '0.10', 'Minimum airtime payout amount in USD', FALSE),
('max_payout_per_day', '50.00', 'Maximum total payout per user per day', FALSE),
('reward_multiplier', '1.0', 'Multiplier for all rewards', FALSE),
('maintenance_mode', 'false', 'Enable maintenance mode', FALSE);

-- ============================================================================
-- 11. SAMPLE DATA (for development/testing)
-- ============================================================================

INSERT INTO users (id, email, username, password_hash, first_name, last_name, region, is_email_verified) VALUES
('550e8400-e29b-41d4-a716-446655440001', 'test@fraudguard.com', 'testuser', '$2a$10$N9qo8uLOickgx2ZMRZoMye', 'Test', 'User', 'Western Cape', TRUE);

INSERT INTO scenarios (id, title, scenario_type, channel, difficulty, explanation, reward_amount) VALUES
('660e8400-e29b-41d4-a716-446655440001', 
 'WhatsApp Business Scam', 
 'WHATSAPP_SCAM', 
 'MOBILE_APP', 
 'BEGINNER', 
 'Banks never request OTPs via WhatsApp. Always verify through official channels.',
 0.10);

INSERT INTO wallets (id, user_id, balance_usd, total_earned) VALUES
('770e8400-e29b-41d4-a716-446655440001', '550e8400-e29b-41d4-a716-446655440001', 0.00, 0.00);

INSERT INTO user_progress (user_id, total_scenarios_attempted, user_level) VALUES
('550e8400-e29b-41d4-a716-446655440001', 0, 'ROOKIE');

-- ============================================================================
-- DATABASE TRIGGERS & STORED PROCEDURES
-- ============================================================================

-- Trigger to update user streak on new attempt
DELIMITER //

CREATE TRIGGER update_streak_on_attempt
AFTER INSERT ON user_scenario_attempts
FOR EACH ROW
BEGIN
    IF NEW.is_correct THEN
        UPDATE user_progress 
        SET current_streak = current_streak + 1 
        WHERE user_id = NEW.user_id;
    ELSE
        UPDATE user_progress 
        SET current_streak = 0 
        WHERE user_id = NEW.user_id;
    END IF;
END//

DELIMITER ;

-- ============================================================================
-- CREATE INDEXES FOR QUERY OPTIMIZATION
-- ============================================================================

CREATE INDEX idx_user_attempts_date ON user_scenario_attempts(user_id, created_at);
CREATE INDEX idx_transactions_balance ON transactions(user_id, transaction_type, created_at);
CREATE INDEX idx_payout_status_user ON payouts(user_id, status, requested_at);
CREATE INDEX idx_ussd_active ON ussd_sessions(is_active, last_activity);

-- ============================================================================
-- DATABASE GRANTS FOR APPLICATION USER
-- ============================================================================

-- Uncomment when deploying to production
-- CREATE USER 'fraudguard_app'@'localhost' IDENTIFIED BY 'SecurePassword123!';
-- GRANT SELECT, INSERT, UPDATE, DELETE, EXECUTE ON fraudguard_db.* TO 'fraudguard_app'@'localhost';
-- FLUSH PRIVILEGES;

-- ============================================================================
-- END OF SCHEMA
-- ============================================================================
