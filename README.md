# Shipment Tracking System

Backend REST API for managing customers, shipments, shipment status lifecycle, shipment status history, filtering and bulk import from CSV/Excel files.

The application was implemented as a Spring Boot backend system for a logistics/shipping use case. It allows users/customers to have multiple shipments, each shipment has a unique tracking number, and every status change is recorded in an append-only history table.

---

## Features

* Customer management
* Shipment creation with automatically generated tracking number
* Shipment lookup by ID
* Shipment lookup by tracking number
* Shipment status lifecycle validation
* Shipment status history
* Shipment filtering by:

    * customer
    * status
    * creation date range
* Pagination and sorting for shipment search
* Bulk shipment import from CSV files
* Bulk shipment import from Excel `.xlsx` files
* Row-level import validation with detailed error response
* PostgreSQL database
* Flyway database migrations
* Swagger/OpenAPI documentation
* Docker Compose support for running the full system

---

## Tech Stack

* Java 17
* Spring Boot 3.5
* Spring Web
* Spring Data JPA
* PostgreSQL
* Flyway
* Maven
* Lombok
* Jakarta Validation
* Springdoc OpenAPI / Swagger UI
* Apache Commons CSV
* Apache POI
* Docker Compose

---

## Domain Overview

The system contains three main domain concepts:

### Customer

Represents a user/customer who can have one or more shipments.

Main fields:

* first name
* last name
* email
* phone

The customer email is unique.

### Shipment

Represents a package/shipment that belongs to one customer.

Main fields:

* tracking number
* content description
* current status
* customer
* creation date
* update date

The tracking number is generated automatically by the system.

Example tracking number:

```text
TRK-A1B2C3D4
```

### Shipment Status History

Represents a single status change event for a shipment.

Main fields:

* shipment
* status
* changed at
* note

The history is append-only. Each status change creates a new history record.

---

## Shipment Status Lifecycle

Supported shipment statuses:

```text
CREATED
IN_TRANSIT
DELIVERED
CANCELLED
```

Allowed transitions:

```text
CREATED -> IN_TRANSIT
CREATED -> CANCELLED

IN_TRANSIT -> DELIVERED
IN_TRANSIT -> CANCELLED

DELIVERED -> final status
CANCELLED -> final status
```

Invalid transitions are rejected with `400 Bad Request`.

Examples of invalid transitions:

```text
CREATED -> DELIVERED
DELIVERED -> IN_TRANSIT
CANCELLED -> DELIVERED
IN_TRANSIT -> CREATED
```

---

## Environment Variables

The application uses environment variables for database configuration.

| Variable      | Description       | Default for local run |
| ------------- | ----------------- | --------------------- |
| `DB_HOST`     | Database host     | `localhost`           |
| `DB_PORT`     | Database port     | `5433`                |
| `DB_NAME`     | Database name     | `shipment_tracking`   |
| `DB_USERNAME` | Database username | `shipment_user`       |
| `DB_PASSWORD` | Database password | `shipment_password`   |

Example `.env.example`:

```env
DB_HOST=localhost
DB_PORT=5433
DB_NAME=shipment_tracking
DB_USERNAME=shipment_user
DB_PASSWORD=shipment_password
```

When the application is started through Docker Compose, the backend container uses:

```text
DB_HOST=postgres
DB_PORT=5432
```

This is because the backend container connects to the PostgreSQL container through the Docker network.

---

## Running with Docker Compose

The easiest way to run the full system is through Docker Compose.

From the project root directory, run:

```bash
docker compose up --build
```

This starts:

* PostgreSQL database
* Spring Boot backend application

The application will be available at:

```text
http://localhost:8080
```

Swagger UI will be available at:

```text
http://localhost:8080/swagger-ui/index.html
```

To stop the containers:

```bash
docker compose down
```

To stop the containers and remove the database volume:

```bash
docker compose down -v
```

---

## Running Locally Without Dockerizing the App

