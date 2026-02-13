# 📦 Inventory Management System - Producer API

**Producer Application** for inventory management system using **Apache Kafka** as message broker. This application is responsible for managing product, category, supplier data, and sending sales transactions to Kafka topics to be processed by the Consumer.

---

## 🚀 Tech Stack

- **Java 21**
- **Spring Boot 4.0.2**
  - Spring Web MVC
  - Spring Data JPA
  - Spring Kafka
  - Spring AOP (Aspect-Oriented Programming)
  - Spring Validation
- **PostgreSQL** - Database
- **Apache Kafka** - Message Broker
- **Lombok** - Reduce boilerplate code
- **SpringDoc OpenAPI** - API Documentation (Swagger UI)
- **Logback** - Logging framework
- **Maven** - Build tool

---

## ✨ Features

### 📋 Core Features
- ✅ **Product Management** - CRUD operations for products
- ✅ **Category Management** - CRUD operations for product categories
- ✅ **Supplier Management** - CRUD operations for suppliers
- ✅ **Transaction Processing** - Validate and send transactions to Kafka
- ✅ **Stock Management** - Track stock changes with stock logs
- ✅ **Sales Report** - Sales reports by period
- ✅ **Stock Log Report** - Stock change history

### 🔧 Technical Features
- ✅ **Kafka Producer** - Send transaction data to Kafka topic
- ✅ **Advanced Logging** - Structured logging for monitoring & debugging
- ✅ **Exception Handling** - Global exception handler
- ✅ **Input Validation** - Jakarta Bean Validation
- ✅ **AOP Logging** - HTTP request/response logging
- ✅ **API Documentation** - Swagger UI & OpenAPI
- ✅ **Low Stock Alert** - Warning log when stock ≤ 10

---

## 🏗️ Architecture

```
┌─────────────┐         ┌──────────────┐         ┌─────────────┐
│   Client    │ ──────> │   Producer   │ ──────> │    Kafka    │
│   (API)     │ <────── │   (Spring)   │         │   Topic     │
└─────────────┘         └──────────────┘         └─────────────┘
                              │                          │
                              │                          ▼
                              ▼                   ┌─────────────┐
                        ┌──────────┐              │  Consumer   │
                        │ Database │              │  (Process)  │
                        │PostgreSQL│              └─────────────┘
                        └──────────┘
```

### Transaction Flow:
1. **Client** sends transaction request to Producer API
2. **Producer** validates data (product exists, stock availability)
3. **Producer** sends transaction data to **Kafka topic**
4. **Consumer** (separate application) consumes message and saves to database
5. **Producer** also provides endpoints to view data & reports

---

## 🗄️ Database Schema

### Tables

#### 1. **category**
```sql
- id (PK, SERIAL)
- category_name (VARCHAR 50)
- created_at (TIMESTAMP)
- updated_at (TIMESTAMP)
```

#### 2. **supplier**
```sql
- id (PK, SERIAL)
- supplier_name (VARCHAR 100)
- contact_info (VARCHAR 100)
- created_at (TIMESTAMP)
- updated_at (TIMESTAMP)
```

#### 3. **product**
```sql
- id (PK, SERIAL)
- sku (VARCHAR 50)
- product_name (VARCHAR 100)
- category_id (FK -> category.id)
- supplier_id (FK -> supplier.id)
- current_stock (INTEGER)
- price (DECIMAL 15,2)
- created_at (TIMESTAMP)
- updated_at (TIMESTAMP)
```

#### 4. **stock_log**
```sql
- id (PK, SERIAL)
- product_id (FK -> product.id)
- quantity_change (INTEGER)
- log_type (VARCHAR 20) -- PURCHASE, SALE, ADJUSTMENT
- created_at (TIMESTAMP)
```

#### 5. **transaction_history**
```sql
- id (PK, SERIAL)
- transaction_date (DATE)
- total_price (DECIMAL 15,2)
- created_at (TIMESTAMP)
```

#### 6. **transaction_detail**
```sql
- id (PK, SERIAL)
- transaction_history_id (FK -> transaction_history.id)
- product_id (FK -> product.id)
- qty (INTEGER)
- price (DECIMAL 15,2)
- created_at (TIMESTAMP)
```

