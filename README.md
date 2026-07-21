# JavaProjects

![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-Backend-green)
![Status](https://img.shields.io/badge/Status-In%20Progress-blue)

A collection of Java projects built to learn the tech stack listed below — starting simple and building toward production-style backend systems. Each project introduces new pieces of the stack, building on what the last one covered.

## Projects

| Project | Description | Status |
|---------|--------------|--------|
| [Calculator Preview](./01_calculator/assets/%20calculator-preview.png) | Apple iOS styled calculator built with Java Swing. | ✅ Complete |
| [BankingApp (CLI)](./02_bankingapp) | Command-line banking application simulating core operations like accounts, deposits, and transfers. | ✅ Complete |
| [BankCore API](./03_bankcore-api) | Spring Boot REST API for core banking operations, backed by PostgreSQL. | 🔜 Coming Soon |
| [Fraud Behavior Engine](./04_fraud-behavior-engine) | Event-driven service for detecting fraudulent transaction patterns using Kafka. | 🔜 Coming Soon |

---

## Tech Stack

### Core
- Java 21
- Spring Boot (JPA, Security)
- PostgreSQL
- Apache Kafka
- Redis
- Hibernate

### Testing
- JUnit 5
- Mockito

### Documentation
- Swagger / OpenAPI

### DevOps
- Docker
- AWS

---

## What I'm Learning

- Building RESTful APIs with Spring Boot
- Data persistence and ORM with JPA/Hibernate
- Securing APIs with Spring Security
- Event-driven architecture with Kafka
- Caching strategies with Redis
- Writing unit and integration tests with JUnit 5 and Mockito
- API documentation with Swagger/OpenAPI
- Containerization with Docker and deployment on AWS

---

## Repo Structure

```
JavaProjects/
├── 01_calculator/
├── 02_bankingapp/
├── 03_bankcore-api/
├── 04_fraud-behavior-engine/
└── README.md
```

---

## How to Run

1. Clone the repository
   ```
   git clone https://github.com/<your-username>/JavaProjects.git
   ```
2. Open the desired project folder in IntelliJ IDEA (or your preferred IDE)
3. Ensure Java 21 is installed and set as the project SDK
4. Run the project's `Main.java` (or, for Spring Boot projects, the `*Application.java` entry point)

---

## License

This repository is for personal learning purposes. Feel free to explore or reference the code.
