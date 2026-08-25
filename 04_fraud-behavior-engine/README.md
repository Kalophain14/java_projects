# Financial Behaviour Monitoring Engine

A rule-based financial behaviour monitoring system designed to identify unusual transactional patterns and account activity.

The application analyses customer transactions and account behaviour in near real time, scoring risk and generating alerts for review.

This project is inspired by common financial risk management, fraud monitoring, and compliance concepts used within financial institutions.

---

# Technology Stack

* **Java 21**
* **Spring Boot 3**
* **PostgreSQL** (transaction and alert persistence)
* **Apache Kafka** (transaction event stream ingestion)
* **Redis** (rolling-window state and rule caching)
* **Docker** (containerised local development and deployment)

---

# Project Structure

```text
financial-behaviour-monitoring-engine/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/kalophain14/finbehaviourengine/
│   │   │       ├── ingestion/
│   │   │       │   └── TransactionEventConsumer.java
│   │   │       │
│   │   │       ├── scoring/
│   │   │       │   ├── RiskScoringService.java
│   │   │       │   └── RiskScore.java
│   │   │       │
│   │   │       ├── rules/
│   │   │       │   ├── RuleEngine.java
│   │   │       │   ├── Rule.java
│   │   │       │   ├── SalaryShieldingRule.java
│   │   │       │   ├── MinorAccountActivityRule.java
│   │   │       │   ├── StokvelActivityRule.java
│   │   │       │   ├── StructuringDetectionRule.java
│   │   │       │   └── DormantAccountRule.java
│   │   │       │
│   │   │       ├── state/
│   │   │       │   ├── AccountStateService.java
│   │   │       │   └── RedisStateRepository.java
│   │   │       │
│   │   │       ├── alert/
│   │   │       │   ├── AlertService.java
│   │   │       │   └── Alert.java
│   │   │       │
│   │   │       ├── model/
│   │   │       │   ├── Transaction.java
│   │   │       │   └── Account.java
│   │   │       │
│   │   │       ├── config/
│   │   │       │   └── RuleConfig.java
│   │   │       │
│   │   │       └── FinancialBehaviourMonitoringEngineApplication.java
│   │   │
│   │   └── resources/
│   │       ├── application.yml
│   │       └── rules.yml
│   │
│   └── test/
│       └── java/
│
├── docker/
│   ├── docker-compose.yml
│   └── Dockerfile
│
├── logs/
│
├── README.md
└── pom.xml
```

---

# Architecture

```text
                    Core Banking / Payments
                            │
                            ▼
                    Kafka Topic: transactions
                            │
                            ▼
              TransactionEventConsumer (Java)
                            │
                            ▼
                  AccountStateService ◄──────── Redis
                  (rolling windows, history)
                            │
                            ▼
                       RuleEngine
                            │
              ┌─────────────┼─────────────┐
              ▼             ▼             ▼
      SalaryShielding  Structuring   StokvelActivity   ...
              │             │             │
              └─────────────┼─────────────┘
                            ▼
                    RiskScoringService
                            │
                            ▼
                       AlertService
                            │
                  ┌─────────┴─────────┐
                  ▼                   ▼
            PostgreSQL           Downstream
          (alert history)      (review queue /
                                Kafka: alerts)
```

Incoming transaction events are consumed from Kafka and enriched with account state (recent history, rolling totals) held in Redis. Each transaction is evaluated against the full set of configured rules; any rule that fires contributes to the account's aggregate risk score. Alerts above the configured threshold are persisted to PostgreSQL and published to a downstream `alerts` topic for consumption by a review queue or case management system.

---

# Features

## Behaviour Monitoring

* Transaction pattern analysis across a rolling window per account
* Composite risk scoring, combining signals from multiple rules
* Alert generation with severity levels
* Account activity monitoring (velocity, dormancy, turnover)

## Detection Rules

### Salary Shielding Detection

Detects scenarios where large portions of income are transferred out shortly after being deposited.

```text
Salary Deposited
      │
      ▼
Large Transfer Within 24 Hours
      │
      ▼
High Risk Alert
```

### Minor Account Business Activity Detection

Identifies potential commercial activity conducted through minor accounts.

Indicators:

* High transaction volumes
* Multiple unrelated payers
* Consistent turnover patterns

### Stokvel Activity Monitoring

Monitors accounts exhibiting group savings behaviour.

Indicators:

* Multiple incoming deposits from different sources
* Monthly recurring cycles
* Consolidated withdrawals

