# POS Branch Server

This is a Spring Boot application acting as a Branch Server in a supermarket POS network. It manages products, sales, and synchronization with a central system.

## Prerequisites

- **Java 21**
- **Maven 3.x**
- **Docker & Docker Compose**

## Quick Start (Docker Compose)

The easiest way to run the entire stack (Application + Database) is using Docker Compose:

```bash
docker compose up --build
```

The application will be available at `http://localhost:8080`.
Swagger UI: `http://localhost:8080/swagger-ui.html`

## Lightweight Start (No Docker Required)

If you don't have Docker or want to run the application with a local database:

### Option A: H2 (Recommended for zero-config)
Runs entirely in memory and initializes from `schema.sql`.
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=h2
```

### Option B: SQLite (File-based)
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=sqlite
```
*Note: SQLite profile may require specific Hibernate metadata settings in some environments.*

## Local Development (PostgreSQL)

If you want to run the application on your host machine while keeping the database in Docker:

1. **Start the Database**:
   ```bash
   docker compose up -d postgres
   ```

2. **Run the Application**:
   ```bash
   mvn spring-boot:run
   ```

## Testing & Verification

### Automated Test Suite
To run all unit and integration tests (including concurrency tests):
```bash
mvn clean test
```

#### Specific Concurrency Test
The project includes a dedicated `ConcurrencyIntegrationTest` to verify the **Pessimistic Locking** implementation in `InventoryRepository`. To run only this test:
```bash
mvn test -Dtest=ConcurrencyIntegrationTest
```
This test simulates 20 concurrent threads performing 100 total sales to ensure inventory levels are deducted correctly without race conditions.

### Code Coverage
Generate the coverage report:
```bash
mvn jacoco:report
```
View the results at: `target/site/jacoco/index.html`.
*Note: A minimum of 70% coverage is required for the build to pass.*

### Manual Verification (CURL)
If the application is running (e.g., via H2 profile), you can test key endpoints manually:

#### 1. Check Initial Inventory
```bash
curl -X GET "http://localhost:8080/api/inventory/branch/1"
```

#### 2. Create a Sale (Triggers Stock Deduction)
```bash
curl -X POST "http://localhost:8080/api/sales" \
     -H "Content-Type: application/json" \
     -d '{
       "id": "'$(uuidgen)'",
       "branch_id": 1,
       "pos_id": 1,
       "payment_type_id": 1,
       "total_amount": 3000.0,
       "items": [
         {
           "product_id": 1,
           "quantity": 2,
           "price": 1500.0,
           "discount_amount": 0.0
         }
       ]
     }'
```

#### 3. Verify Stock Deduction
Re-run the GET request from Step 1 to see the updated quantity.

### Swagger UI
For interactive API testing, visit:
`http://localhost:8080/swagger-ui/index.html`

## Project Structure

- `src/main/java`: Core logic (Models, Services, Patterns).
- `src/main/resources`: Configuration (`application.properties`).
- `docker-compose.yml`: Multi-container setup.
- `schema.sql`: Database schema initialization.
- `Dockerfile`: Multi-stage build for the application.
