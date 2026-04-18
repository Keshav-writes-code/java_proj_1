# Enterprise Financial Transaction Engine

This is the Final Year Capstone Project for a Bachelors in Technology (Computer Science) program. The codebase implements a fault-tolerant, high-concurrency backend system mimicking a real-world financial ledger.

## Architecture Overview

The system acts as the core transaction processor, handling REST API requests to execute peer-to-peer transfers, deposit logging, and withdrawal processing.

### Key Features
- **ACID-Compliant Transaction Management:** Implements strict data isolation (REPEATABLE_READ) and atomic fund transfers. If any step fails (e.g., insufficient funds), the entire transaction rolls back cleanly.
- **Optimistic Locking:** The `Account` entity uses `@Version` to prevent concurrent modification anomalies without the severe performance penalty of pessimistic database locks.
- **Aspect-Oriented Programming (AOP):** Implements an `@AuditLog` annotation. Any service method annotated with this will have its execution invisibly intercepted by the `AuditAspect`, securely logging the execution time, method name, and sanitized parameters to an audit database table without exposing PII.
- **Robust API Validation:** Rigorous input sanitization using Jakarta Bean Validation (`@Valid`) and global exception handling (`@ControllerAdvice`) mapping to clean, consistent JSON error responses.

## Tech Stack
- Java 17+
- Spring Boot 3.2.x
- Spring Data JPA (Hibernate)
- MySQL 8
- JUnit 5 & Mockito
- Docker

---

## Running Locally on antiX Linux

Because antiX is a systemd-free distribution, running Docker requires specific sysvinit or runit commands to initialize the daemon. Follow these exact steps:

### 1. Install Tooling (Debian-based)
```bash
sudo apt-get update
sudo apt-get install -y openjdk-17-jdk maven docker.io docker-compose
```

### 2. Start the Docker Daemon (SysVinit/runit)
Since antiX does not use `systemctl`, start Docker using the `service` command:
```bash
sudo service docker start
```
*Verify it is running with: `sudo service docker status`*

### 3. Provision the Database
Spin up the MySQL container in the background:
```bash
docker-compose up -d
```

### 4. Run the Application
Start the Spring Boot backend:
```bash
mvn spring-boot:run
```

---

## Testing

The application implements rigorous Test-Driven Development (TDD) focusing on both happy paths and strict edge case handling (e.g., negative amounts, insufficient funds, invalid payloads).

To run the complete unit and integration test suite:
```bash
mvn clean test
```

## Dockerization

The application contains a multi-stage Dockerfile optimized for Docker layer caching.
To build the image manually:
```bash
docker build -t financial-engine .
```