You can also run PostgreSQL through Docker and start the Spring Boot application from the IDE.

Start PostgreSQL:

```bash
docker compose up -d postgres
```

Run the application:

```bash
./mvnw spring-boot:run
```

On Windows:

```bash
mvnw.cmd spring-boot:run
```

Local Swagger UI:

```text
http://localhost:8080/swagger-ui/index.html
```

---

## Database Migrations

The project uses Flyway for database migrations.

Migration files are located in:

```text
src/main/resources/db/migration
```

Current migrations:

```text
V1__create_customers_table.sql
V2__create_shipments_table.sql
V3__create_shipment_status_history_table.sql
```

Hibernate is configured with:

```yaml
ddl-auto: validate
```

This means Hibernate does not create or update tables automatically. Database structure is managed through Flyway migrations.

---

## API Documentation

Swagger UI is available after starting the application:

```text
http://localhost:8080/swagger-ui/index.html
```

---

## API Endpoints

### Customers

| Method | Endpoint              | Description        |
| ------ | --------------------- | ------------------ |
| `POST` | `/api/customers`      | Create customer    |
| `GET`  | `/api/customers`      | Get all customers  |
| `GET`  | `/api/customers/{id}` | Get customer by ID |
| `PUT`  | `/api/customers/{id}` | Update customer    |

Example create customer request:

```json
{
  "firstName": "Nemanja",
  "lastName": "Ristic",
  "email": "nemanja@example.com",
  "phone": "+381601234567"
}
```

---

### Shipments

| Method  | Endpoint                                   | Description                     |
| ------- | ------------------------------------------ | ------------------------------- |
| `POST`  | `/api/shipments`                           | Create shipment                 |
| `GET`   | `/api/shipments`                           | Search/filter shipments         |
| `GET`   | `/api/shipments/{id}`                      | Get shipment by ID              |
| `GET`   | `/api/shipments/tracking/{trackingNumber}` | Get shipment by tracking number |
| `PATCH` | `/api/shipments/{id}/status`               | Update shipment status          |
| `GET`   | `/api/shipments/{id}/history`              | Get shipment status history     |

Example create shipment request:

```json
{
  "customerId": 1,
  "contentDescription": "Laptop accessories and documents"
}
```

Example update status request:

```json
{
  "status": "IN_TRANSIT",
  "note": "Shipment picked up by courier."
}
```

---

## Shipment Filtering

The shipment search endpoint supports filtering, pagination and sorting.

Endpoint:

```http
GET /api/shipments
```

Supported query parameters:

| Parameter     | Description                        | Example          |
| ------------- | ---------------------------------- | ---------------- |
| `customerId`  | Filter by customer ID              | `1`              |
| `status`      | Filter by shipment status          | `IN_TRANSIT`     |
| `createdFrom` | Filter shipments created from date | `2026-06-01`     |
| `createdTo`   | Filter shipments created to date   | `2026-06-30`     |
| `page`        | Page number                        | `0`              |
| `size`        | Page size                          | `10`             |
| `sort`        | Sort field and direction           | `createdAt,desc` |

Example:

```http
GET /api/shipments?customerId=1&status=IN_TRANSIT&createdFrom=2026-06-01&createdTo=2026-06-30&page=0&size=10&sort=createdAt,desc
```

Example paginated response:

```json
{
  "content": [
    {
      "id": 1,
      "trackingNumber": "TRK-A1B2C3D4",
      "contentDescription": "Laptop accessories and documents",
      "status": "IN_TRANSIT",
      "customer": {
        "id": 1,
        "firstName": "Nemanja",
        "lastName": "Ristic",
        "email": "nemanja@example.com"
      },
      "createdAt": "2026-06-24T12:00:00",
      "updatedAt": "2026-06-24T12:10:00"
    }
  ],
  "page": 0,
  "size": 10,
  "totalElements": 1,
  "totalPages": 1,
  "last": true
}
```

