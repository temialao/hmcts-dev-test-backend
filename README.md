# HMCTS Task Management API

A RESTful API for managing caseworker tasks, built with Spring Boot and PostgreSQL.

## Tech Stack

- Java 21
- Spring Boot 3.5
- Spring Data JPA
- PostgreSQL 16
- Gradle
- Docker Compose
- JUnit 5 & Mockito

## Prerequisites

- Java 21
- Docker & Docker Compose

## Getting Started

1. Clone the repository:
   ```bash
   git clone https://github.com/YOUR_USERNAME/hmcts-dev-test-backend.git
   cd hmcts-dev-test-backend
   ```

2. Start the database:
   ```bash
   docker compose up -d
   ```

3. Run the application:
   ```bash
   ./gradlew bootRun
   ```

The API will be available at `http://localhost:4000`.

## API Documentation

Swagger UI is available at `http://localhost:4000/swagger-ui.html` when the application is running.

### Endpoints

| Method | Endpoint             | Description                              |
|--------|----------------------|------------------------------------------|
| POST   | /tasks               | Create a new task                        |
| GET    | /tasks               | Get all tasks (paginated, filterable)    |
| GET    | /tasks/{id}          | Get a task by ID                         |
| PATCH  | /tasks/{id}/status   | Update the status of a task              |
| DELETE | /tasks/{id}          | Delete a task                            |

### Query Parameters for GET /tasks

| Parameter | Default     | Description                          |
|-----------|-------------|--------------------------------------|
| status    | -           | Filter by status (PENDING, IN_PROGRESS, COMPLETED) |
| page      | 0           | Page number                          |
| size      | 20          | Page size                            |
| sortBy    | dueDateTime | Sort field                           |
| direction | asc         | Sort direction (asc/desc)            |

### Task Schema

```json
{
  "title": "Review case files",
  "description": "Optional description",
  "status": "PENDING",
  "dueDateTime": "2026-04-01T10:00:00"
}
```

**Status values:** `PENDING`, `IN_PROGRESS`, `COMPLETED`

## Running Tests

```bash
./gradlew test
```

### Test Coverage Report

```bash
./gradlew jacocoTestReport
open build/reports/jacoco/test/html/index.html
```

Current coverage: **81% instructions, 83% branches**

## Design Decisions

- **PostgreSQL over H2**: Used PostgreSQL via Docker to match the HMCTS production stack, with Docker Compose for easy local setup.
- **Pagination**: GET /tasks returns paginated results by default to handle large datasets efficiently.
- **Status filtering**: Tasks can be filtered by status to support caseworker workflows.
- **Audit timestamps**: `createdAt` and `updatedAt` fields are automatically managed via JPA auditing.
- **Custom validation**: Due dates must be in the future, enforced by a custom validator.
- **Global exception handling**: Consistent error responses via `@RestControllerAdvice` with proper HTTP status codes.
- **DTO for status updates**: `StatusUpdateRequest` provides type safety and validation for PATCH requests.