### Transaction Structuring Detection

Detects large amounts being fragmented into multiple smaller transactions to stay under reporting thresholds.

| Original Amount | Pattern Detected | Split Transactions |
| ---------------- | ----------------- | ------------------- |
| R50 000           | Structuring        | 10 × R5 000          |

### Dormant Account Monitoring

Identifies unusual activity on previously inactive accounts.

Indicators:

* Long inactivity period
* Significant incoming funds
* Increased transaction velocity following dormancy

---

# Configuration

Rule thresholds and weights are defined in `rules.yml`, so tuning detection sensitivity doesn't require a code change or redeploy.

Example:

```yaml
rules:
  salaryShielding:
    enabled: true
    windowHours: 24
    transferThresholdPct: 70
    weight: 30

  structuring:
    enabled: true
    reportingThreshold: 50000
    minSplitCount: 3
    windowHours: 24
    weight: 25

  dormantAccount:
    enabled: true
    inactivityDays: 90
    velocityMultiplier: 5
    weight: 20

alerting:
  scoreThreshold: 60
  topic: alerts
```

---

# Getting Started

## Prerequisites

* Java 21 (JDK)
* Maven 3.9+
* Docker and Docker Compose (for local Kafka, Redis, and PostgreSQL)

## Build

```bash
git clone https://github.com/<your-org>/financial-behaviour-monitoring-engine.git
cd financial-behaviour-monitoring-engine
mvn clean package
```

This produces an executable JAR at `target/financial-behaviour-monitoring-engine-<version>.jar`.

## Run local dependencies

```bash
cd docker
docker compose up -d
```

This starts local Kafka, Redis, and PostgreSQL instances configured to match `application.yml`.

## Configure

Edit `src/main/resources/rules.yml` to tune rule thresholds and weights (see the Configuration section above), and `application.yml` for datastore connection details.

## Run

```bash
java -jar target/financial-behaviour-monitoring-engine-<version>.jar
```

## Verify

Publish a test transaction event to the Kafka topic and confirm an alert is generated:

```bash
kafka-console-producer --topic transactions --bootstrap-server localhost:9092
```

```bash
kafka-console-consumer --topic alerts --bootstrap-server localhost:9092 --from-beginning
```

You should see a structured alert record for any transaction that triggers a rule.

---

# Core Components

## TransactionEventConsumer

* Consumes transaction events from the Kafka `transactions` topic
* Deserialises and validates incoming events
* Passes each transaction to the `RuleEngine` for evaluation

## AccountStateService

* Maintains rolling-window account state (recent transactions, totals, last-activity timestamp) in Redis
* Supplies historical context the rules need without hitting PostgreSQL on every event

## RuleEngine

* Evaluates each transaction against all enabled rules
* Aggregates individual rule outcomes into a composite risk score
* Each rule is implemented independently, so new rules can be added without touching existing ones

## AlertService

* Persists alerts above the configured threshold to PostgreSQL
* Publishes alerts to the downstream `alerts` Kafka topic for review/case management systems

---

# Logging

Every scored transaction is logged with structured metadata.

Example:

```json
{
  "timestamp": "2026-07-23T10:15:20Z",
  "transactionId": "txn_98213",
  "accountId": "acc_4471",
  "amount": 5000.00,
  "rulesTriggered": ["structuring", "dormantAccount"],
  "riskScore": 68,
  "decision": "alert_raised"
}
```

---

# Reliability

The application follows a **fail-safe** strategy for the alerting path.

If a rule fails to evaluate or the state store is unavailable:

* the error is logged with full context;
* the transaction is flagged for manual review rather than silently passing;
* a single rule failure never suppresses evaluation of the other configured rules.

---

# Deployment

The application is packaged as a Spring Boot executable JAR and deployed as a containerised service.

Example:

```bash
docker build -t financial-behaviour-monitoring-engine .
docker run -d --env-file .env financial-behaviour-monitoring-engine
```

---

# Future Enhancements

* Machine-learning-based anomaly scoring alongside rule-based detection
* Web-based case management dashboard for alert review
* Configurable rule hot-reloading without service restart

---

# Learning Objectives

This project demonstrates practical experience with:

* Java 21
* Spring Boot
* Event-driven architecture with Kafka
* Stateful stream processing patterns using Redis
* Rule-engine design and composite risk scoring
* PostgreSQL persistence
* Structured logging
* Unit testing
* Fraud/AML domain concepts (structuring, dormant accounts, group savings schemes)
* Enterprise application architecture
