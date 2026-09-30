# Warehouse Management System 📦

A full-stack Java desktop application for managing warehouse inventory, tracking transactions, and handling secure, role-based user access. Built to demonstrate practical backend development and relational database engineering skills.

## 🎥 Demo

Since this is a desktop application, you can see how it looks like in assets folder in project.

## 🛠️ Built With

**Backend**
- Java [confirm version — 17 or 25?]
- Spring Boot — business logic and database access
- PostgreSQL — relational data storage
- Maven — dependency management

**Frontend**
- Java Swing — desktop UI

## ✨ Features

- **Role-Based Logins** — separate dashboards and permissions for Admins, Sales Managers, and Warehouse Managers
- **Live Inventory Tracking** — stock quantities update automatically on restock (IN) or sale (OUT)
- **Low Stock Alerts** — scans inventory on login and flags anything below its safety threshold
- **Transaction History** — logs who processed each transaction, when, and with which supplier/dealer
- **Bill Generation** — generates formatted, printable receipts for individual transactions

## 🏗️ Architecture

Spring Boot handles the service layer, dependency injection, and PostgreSQL persistence via [JPA/Hibernate or JDBC — confirm which], while Swing provides the native desktop UI. This separation keeps business logic testable independently of the UI layer.

## 🚀 Getting Started

### Prerequisites
- Java [version] JDK
- PostgreSQL installed locally
- Maven (or use the included `mvnw` wrapper)

### 1. Clone the repo
```bash
git clone https://github.com/KhataiAlizade/WareHouseManagement.git
cd WareHouseManagement
```

### 2. Database Setup
Open pgAdmin or the SQL command line and create an empty database:
```sql
CREATE DATABASE warehouse_db;
```

### 3. Configure the App
In `src/main/resources/application.properties`, set your PostgreSQL credentials:
```properties
spring.datasource.username=YOUR_USERNAME
spring.datasource.password=YOUR_PASSWORD
```

### 4. Run the Application
```bash
mvnw.cmd spring-boot:run
```
*(macOS/Linux: `./mvnw spring-boot:run`)*