---

## 📋 Prerequisites

- **Java JDK 21** or higher
- **Maven 3.8+**
- **PostgreSQL 12+** (Database must be running)
- **Apache Kafka 3.x** (Must be running)
- **IDE** (IntelliJ IDEA, Eclipse, or VS Code)

---

## ⚙️ Installation & Setup

### 1. Clone Repository
```bash
git clone <repository-url>
cd producer
```

### 2. Configure Database
Create database in PostgreSQL:
```sql
CREATE DATABASE your_database_name;
```

### 3. Configure Application Properties
Edit `src/main/resources/application.properties`:

```properties
# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/your_database_name
spring.datasource.username=your_username
spring.datasource.password=your_password

# Kafka Configuration
kafka.bootstrap-servers=localhost:9092
kafka.topic.sales-transaction=sales-transaction-yourname
```

### 4. Build Project
```bash
mvn clean install
```

### 5. Run Application
```bash
mvn spring-boot:run
```

Or run JAR file:
```bash
java -jar target/producer-0.0.1-SNAPSHOT.jar
```

Application will run at: `http://localhost:8088`

---

## 📚 API Documentation

After the application is running, access API documentation:

- **Swagger UI**: http://localhost:8088/swagger
- **OpenAPI JSON**: http://localhost:8088/api-docs-json

---

## 🔌 API Endpoints

Base URL: `http://localhost:8088/api/v1`

### 📦 Product Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/product` | Create new product |
| GET | `/products` | Get all products |
| GET | `/product/{id}` | Get product by ID |
| PUT | `/product/{id}` | Update product |
| DELETE | `/product/{id}` | Delete product |

**Example Request - Create Product:**
```json
POST /api/v1/product
{
  "sku": "SKU001",
  "product_name": "Laptop ASUS ROG",
  "category_id": 1,
  "supplier_id": 1,
  "current_stock": 50,
  "price": 15000000
}
```

### 📂 Category Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/category` | Create new category |
| GET | `/categories` | Get all categories |
| GET | `/category/{id}` | Get category by ID |
| PUT | `/category/{id}` | Update category |
| DELETE | `/category/{id}` | Delete category |

**Example Request - Create Category:**
```json
POST /api/v1/category
{
  "category_name": "Electronics"
}
```

### 🏭 Supplier Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/supplier` | Create new supplier |
| GET | `/suppliers` | Get all suppliers |
| GET | `/supplier/{id}` | Get supplier by ID |
| PUT | `/supplier/{id}` | Update supplier |
| DELETE | `/supplier/{id}` | Delete supplier |

**Example Request - Create Supplier:**
```json
POST /api/v1/supplier
{
  "supplier_name": "PT. Teknologi Maju",
  "contact_info": "081234567890"
}
```

### 💳 Transaction Endpoint

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/transaction` | Create transaction (send to Kafka) |

**Example Request - Create Transaction:**
```json
POST /api/v1/transaction
{
  "transaction_date": "2026-02-13",
  "items": [
    {
      "product_id": 1,
      "qty": 2
    },
    {
      "product_id": 3,
      "qty": 1
    }
  ]
}
```

**Flow:**
1. Validate product exists
2. Check stock availability
3. Calculate estimated total price
4. Send to Kafka topic: `sales-transaction-yourname`
5. Consumer will process and update database

### 📊 Report Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/report/sales?start_date=2026-01-01&end_date=2026-12-31` | Get sales report |
| GET | `/report/stockLog` | Get all stock logs |

---

## 📝 Logging System

### Log Files
Logs are saved in `logs/` folder:
- **application.log** - Business logic logs (INFO, WARN, ERROR)
- **endpoint-access.log** - HTTP request/response logs

### Log Levels & Format

#### ✅ INFO - Business Operations
```
INFO  - Received transaction: date=2026-02-13, items=2
INFO  - Processing item: product_id=1, qty=5, current_stock=100
INFO  - Transaction sent to Kafka successfully: date=2026-02-13, items=2, estimated_total_price=250000.0
INFO  - New product created: product_id=5, product_name=Mouse, initial_stock=100
INFO  - Stock adjusted: product_id=3, product_name=Keyboard, old_stock=50, new_stock=100, change=50
INFO  - Product deleted: product_id=10, product_name=Old Product
```

