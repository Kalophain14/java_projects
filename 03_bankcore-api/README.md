# BankCore API - Learning Project

A banking backend simulation built using Java Spring Boot. This is a **learning project** — every file you write teaches you something new about Java, Spring Boot, and backend development.

## How This Project Works



---

## 📁 Project Structure

```
bankcore-api/
├── ✅ pom.xml                          # Maven build config (provided)
├── ✅ Dockerfile                       # Docker build (provided)
├── ✅ docker-compose.yml              # Docker orchestration (provided)
├── ✅ .gitignore                      # Git ignore rules (provided)
│
├── ✅ src/main/resources/
│   ├── ✅ application.properties      # App config (provided)
│   └── ✅ db/migration/
│       ├── ✅ V1__init_schema.sql     # Database schema (provided)
│       └── ✅ V2__seed_data.sql       # Seed data (provided)
│
├── ✅ src/test/resources/
│   └── ✅ application-test.properties # Test config (provided)
│
└── ❌ src/main/java/com/bankcore/api/
    ├── ❌ BankCoreApiApplication.java          # Main entry point
    │
    ├── ❌ model/
    │   ├── ❌ enums/
    │   │   ├── ❌ AccountType.java
    │   │   ├── ❌ AccountStatus.java
    │   │   ├── ❌ TransactionType.java
    │   │   ├── ❌ TransactionStatus.java
    │   │   ├── ❌ CustomerStatus.java
    │   │   └── ❌ Role.java
    │   ├── ❌ Customer.java
    │   ├── ❌ Account.java
    │   ├── ❌ Transaction.java
    │   ├── ❌ User.java
    │   └── ❌ AuditLog.java
    │
    ├── ❌ dto/
    │   ├── ❌ CustomerDTO.java
    │   ├── ❌ CreateCustomerRequest.java
    │   ├── ❌ UpdateCustomerRequest.java
    │   ├── ❌ AccountDTO.java
    │   ├── ❌ CreateAccountRequest.java
    │   ├── ❌ TransactionDTO.java
    │   ├── ❌ DepositRequest.java
    │   ├── ❌ WithdrawalRequest.java
    │   ├── ❌ TransferRequest.java
    │   ├── ❌ LoginRequest.java
    │   ├── ❌ AuthResponse.java
    │   ├── ❌ RegisterRequest.java
    │   ├── ❌ ApiResponse.java
    │   ├── ❌ PageResponse.java
    │   ├── ❌ AccountBalanceDTO.java
    │   ├── ❌ TransactionHistoryRequest.java
    │   └── ❌ AuditLogDTO.java
    │
    ├── ❌ exception/
    │   ├── ❌ ResourceNotFoundException.java
    │   ├── ❌ DuplicateResourceException.java
    │   ├── ❌ InsufficientFundsException.java
    │   ├── ❌ AccountClosedException.java
    │   ├── ❌ InvalidTransactionException.java
    │   ├── ❌ UnauthorizedAccessException.java
    │   └── ❌ GlobalExceptionHandler.java
    │
    ├── ❌ repository/
    │   ├── ❌ CustomerRepository.java
    │   ├── ❌ AccountRepository.java
    │   ├── ❌ TransactionRepository.java
    │   ├── ❌ UserRepository.java
    │   └── ❌ AuditLogRepository.java
    │
    ├── ❌ mapper/
    │   ├── ❌ CustomerMapper.java
    │   ├── ❌ AccountMapper.java
    │   ├── ❌ TransactionMapper.java
    │   └── ❌ AuditLogMapper.java
    │
    ├── ❌ service/
    │   ├── ❌ CustomerService.java
    │   ├── ❌ AccountService.java
    │   ├── ❌ TransactionService.java
    │   ├── ❌ AuthService.java
    │   └── ❌ AuditLogService.java
    │
    ├── ❌ controller/
    │   ├── ❌ AuthController.java
    │   ├── ❌ CustomerController.java
    │   ├── ❌ AccountController.java
    │   ├── ❌ TransactionController.java
    │   ├── ❌ AuditLogController.java
    │   └── ❌ AdminController.java
    │
    ├── ❌ security/
    │   ├── ❌ JwtTokenProvider.java
    │   ├── ❌ JwtAuthenticationFilter.java
    │   ├── ❌ SecurityConfig.java
    │   └── ❌ CustomUserDetailsService.java
    │
    ├── ❌ config/
    │   ├── ❌ OpenApiConfig.java
    │   ├── ❌ JacksonConfig.java
    │   └── ❌ WebConfig.java
    │
    └── ❌ audit/
        └── ❌ AuditService.java

└── ❌ src/test/java/com/bankcore/api/
    ├── ❌ BankCoreApiApplicationTests.java
    ├── ❌ CustomerServiceTest.java
    ├── ❌ AccountServiceTest.java
    └── ❌ TransactionServiceTest.java
```

---

## 🎯 Learning Roadmap

### Phase 1: Foundation (Enums & Main Class)
1. `BankCoreApiApplication.java` — The starting point
2. All 6 enums — Define the constants our app uses

### Phase 2: Data Layer (Entities & DTOs)
3. All 5 entities — Java objects that map to database tables
4. All 17 DTOs — Objects for API requests/responses

### Phase 3: Data Access (Repositories)
5. All 5 repositories — Interfaces for database queries

### Phase 4: Mapping (Mappers)
6. All 4 MapStruct mappers — Convert entities to DTOs

### Phase 5: Business Logic (Services)
7. All 5 services — The "brain" of each feature

### Phase 6: API Layer (Controllers)
8. All 6 controllers — HTTP endpoints

### Phase 7: Security
9. All 4 security classes — JWT & Spring Security

### Phase 8: Configuration & Audit
10. All 3 config classes + AuditService

### Phase 9: Exception Handling
11. All 7 exception classes

### Phase 10: Testing
12. All 4 test classes

---

## 🚀 Quick Start

```bash
# 1. Build the project
mvn clean compile

# 2. Run tests
mvn test

# 3. Run the application
mvn spring-boot:run

# 4. Or use Docker
docker-compose up -d
```

---

## 📚 Default Users (already in database)

| Username | Password | Role |
|----------|----------|------|
| admin | admin123 | ADMIN |
| manager | manager123 | MANAGER |
| teller | teller123 | TELLER |

---

## 📝 Notes

- Each empty file has a `// TODO: Implement this class` comment
- Start from the top of the roadmap and work down
- Don't skip phases — each layer depends on the one below it
- Ask questions! Every line of code will be explained.
