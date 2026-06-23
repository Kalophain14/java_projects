# FraudGuard Academy - Getting Started Guide

Welcome! This guide will help you set up the FraudGuard project locally and get the application running in ~30 minutes.

---

## 📋 Prerequisites

Before you start, ensure you have these tools installed:

### Required Software
- **Java 21 LTS** - Download [Amazon Corretto 21](https://aws.amazon.com/corretto/) or [OpenJDK 21](https://jdk.java.net/21/)
  ```bash
  java -version  # Should show Java 21.x.x
  ```

- **Maven 3.9+** - [Download Maven](https://maven.apache.org/download.cgi)
  ```bash
  mvn --version  # Should show Maven 3.9.0 or higher
  ```

- **Docker & Docker Compose** - [Install Docker Desktop](https://www.docker.com/products/docker-desktop)
  ```bash
  docker --version && docker-compose --version
  ```

- **Git** - [Download Git](https://git-scm.com/)
  ```bash
  git --version
  ```

- **Node.js & npm** (for Flutter/frontend development)
  - [Download Node.js LTS](https://nodejs.org/)
  ```bash
  node --version && npm --version
  ```

- **Flutter SDK** (for mobile app development)
  - [Install Flutter](https://flutter.dev/docs/get-started/install)
  ```bash
  flutter --version
  ```

### Recommended IDEs
- **IntelliJ IDEA Community** (free) - Best for Java backend
- **Android Studio** - Best for Flutter mobile development
- **VS Code** - Lightweight alternative

### System Requirements
- **Disk Space:** 10GB minimum (for Docker images, Maven dependencies, Android emulator)
- **RAM:** 8GB minimum (16GB recommended for running backend + mobile emulator)
- **OS:** macOS, Linux, or Windows (WSL2 recommended for Windows)

---

## 🚀 Quick Start (5 minutes)

### 1. Clone the Repository

```bash
# Clone the fraudguard-workspace
git clone https://github.com/fraudguard/fraudguard-workspace.git
cd fraudguard-workspace

# Verify the multi-module structure
ls -la
# You should see:
# ├── fraudguard-backend/
# ├── fraudguard-ussd/
# ├── fraudguard-mobile-app/
# ├── .github/workflows/
# └── README.md
```

### 2. Build the Backend (Maven)

```bash
# Navigate to backend directory
cd fraudguard-backend

# Build the Spring Boot application
mvn clean install -DskipTests

# Output: BUILD SUCCESS
# (First build takes ~2 minutes - Maven downloads dependencies)
```

### 3. Start the Database & Backend (Docker Compose)

```bash
# From the root of fraudguard-workspace
docker-compose up -d

# Check if containers are running
docker-compose ps
# You should see:
# NAME                    COMMAND                  SERVICE      STATUS
# fraudguard-mysql        "docker-entrypoint..."   mysql        Up 2 seconds
# fraudguard-backend      "java -jar app.jar..."   backend      Up 3 seconds
```

### 4. Verify the API is Running

```bash
# Test the health check endpoint
curl http://localhost:8080/api/v1/health

# Expected response:
# {"status":"UP","timestamp":"2024-01-20T14:50:00Z"}
```

✅ **Backend is running!** You can now access:
- 📘 **API Documentation:** http://localhost:8080/swagger-ui.html
- 🏥 **Health Check:** http://localhost:8080/api/v1/health

---

## 🛠️ Full Setup Guide

### Step 1: Fork & Clone Repository

```bash
# Option A: Clone from main repo
git clone https://github.com/fraudguard/fraudguard-workspace.git fraudguard
cd fraudguard

# Option B: Fork on GitHub, then clone your fork
git clone https://github.com/YOUR_USERNAME/fraudguard-workspace.git
cd fraudguard-workspace

# Add upstream remote to sync with main repo
git remote add upstream https://github.com/fraudguard/fraudguard-workspace.git
```

### Step 2: Project Structure Overview

```
fraudguard-workspace/
├── fraudguard-backend/           # Spring Boot REST API
│   ├── src/main/java/com/fraudguard/
│   │   ├── config/               # Spring Security, Bean configurations
│   │   ├── controller/           # REST endpoints (@RestController)
│   │   ├── dto/                  # Data Transfer Objects
│   │   ├── entity/               # JPA entities (@Entity)
│   │   ├── repository/           # Database access (@Repository)
│   │   ├── service/              # Business logic (@Service)
│   │   └── exception/            # Custom exceptions
│   ├── src/main/resources/
│   │   ├── application.yml       # Spring Boot configuration
│   │   ├── application-dev.yml   # Development overrides
│   │   ├── application-prod.yml  # Production overrides
│   │   └── db/migration/         # Flyway/Liquibase migrations
│   ├── pom.xml                   # Maven dependencies
│   └── Dockerfile                # Docker image definition
│
├── fraudguard-ussd/              # USSD session handler
│   ├── src/main/java/com/fraudguard/ussd/
│   │   ├── UssdMenuRouter.java   # State machine logic
│   │   └── SmsNotifier.java      # SMS sending
│   ├── pom.xml
│   └── Dockerfile
│
├── fraudguard-mobile-app/        # Flutter cross-platform app
│   ├── lib/
│   │   ├── main.dart             # App entry point
│   │   ├── screens/              # UI screens
│   │   ├── widgets/              # Reusable UI components
│   │   ├── services/             # API client, local storage
│   │   ├── models/               # Data models
│   │   └── providers/            # State management (Riverpod)
│   ├── pubspec.yaml              # Flutter dependencies
│   ├── android/                  # Android native code
│   ├── ios/                      # iOS native code
│   └── test/                     # Flutter tests
│
├── docker-compose.yml            # Local development orchestration
├── .github/
│   └── workflows/                # CI/CD pipelines (GitHub Actions)
├── README.md                     # Project overview
└── CONTRIBUTING.md               # Contribution guidelines
```

### Step 3: Configure Java Development Environment

#### IntelliJ IDEA Setup

```
1. Open the fraudguard-workspace folder
2. File → Project Structure → Project
3. Set Project SDK to "Amazon Corretto 21"
4. Set Language level to "21 - Record patterns, pattern matching for switch"
5. Click Apply
6. Build → Rebuild Project (flushes old caches)
```

#### Eclipse Setup

```
1. Window → Preferences → Java → Compiler
2. Set Compiler Compliance Level to 21
3. Project → Clean All
```

#### VS Code Setup

```json
// .vscode/settings.json
{
  "java.configuration.runtimes": [
    {
      "name": "JavaSE-21",
      "path": "/path/to/amazon-corretto-21",
      "default": true
    }
  ],
  "java.compile.nullAnalysis.mode": "automatic"
}
```

### Step 4: Build All Modules

```bash
# Navigate to root directory
cd fraudguard-workspace

# Clean previous builds
mvn clean

# Build all modules (backend, ussd)
mvn install -DskipTests

# Expected output:
# [INFO] fraudguard-workspace .......................... SUCCESS
# [INFO] fraudguard-backend ............................ SUCCESS
# [INFO] fraudguard-ussd ............................... SUCCESS
```

### Step 5: Start Docker Environment

```bash
# Create .env file for Docker environment variables
cat > .env << EOF
# MySQL Configuration
MYSQL_ROOT_PASSWORD=rootpassword123
MYSQL_DATABASE=fraudguard_db
MYSQL_USER=fraudguard_app
MYSQL_PASSWORD=apppassword123

# Spring Boot Configuration
SPRING_PROFILE=dev
JAVA_TOOL_OPTIONS=-Dfile.encoding=UTF-8
EOF

# Start Docker Compose services
docker-compose up -d

# Wait for services to be healthy
echo "Waiting for MySQL to be ready..."
sleep 10

# Check logs
docker-compose logs -f backend
```

### Step 6: Initialize Database

The database schema will be loaded automatically via Flyway migrations.

To verify the schema was created:

```bash
# Access MySQL container
docker exec -it fraudguard-mysql mysql -u root -prootpassword123

# Inside MySQL prompt
use fraudguard_db;
show tables;
# You should see 15+ tables

# Exit MySQL
exit
```

### Step 7: Test the Backend API

#### Option A: Using Curl

```bash
# Health check
curl http://localhost:8080/api/v1/health

# Register a new user
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@fraudguard.com",
    "username": "testuser",
    "password": "SecurePass123!",
    "firstName": "Test",
    "lastName": "User",
    "region": "Western Cape",
    "phoneNumber": "+27821234567"
  }'

# Login
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@fraudguard.com",
    "password": "SecurePass123!"
  }'

# Save the accessToken from the response, then get user profile
curl http://localhost:8080/api/v1/user/profile \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN_HERE"
```

#### Option B: Using Postman

1. Download [Postman](https://www.postman.com/downloads/)
2. Import the API collection:
   ```bash
   # Get the Postman collection (will be in repo)
   curl https://raw.githubusercontent.com/fraudguard/fraudguard-workspace/main/postman-collection.json \
     -o FraudGuard.postman_collection.json
   ```
3. Open Postman → Import → Select the JSON file
4. Set environment variable `base_url` to `http://localhost:8080/api/v1`
5. Run requests from the collection

#### Option C: Using Swagger UI

Visit: http://localhost:8080/swagger-ui.html

Try it out:
1. Click "POST /auth/register"
2. Click "Try it out"
3. Enter test user details
4. Click "Execute"
5. Copy the `accessToken` from response
6. Click "Authorize" (top right)
7. Paste token: `Bearer YOUR_TOKEN`

### Step 8: Setup Flutter Mobile App

```bash
# Navigate to mobile app directory
cd fraudguard-mobile-app

# Get Flutter dependencies
flutter pub get

# Run the app on emulator
flutter run

# Or build for specific platform
flutter run -d chrome     # Web
flutter run -d emulator   # Android emulator
flutter run -d simulator  # iOS simulator
```

### Step 9: Run Tests

```bash
# Backend unit tests
cd fraudguard-backend
mvn test

# Backend integration tests
mvn verify

# Flutter widget tests
cd ../fraudguard-mobile-app
flutter test

# Generate coverage report
flutter test --coverage
```

---

## 🔧 Development Workflow

### Daily Development Loop

```bash
# 1. Start fresh (pull latest changes)
git pull upstream main

# 2. Create feature branch
git checkout -b feature/your-feature-name

# 3. Make changes to backend
cd fraudguard-backend
# Edit src/main/java/...

# 4. Test locally
mvn clean test
mvn spring-boot:run

# 5. Commit and push
git add .
git commit -m "feat: add new API endpoint"
git push origin feature/your-feature-name

# 6. Open Pull Request on GitHub
```

### Common Development Tasks

#### Add a New API Endpoint

```java
// fraudguard-backend/src/main/java/com/fraudguard/controller/MyController.java

package com.fraudguard.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/v1/my-endpoint")
public class MyController {
    
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getItem(@PathVariable String id) {
        // Implementation
        return ResponseEntity.ok("Data");
    }
    
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> createItem(@RequestBody MyRequest request) {
        // Implementation
        return ResponseEntity.status(201).body(response);
    }
}
```

Then test:
```bash
# Rebuild
mvn clean install -DskipTests

# Restart Docker
docker-compose restart backend

# Test endpoint
curl http://localhost:8080/api/v1/my-endpoint/123 \
  -H "Authorization: Bearer YOUR_TOKEN"
```

#### Add a Database Table

```sql
-- fraudguard-backend/src/main/resources/db/migration/V2__add_my_table.sql

CREATE TABLE my_new_table (
    id CHAR(36) PRIMARY KEY,
    user_id CHAR(36) NOT NULL,
    data VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    KEY idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

Then:
```bash
# Flyway will automatically detect and run migrations on app restart
docker-compose restart backend
docker-compose logs -f backend | grep -i "migrat"
```

#### Add a New Service

```java
// fraudguard-backend/src/main/java/com/fraudguard/service/MyService.java

package com.fraudguard.service;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MyService {
    
    private final MyRepository repository;
    
    public void doSomething() {
        // Business logic
    }
}
```

### Debugging

#### Enable Debug Mode

```bash
# Set environment variable
export JAVA_OPTS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005"

# Restart backend with debug flag
docker-compose restart backend

# In IntelliJ: Run → Edit Configurations → Add Remote JVM Debug
# Set Host: localhost, Port: 5005
# Click Debug
```

#### View Application Logs

```bash
# Real-time logs from backend
docker-compose logs -f backend

# Logs from MySQL
docker-compose logs -f mysql

# Logs from specific service
docker logs fraudguard-backend -f

# Save logs to file
docker-compose logs > logs.txt
```

#### Common Error Solutions

| Error | Solution |
|-------|----------|
| `Port 8080 already in use` | `lsof -i :8080` → `kill -9 PID` |
| `MySQL connection refused` | `docker-compose ps` → check MySQL status → `docker-compose logs mysql` |
| `Maven dependency issues` | `mvn clean install -U` (forces download of latest) |
| `Java version mismatch` | `java -version` → verify Java 21 is installed |
| `Docker daemon not running` | Start Docker Desktop or run `dockerd` |

---

## 🚀 Deploying to Staging

### Build Docker Images

```bash
# Build backend image
cd fraudguard-backend
docker build -t fraudguard-backend:v0.1 .

# Build USSD image
cd ../fraudguard-ussd
docker build -t fraudguard-ussd:v0.1 .

# Verify images
docker images | grep fraudguard
```

### Push to Docker Registry

```bash
# Login to Docker Hub (or AWS ECR)
docker login

# Tag images for registry
docker tag fraudguard-backend:v0.1 your-registry/fraudguard-backend:v0.1
docker tag fraudguard-ussd:v0.1 your-registry/fraudguard-ussd:v0.1

# Push to registry
docker push your-registry/fraudguard-backend:v0.1
docker push your-registry/fraudguard-ussd:v0.1
```

### Deploy to AWS ECS (Example)

```bash
# Create ECS task definition
aws ecs register-task-definition \
  --family fraudguard-backend \
  --container-definitions file://task-definition.json

# Update ECS service
aws ecs update-service \
  --cluster fraudguard \
  --service fraudguard-backend \
  --force-new-deployment
```

---

## 📚 Additional Resources

- **Spring Boot Documentation:** https://spring.io/projects/spring-boot
- **Flutter Documentation:** https://flutter.dev/docs
- **Africa's Talking API:** https://africastalking.com/
- **Docker Documentation:** https://docs.docker.com/
- **GitHub Repository:** https://github.com/fraudguard/fraudguard-workspace

---

## ✅ Verification Checklist

Before considering your setup complete:

- [ ] Java 21 installed and verified
- [ ] Maven 3.9+ installed
- [ ] Docker & Docker Compose running
- [ ] Backend builds with `mvn clean install`
- [ ] Backend starts with `docker-compose up`
- [ ] Health check endpoint returns 200 OK
- [ ] Can register and login via API
- [ ] Can access Swagger UI
- [ ] Flutter dependencies installed
- [ ] Mobile app runs on emulator
- [ ] Database schema created with 15+ tables

---

## 🆘 Getting Help

- **Stuck?** Check the [Troubleshooting Guide](#troubleshooting)
- **Have a question?** Open an issue on GitHub
- **Found a bug?** Create a GitHub issue with reproduction steps
- **Want to contribute?** Read [CONTRIBUTING.md](CONTRIBUTING.md)

---

## 🎉 What's Next?

After your setup is complete:

1. **Read the [API Documentation](FRAUDGUARD_API_OPENAPI.yaml)** to understand available endpoints
2. **Review the [Database Schema](FRAUDGUARD_DATABASE_SCHEMA.sql)** to understand data model
3. **Check the [Project Board](FRAUDGUARD_PROJECT_BOARD.md)** for current tasks
4. **Pick an issue from GitHub** and start contributing!

---

**Happy coding! 🚀**