#### ⚠️ WARN - Alerts & Business Warnings
```
WARN  - Low stock alert: product_id=5, product_name=Laptop, stock_after_transaction=8, threshold=10
WARN  - Cannot delete product in use: product_id=2, product_name=Mouse, transaction_count=5
WARN  - [ERROR] 2026-02-13 10:00:00 | DELETE /api/v1/product/11 | Status: 409 CONFLICT | Message: Cannot delete product. It has 1 stock log record(s)
```

#### ❌ ERROR - Errors
```
ERROR - Product not found: product_id=999
ERROR - Insufficient stock: product_id=5, required=20, available=10
ERROR - Failed to send transaction to Kafka: date=2026-02-13, error=Connection refused
```

### Low Stock Threshold
Default: **10 units**
- When stock ≤ 10 after transaction, a **WARNING log** will appear
- Can be changed in `TransactionService.LOW_STOCK_THRESHOLD`

---

## 📁 Project Structure

```
producer/
├── src/
│   ├── main/
│   │   ├── java/javadev/project/producer/
│   │   │   ├── ProducerApplication.java          # Main application
│   │   │   ├── aspect/
│   │   │   │   └── LoggingAspect.java            # AOP for HTTP logging
│   │   │   ├── configuration/
│   │   │   │   ├── KafkaProducerConfig.java      # Kafka configuration
│   │   │   │   └── OpenApiConfiguration.java     # Swagger config
│   │   │   ├── controller/
│   │   │   │   ├── ProductController.java
│   │   │   │   ├── CategoryController.java
│   │   │   │   ├── SupplierController.java
│   │   │   │   ├── TransactionController.java
│   │   │   │   └── ReportController.java
│   │   │   ├── dto/                              # Data Transfer Objects
│   │   │   │   ├── category/
│   │   │   │   ├── product/
│   │   │   │   ├── supplier/
│   │   │   │   ├── transaction/
│   │   │   │   └── report/
│   │   │   ├── entity/                           # JPA Entities
│   │   │   │   ├── category.java
│   │   │   │   ├── product.java
│   │   │   │   ├── supplier.java
│   │   │   │   ├── stockLog.java
│   │   │   │   ├── transactionHistory.java
│   │   │   │   └── transactionDetail.java
│   │   │   ├── exception/
│   │   │   │   └── GlobalExceptionHandler.java   # Exception handling
│   │   │   ├── repository/                       # JPA Repositories
│   │   │   │   ├── ProductRepository.java
│   │   │   │   ├── CategoryRepository.java
│   │   │   │   ├── SupplierRepository.java
│   │   │   │   ├── StockLogRepository.java
│   │   │   │   ├── TransactionHistoryRepository.java
│   │   │   │   └── TransactionDetailRepository.java
│   │   │   └── service/                          # Business Logic
│   │   │       ├── ProductService.java
│   │   │       ├── CategoryService.java
│   │   │       ├── SupplierService.java
│   │   │       ├── TransactionService.java
│   │   │       └── ReportService.java
│   │   └── resources/
│   │       ├── application.properties            # Main configuration
│   │       ├── application example.properties    # Config template
│   │       └── logback-spring.xml               # Logging configuration
│   └── test/
│       └── java/                                # Unit tests
├── logs/                                        # Log files (auto-generated)
│   ├── application.log
│   └── endpoint-access.log
├── target/                                      # Build output
├── pom.xml                                      # Maven dependencies
└── README.md                                    # This file
```

---

## 🔍 Usage Examples

### 1. Setup Initial Data

