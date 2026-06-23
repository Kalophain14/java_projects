# FraudGuard Project Management - GitHub Issues Board

## 📋 Project Board Structure (4 Columns)

```
┌─────────────┬──────────────┬────────────┬──────────────┐
│  Backlog    │  In Progress │  In Review │  Done        │
├─────────────┼──────────────┼────────────┼──────────────┤
│ Feature A   │ Feature B    │ Bug Fix 1  │ Release v0.1 │
│ Feature C   │ Task 2       │ Feature D  │ API v1.0     │
│ Bug 1       │              │            │              │
└─────────────┴──────────────┴────────────┴──────────────┘
```

---

## 🗂️ Issue Labels & Categories

### By Type
- `feature` - New functionality
- `bug` - Code defect
- `chore` - Maintenance, refactoring
- `documentation` - README, API docs
- `infrastructure` - DevOps, deployment

### By Priority
- `P0-critical` - Blocks release, security risk
- `P1-high` - Sprint must-have
- `P2-medium` - Nice to have this sprint
- `P3-low` - Backlog for later

### By Module
- `backend` - Spring Boot engine
- `ussd` - USSD/SMS handler
- `mobile` - Flutter app
- `database` - MySQL schema/migrations
- `devops` - CI/CD, Docker

### By Phase
- `phase-1-mvp` - Weeks 1-8 (core learning loop)
- `phase-2-telecom` - Weeks 9-16 (Africa's Talking integration)
- `phase-3-monetization` - Weeks 17+ (partnerships, payouts)

---

## 📊 Phase 1: MVP (Weeks 1-8)

### Week 1-2: Foundation & Setup
- [ ] **#101** - Set up GitHub repository with Maven multi-module structure
  - Labels: `phase-1-mvp` `backend` `chore`
  - Acceptance: All 3 modules build independently, CI/CD passes
  
- [ ] **#102** - Configure Spring Boot 3.x skeleton with REST endpoints
  - Labels: `phase-1-mvp` `backend` `feature`
  - Tasks:
    - Create UserController.java (POST /register, POST /login)
    - Create ScenarioController.java (GET /scenarios)
    - Spring Security with JWT tokens
    - Acceptance: Postman collection works

- [ ] **#103** - Design & implement MySQL database schema (User, Scenario, Wallet, Transaction)
  - Labels: `phase-1-mvp` `database` `feature`
  - Acceptance: All tables created, foreign keys validated, sample data inserted

- [ ] **#104** - Set up Docker Compose for local development (MySQL + Spring Boot)
  - Labels: `phase-1-mvp` `devops` `chore`
  - Acceptance: `docker-compose up` launches full stack, accessible on localhost:8080

- [ ] **#105** - Create Flutter project structure with basic navigation
  - Labels: `phase-1-mvp` `mobile` `chore`
  - Tasks:
    - Initialize pubspec.yaml dependencies (http, riverpod, etc.)
    - Create login screen UI
    - Create main app navigation scaffold
    - Acceptance: App builds & runs on Android emulator

---

### Week 3-4: Core Learning Loop
- [ ] **#201** - Build 5 fraud scenario templates (WhatsApp scam, phishing email, fake banking portal, SMS vishing, fake support call)
  - Labels: `phase-1-mvp` `backend` `feature`
  - Tasks:
    - Create Scenario entity with fields: title, description, imageUrl, correctAnswer, explanation
    - Seed database with 5 scenarios
    - GET /scenarios endpoint returns paginated list
  - Acceptance: Postman GET /scenarios returns 5 scenarios with images

- [ ] **#202** - Implement scenario answer validation & scoring logic
  - Labels: `phase-1-mvp` `backend` `feature`
  - Tasks:
    - POST /scenarios/{id}/answer endpoint
    - Validate user answer, return correctness + explanation
    - Increment user score/streak
  - Acceptance: Correct answer returns `{"isCorrect": true}`, incorrect returns `{"isCorrect": false}`

- [ ] **#203** - Build Mobile Scenario Visualizer UI with interactive hotspots
  - Labels: `phase-1-mvp` `mobile` `feature`
  - Tasks:
    - Create ScenarioScreen widget (displays scenario image + description)
    - Add tap detection on clickable UI elements
    - Show result popup (correct/incorrect + explanation)
    - Animated streak counter
  - Acceptance: Can tap hotspot, see feedback, streak updates

- [ ] **#204** - Implement Mobile Wallet screen (balance display + transaction history)
  - Labels: `phase-1-mvp` `mobile` `feature`
  - Acceptance: Displays $0.00 balance, empty transaction list initially

- [ ] **#205** - Create Mobile Leaderboard screen (basic regional rankings)
  - Labels: `phase-1-mvp` `mobile` `feature`
  - Tasks:
    - GET /leaderboard endpoint returns top 100 users by score
    - Mobile UI shows rank, username, score
  - Acceptance: Can scroll leaderboard, see rankings

---

### Week 5-6: User Management & Authentication
- [ ] **#301** - Implement JWT authentication flow (register, login, token refresh)
  - Labels: `phase-1-mvp` `backend` `feature` `P0-critical`
  - Tasks:
    - POST /auth/register - creates user, returns JWT
    - POST /auth/login - validates credentials, returns JWT
    - JWT refresh token logic
    - Add @Secured annotations to protected endpoints
  - Acceptance: Can register → login → access /scenarios with Authorization header

- [ ] **#302** - Add user profile management (update name, region, preferences)
  - Labels: `phase-1-mvp` `backend` `feature`
  - Acceptance: GET/PUT /user/{id} works

- [ ] **#303** - Implement Flutter authentication UI (login screen, register form)
  - Labels: `phase-1-mvp` `mobile` `feature`
  - Tasks:
    - Login form with email/password validation
    - Registration form
    - Secure token storage (using flutter_secure_storage)
    - Auto-login if token valid
  - Acceptance: Can register & login, token persisted across app restarts

- [ ] **#304** - Add user preferences storage (region selection for leaderboard filtering)
  - Labels: `phase-1-mvp` `mobile` `feature`
  - Acceptance: Can select region, leaderboard filters by region

---

### Week 7-8: Testing, Documentation & MVP Release
- [ ] **#401** - Write unit tests for backend (UserService, ScenarioService, WalletService)
  - Labels: `phase-1-mvp` `backend` `chore` `P1-high`
  - Target: 70% code coverage
  - Acceptance: `mvn test` passes all tests

- [ ] **#402** - Write integration tests for REST API endpoints
  - Labels: `phase-1-mvp` `backend` `chore` `P1-high`
  - Acceptance: Postman/RestAssured tests verify all CRUD operations

- [ ] **#403** - Write Flutter widget tests for main screens
  - Labels: `phase-1-mvp` `mobile` `chore`
  - Acceptance: `flutter test` passes

- [ ] **#404** - Create API documentation (OpenAPI/Swagger)
  - Labels: `phase-1-mvp` `documentation` `feature`
  - Acceptance: Swagger UI accessible at /swagger-ui.html, all endpoints documented

- [ ] **#405** - Create Database Schema documentation with ER diagrams
  - Labels: `phase-1-mvp` `documentation` `feature`
  - Acceptance: README includes schema explanation + visual diagram

- [ ] **#406** - Create GETTING_STARTED.md with setup instructions
  - Labels: `phase-1-mvp` `documentation` `feature`
  - Acceptance: New developer can clone → `docker-compose up` → app runs locally

- [ ] **#407** - Deploy MVP to staging (AWS/DigitalOcean)
  - Labels: `phase-1-mvp` `devops` `P0-critical`
  - Acceptance: App accessible at staging.fraudguard.com

- [ ] **#408** - Create v0.1 release & tag GitHub
  - Labels: `phase-1-mvp` `chore`
  - Acceptance: GitHub release published with changelog

---

## 📊 Phase 2: Telecom Integration (Weeks 9-16)

### Week 9-10: USSD Foundation
- [ ] **#501** - Set up Africa's Talking API credentials & sandbox testing
  - Labels: `phase-2-telecom` `ussd` `chore` `P0-critical`

- [ ] **#502** - Build USSD Menu Router state machine (UssdMenuRouter.java)
  - Labels: `phase-2-telecom` `ussd` `feature` `P0-critical`
  - Tasks:
    - Handle USSD session initiation
    - Route menu selections (1, 2, 3, etc.)
    - Manage session state (welcome → choose scenario → answer → confirmation)
  - Acceptance: Can dial *120*123# on real SIM, see menu

- [ ] **#503** - Integrate Africa's Talking USSD API for webhook handling
  - Labels: `phase-2-telecom` `ussd` `feature` `P0-critical`
  - POST /ussd/callback receives session updates
  - Acceptance: USSD requests routed to backend correctly

---

### Week 11-12: SMS & Notifications
- [ ] **#601** - Implement SmsNotifier.java for reward notifications
  - Labels: `phase-2-telecom` `ussd` `feature`
  - Tasks:
    - Send SMS on correct answer (include airtime balance)
    - Send educational reminder SMSes
  - Acceptance: Can receive test SMS

- [ ] **#602** - Create text-based fraud scenarios for USSD
  - Labels: `phase-2-telecom` `ussd` `feature`
  - Example: Vishing scenario (fake bank call transcript), Smishing scenario (SMS phishing)
  - Acceptance: 5+ USSD scenarios created

---

### Week 13-14: Dummy Payouts & Testing
- [ ] **#701** - Implement dummy airtime payout logic (no real money yet)
  - Labels: `phase-2-telecom` `backend` `feature`
  - POST /wallet/request-payout (returns success but doesn't charge)
  - Acceptance: Can request payout in staging, no actual debit

- [ ] **#702** - Test USSD flow end-to-end on feature phones
  - Labels: `phase-2-telecom` `ussd` `P1-high`
  - Real device testing on MTN/Vodacom
  - Acceptance: Full USSD session completes without timeout

---

### Week 15-16: Real Payouts & Deployment
- [ ] **#801** - Integrate real Africa's Talking airtime payouts with message queue
  - Labels: `phase-2-telecom` `backend` `feature` `P0-critical`
  - Tasks:
    - Connect to payout API (start with small amounts: $0.10)
    - Implement Redis queue for retry logic
    - Add exponential backoff
  - Acceptance: Can successfully payout $0.10 in staging

- [ ] **#802** - Add payout failure handling & notifications
  - Labels: `phase-2-telecom` `backend` `feature` `P0-critical`
  - User notified via SMS if payout fails
  - Admin dashboard shows failed transactions

- [ ] **#803** - Deploy Phase 2 to production (with pilot bank partner)
  - Labels: `phase-2-telecom` `devops` `P0-critical`
  - Acceptance: Pilot bank + region live with real payouts

---

## 📊 Phase 3: Monetization & Partnerships (Weeks 17+)

### Backlog Items
- [ ] **#901** - Bank co-branding UI templates
  - Labels: `phase-3-monetization` `mobile` `feature` `P2-medium`

- [ ] **#902** - Partner analytics dashboard (bank sees user engagement metrics)
  - Labels: `phase-3-monetization` `backend` `feature` `P2-medium`

- [ ] **#903** - Advanced gamification (daily streaks, badges, seasonal campaigns)
  - Labels: `phase-3-monetization` `mobile` `feature` `P2-medium`

- [ ] **#904** - Social sharing features (share achievement to WhatsApp/Facebook)
  - Labels: `phase-3-monetization` `mobile` `feature` `P3-low`

- [ ] **#905** - Fraud detection on payouts (detect abuse/bot accounts)
  - Labels: `phase-3-monetization` `backend` `feature` `P1-high`

---

## 🔄 Sprint Planning Template

### Sprint N (Weeks X-Y)
**Goal:** [Describe sprint objective]

| Issue ID | Title | Owner | Status | Est. Hours | Actual Hours |
|----------|-------|-------|--------|-----------|--------------|
| #101 | Setup repo | @dev1 | Done | 8 | 8 |
| #102 | Spring Boot skeleton | @dev2 | In Progress | 12 | 6 |
| #103 | Database schema | @dev3 | Backlog | 16 | 0 |

**Sprint Velocity:** XX points  
**Burndown:** [Track daily progress]

---

## 📅 Sample Sprint Standup Template

**Date:** Monday, Week X  
**Attendees:** @dev1, @dev2, @dev3  

### Dev1
- ✅ **Yesterday:** Completed issue #101 (repo setup)
- 🔄 **Today:** Starting issue #102 (Spring Boot skeleton)
- 🚧 **Blocker:** None

### Dev2
- ✅ **Yesterday:** 50% done with #102
- 🔄 **Today:** Finish Spring Security JWT implementation
- 🚧 **Blocker:** Waiting for database schema design (issue #103)

### Dev3
- ✅ **Yesterday:** Database ER diagram created
- 🔄 **Today:** Start #103 MySQL schema creation
- 🚧 **Blocker:** Need to clarify transaction logging requirements

**Action Items:**
- [ ] @dev3 clarifies transaction logging by EOD Wednesday
- [ ] @dev1 reviews @dev2's JWT implementation Thursday
- [ ] Schedule architecture review for Friday

---

## 🎯 Release Checklist

### Before v0.1 Release (End of Week 8)
- [ ] All Phase 1 issues closed
- [ ] Code coverage > 70%
- [ ] Documentation complete (API docs, setup guide, schema)
- [ ] Security audit completed (no hardcoded secrets, JWT validation)
- [ ] Performance tested (can handle 1K concurrent users)
- [ ] Staging deployment successful
- [ ] Pilot bank contract signed
- [ ] GitHub release created with changelog

### Before v0.2 Release (End of Week 16)
- [ ] All Phase 2 issues closed
- [ ] USSD tested on real feature phones
- [ ] Real payouts working in sandbox
- [ ] SMS notifications tested
- [ ] Production deployment green
- [ ] Pilot bank with 1K+ active users

---

## 📞 Issue Template for GitHub

```markdown
---
name: Feature Request
about: Suggest a feature for FraudGuard
title: '[FEATURE] '
labels: 'feature, P2-medium'
assignees: ''

---

## Description
[Describe the feature]

## Acceptance Criteria
- [ ] [Criterion 1]
- [ ] [Criterion 2]

## Phase
- [ ] Phase 1 (MVP)
- [ ] Phase 2 (Telecom)
- [ ] Phase 3 (Monetization)

## Module
- [ ] Backend
- [ ] Mobile
- [ ] USSD
- [ ] Database
- [ ] DevOps

## Estimated Hours
[Estimate]
```

---

## 🚨 Roadmap at a Glance

```
Week 1         Week 8         Week 16        Week 24+
├─────────────┤              ├──────────────┤              ├──────────
│   MVP       │              │   Telecom    │              │Monetization
│   Phase 1   │              │   Phase 2    │              │ Phase 3
├─────────────┴──────────────┴──────────────┴──────────────┴──────────
▲             ▲              ▲              ▲
Repo Setup    v0.1 Release   v0.2 Release   v1.0 Release
              + Pilot        + Real Payouts + Partnerships
```
