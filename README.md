# Warehouse Management System 📦

Welcome to my Warehouse Management System! This is a full-stack Java desktop application I developed to demonstrate strong practical fundamentals in backend development and relational database engineering. 

It provides a complete, easy-to-use interface for managing warehouse inventory, tracking business transactions, and handling secure, role-based user access.

## 🛠️ Built With
* **Java** (v25)
* **Spring Boot** - For backend business logic and database connections
* **PostgreSQL** - Relational database management
* **Java Swing** - For the desktop user interface
* **Maven** - Dependency management

## ✨ What It Does
* **Role-Based Logins:** Secure access levels that show different dashboards for Admins, Sales Managers, and Warehouse Managers.
* **Live Inventory Tracking:** Automatically updates product stock quantities when items are restocked (IN) or sold (OUT).
* **Low Stock Alerts:** Scans inventory levels upon login and immediately warns the user if any products fall below their minimum safety threshold.
* **Transaction History:** Keeps a detailed, reliable record of who processed a transaction, when it happened, and which supplier or dealer was involved.
* **Bill Generation:** Creates formatted, printable text receipts for individual transactions with a single click.

## 🚀 How to Run It Locally

### 1. Database Setup
You will need [PostgreSQL](https://www.postgresql.org/) installed on your computer.
* Open pgAdmin or your SQL command line.
* Create a brand new, empty database called `warehouse_db`.

### 2. Configure the App
Open the project in your favorite IDE (like VS Code or IntelliJ).
Navigate to the `src/main/resources/application.properties` file and update it with your personal PostgreSQL username and password:
```properties
spring.datasource.username=YOUR_USERNAME
spring.datasource.password=YOUR_PASSWORD