---

## CSV/Excel Import

The system supports bulk shipment import from:

```text
.csv
.xlsx
```

Endpoint:

```http
POST /api/import/shipments
```

Request type:

```text
multipart/form-data
```

Request part:

```text
file
```

### CSV Format

The CSV file must contain the following header:

```csv
customerFirstName,customerLastName,customerEmail,customerPhone,contentDescription,note
```

Example:

```csv
customerFirstName,customerLastName,customerEmail,customerPhone,contentDescription,note
Marko,Markovic,marko@example.com,+38160111222,Mobile phone and charger,Imported from CSV
Jovana,Jovanovic,jovana@example.com,+38160111333,Books and documents,Imported from CSV
Invalid,User,,+38160111555,Package without email,This row should fail
```

### Excel Format

The Excel `.xlsx` file should use the first sheet.

The first row should contain headers:

```text
customerFirstName | customerLastName | customerEmail | customerPhone | contentDescription | note
```

Expected column order:

| Column | Field                |
| ------ | -------------------- |
| A      | `customerFirstName`  |
| B      | `customerLastName`   |
| C      | `customerEmail`      |
| D      | `customerPhone`      |
| E      | `contentDescription` |
| F      | `note`               |

### Import Behavior

For each imported row:

1. The system validates required fields.
2. The system checks whether a customer already exists by email.
3. If the customer exists, the existing customer is used.
4. If the customer does not exist, a new customer is created.
5. A new shipment is created with status `CREATED`.
6. A new shipment status history record is created.

Rows with validation errors are skipped and returned in the response.

Example import response:

```json
{
  "totalRows": 3,
  "importedRows": 2,
  "failedRows": 1,
  "errors": [
    {
      "rowNumber": 4,
      "message": "Customer email is required"
    }
  ]
}
```

Sample import files are located in:

```text
samples/
```

---

## Error Handling

The API returns structured error responses.

Example validation error:

```json
{
  "timestamp": "2026-06-24T12:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/customers",
  "validationErrors": [
    "email: Email must be valid",
    "firstName: First name is required"
  ]
}
```

Example invalid status transition:

```json
{
  "timestamp": "2026-06-24T12:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Invalid status transition from DELIVERED to IN_TRANSIT",
  "path": "/api/shipments/1/status",
  "validationErrors": null
}
```

---

## Project Structure

```text
src/main/java/com/nemanja/shipmenttrackingsystem
├── common
│   ├── exception
│   └── response
├── customer
│   └── dto
├── shipment
│   └── dto
├── tracking
│   └── dto
└── importfile
    └── dto
```

### Package Responsibilities

| Package      | Responsibility                                                |
| ------------ | ------------------------------------------------------------- |
| `common`     | Shared exceptions and response classes                        |
| `customer`   | Customer entity, repository, service, controller and DTOs     |
| `shipment`   | Shipment entity, status lifecycle, filtering and shipment API |
| `tracking`   | Shipment status history entity, repository and response DTO   |
| `importfile` | CSV/Excel parsing and bulk import API                         |

---

## Design Decisions and Assumptions

* Authentication and authorization are not implemented because they were not part of the assignment requirements.
* Tracking numbers are generated automatically by the system.
* Customer email is treated as unique.
* During import, an existing customer is reused if the same email already exists.
* Imported shipments are created with the initial status `CREATED`.
* Every shipment status change creates a new history record.
* `DELIVERED` and `CANCELLED` are final shipment statuses.
* Shipment history is append-only.
* Flyway is used for database schema management.
* Docker Compose is provided for easier local setup.

---

## Useful Commands

Build the project:

```bash
./mvnw clean package
```

Run the application locally:

```bash
./mvnw spring-boot:run
```

Run the full system with Docker Compose:

```bash
docker compose up --build
```

Stop Docker containers:

```bash
docker compose down
```

---

## Repository

The complete source code is available in this GitHub repository.
