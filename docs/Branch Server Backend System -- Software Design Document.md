# Branch Server Backend System – Software Design Document

**Student ID:** B232270003  
**Project:** POS Branch Server (CSA303)  
**Date:** April 14, 2026

---

## 1. System Overview
The Branch Server acts as an intermediary layer between POS terminals and the Central Server. It handles sales ingestion, product lookups, and data synchronization.

### Key Requirements Addressed:
- **Offline-First**: POS terminals operate independently with SQLite; Branch Server provides REST API for consistent data updates.
- **Idempotency**: UUID-based primary keys prevent duplicate sales entries.
- **Concurrency**: Handled via Spring Task Execution and Transaction management.
- **Scalability**: PostgreSQL partitioning implementation for growing sales data.

## 2. Architecture Design
The system follows a layered architecture:
- **Controller Layer**: REST Endpoints (OpenAPI documented).
- **Service Layer**: Business logic (Sale processing, Price tracking).
- **Repository Layer**: Data access using Spring Data JPA.

### Design Patterns:
1. **Repository Pattern**: Abstraction of PostgreSQL data access.
2. **Strategy Pattern**: `PaymentStrategyFactory` allows adding payment methods (CASH, CARD, QR) without modifying existing service code (OCP).
3. **Observer Pattern**: Utilizes `ApplicationEventPublisher` to notify components about price changes and sale completions.

## 3. Database Design
Strict compliance with the 10-table schema:
1. `category`
2. `product`
3. `barcode`
4. `product_price` (Historical prices)
5. `branch`
6. `pos_terminal`
7. `payment_type`
8. `discount_rule`
9. `sales` (Partitioned by `created_at`)
10. `sale_item`

### Partitioning Strategy:
The `sales` table is partitioned by range based on the `created_at` timestamp to optimize query performance for large datasets.

## 4. Implementation Details
- **Sync Logic**: Incremental updates using `last_updated` timestamps.
- **Cleanup Task**: Automated annual cleanup of data older than 12 months.
- **Retry Mechanism**: POS logic (simulated) retry on failure.

## 5. Performance & Load Testing
A load test was conducted simulating 125 POS terminals (5 per 25 branches).
- **Target**: 125 req/sec
- **Tool**: k6
- **Result**: System stabilized at 125 RPS with < 200ms average response time.

### Environment Info:
- **CPU**: Apple M1
- **RAM**: 8GB
- **OS**: macOS
- **Stack**: Java 23, Spring Boot 3, PostgreSQL 16 (Docker)