```bash
# Create Category
curl -X POST http://localhost:8088/api/v1/category \
  -H "Content-Type: application/json" \
  -d '{"category_name": "Electronics"}'

# Create Supplier
curl -X POST http://localhost:8088/api/v1/supplier \
  -H "Content-Type: application/json" \
  -d '{"supplier_name": "PT. Tech Store", "contact_info": "081234567890"}'

# Create Product
curl -X POST http://localhost:8088/api/v1/product \
  -H "Content-Type: application/json" \
  -d '{
    "sku": "LAPTOP001",
    "product_name": "Laptop ASUS ROG",
    "category_id": 1,
    "supplier_id": 1,
    "current_stock": 50,
    "price": 15000000
  }'
```

### 2. Create Transaction

```bash
curl -X POST http://localhost:8088/api/v1/transaction \
  -H "Content-Type: application/json" \
  -d '{
    "transaction_date": "2026-02-13",
    "items": [
      {"product_id": 1, "qty": 2},
      {"product_id": 2, "qty": 1}
    ]
  }'
```

**Expected Response:**
```json
{
  "message": "Transaction sent to Kafka successfully",
  "data": {
    "transaction_date": "2026-02-13",
    "items": [
      {"product_id": 1, "qty": 2},
      {"product_id": 2, "qty": 1}
    ]
  },
  "errors": null
}
```

**Log Output:**
```
INFO  - Received transaction: date=2026-02-13, items=2
INFO  - Processing item: product_id=1, qty=2, current_stock=50
INFO  - Processing item: product_id=2, qty=1, current_stock=30
INFO  - Transaction sent to Kafka successfully: date=2026-02-13, items=2, estimated_total_price=30500000.0
```

### 3. Get Sales Report

```bash
curl "http://localhost:8088/api/v1/report/sales?start_date=2026-01-01&end_date=2026-12-31"
```

---

## ⚠️ Error Handling

### Common Error Responses

#### 404 Not Found
```json
{
  "message": "Error",
  "data": null,
  "errors": "Product not found with id: 999"
}
```

#### 400 Bad Request - Insufficient Stock
```json
{
  "message": "Error",
  "data": null,
  "errors": "Insufficient stock for product: Laptop. Available: 5, Requested: 10"
}
```

#### 400 Bad Request - Validation Error
```json
{
  "message": "Validation Error",
  "data": null,
  "errors": "product_name: must not be blank, price: must be greater than 0"
}
```

#### 409 Conflict - Cannot Delete
```json
{
  "message": "Error",
  "data": null,
  "errors": "Cannot delete product. It is currently being used in 5 transaction detail(s)"
}
```

---

## 🐛 Troubleshooting

### 1. Cannot Connect to Database
```
Error: Connection to localhost:5432 refused
```
**Solution:**
- Ensure PostgreSQL is running
- Check database credentials in `application.properties`
- Test connection: `psql -U username -d database_name`

### 2. Kafka Connection Error
```
ERROR - Failed to send transaction to Kafka: error=Connection refused
```
**Solution:**
- Ensure Kafka broker is running on port 9092
- Check that Zookeeper is also running
- Verify `kafka.bootstrap-servers` in config

### 3. Table Not Found
```
ERROR: relation "product" does not exist
```
**Solution:**
- Set `spring.jpa.hibernate.ddl-auto=update` in config
- Or create tables manually according to schema

### 4. Port Already in Use
```
Error: Port 8088 is already in use
```
**Solution:**
- Change port in `application.properties`: `server.port=8089`
- Or kill the process using port 8088

---

## 📊 Monitoring

### Check Application Health
```bash
curl http://localhost:8088/actuator/health
```

### View Logs Real-time
```bash
# Application logs
tail -f logs/application.log

# Endpoint access logs
tail -f logs/endpoint-access.log
```

### Monitor Kafka Topic
```bash
# List topics
kafka-topics.sh --list --bootstrap-server localhost:9092

# Consume messages
kafka-console-consumer.sh --bootstrap-server localhost:9092 \
  --topic sales-transaction-yourname \
  --from-beginning
```

---

## 🤝 Contributing

1. Fork the repository
2. Create feature branch: `git checkout -b feature/AmazingFeature`
3. Commit changes: `git commit -m 'Add some AmazingFeature'`
4. Push to branch: `git push origin feature/AmazingFeature`
5. Open Pull Request

---

**Happy Coding! 🚀**
