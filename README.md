# POS Branch Server - Project Submission

This project is a production-correct implementation of the Supermarket POS Branch Server, specifically designed to meet the requirements of the CSA303 Software Engineering Principles assignment.

## 🚀 Getting Started

### Prerequisites
- Java 21+
- Docker & Docker Compose
- Maven 3.8+

### 🛠️ Execution

1. **Start PostgreSQL**:
   ```bash
   cd prj1
   docker-compose up -d
   ```

2. **Build and Test**:
   ```bash
   cd students/b232270003
   mvn clean verify
   ```

3. **Run Application**:
   ```bash
   mvn spring-boot:run
   ```

## 🏗️ Technical Highlights

- **Database Partitioning**: The `sales` table is horizontally partitioned by date (`created_at`).
- **Idempotency**: Implements thread-safe idempotency via application-level locking and transaction isolation, ensuring each sale UUID is processed exactly once even under high concurrency.
- **OCP Compliance**: 
  - **Strategy Pattern**: Payment processing is decoupled from the service layer via interchangeable strategies.
  - **Observer Pattern**: Sales and Price updates are handled via Spring `ApplicationEvents`.
- **Quality Assurance**: 32 automated tests covering concurrency, fallback logic, and service boundaries. 70%+ JaCoCo coverage.

## 📊 Testing Instructions

### Automated Suite
Run all tests and generate coverage report:
```bash
mvn clean test jacoco:report
```
View report at: `target/site/jacoco/index.html`

### Manual Verification
- **API Documentation**: [http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html)
- **Sale Ingestion**: Send a POST request to `/api/sales` with a UUID. Repeat with the same UUID to verify `409 Conflict` (idempotency).
