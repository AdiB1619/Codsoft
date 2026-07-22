# CodSoft Java Development Internship

A collection of projects built during the **CodSoft Java Development Internship**, demonstrating progression from core Java console applications to production-quality full-stack web applications.

---

## Projects

| # | Project | Type | Stack | Status |
|---|---------|------|-------|--------|
| 1 | [ATM Interface](#1-atm-interface) | Console App | Java 17, Maven, JUnit 5 | ✅ Complete |
| 2 | [Currency Converter Application](#2-currency-converter-application) | Full-Stack Web App | Spring Boot 3, React, Tailwind CSS, H2/MySQL | ✅ Complete |
| 3 | [Student Management System](#3-student-management-system) | Full-Stack Web App | Java 21, Spring Boot 3, React, Vite, Tailwind CSS, MySQL | ✅ Complete |

---

## 1. ATM Interface

> **Folder:** [`ATM Interface/`](./ATM%20Interface/)

A robust, object-oriented console application simulating a real-world ATM. Focuses on clean architecture, file-based persistence, and defensive error handling.

### Features
- SHA-256 hashed PIN authentication with 3-attempt lockout
- Cash deposit and withdrawal with denomination and daily limit enforcement
- Transaction history with timestamps and unique transaction IDs
- Mini-statement (last 5 transactions)
- Secure PIN change flow
- File-based persistence across sessions (`.txt` and `.csv`)
- Custom exception hierarchy — application never crashes unpredictably

### Tech Stack
```
Java 17 · Maven · JUnit 5 · OOP · File I/O
```

### Run
```bash
cd "ATM Interface"
mvn clean package
java -jar target/atm-banking-system-1.0.0.jar
```

---

## 2. Currency Converter Application

> **Folder:** [`Currency Converter Application/`](./Currency%20Converter%20Application/)

A full-stack currency conversion web application with live exchange rates, conversion history, and a favorites system.

### Features
- Live exchange rates via ExchangeRate-API with open.er-api.com fallback
- Conversion history persistence with delete support
- Favorite currencies management
- Responsive React frontend with Tailwind CSS
- Full input validation and global exception handling
- Integration and unit tests

### Tech Stack
```
Java 17 · Spring Boot 3 · Spring Data JPA · H2/MySQL · React · Vite · Tailwind CSS · Axios
```

### Architecture
```
React (Vite)  →  Spring Boot REST API  →  Service Layer  →  Repository  →  Database
                                       ↕
                              ExchangeRate-API (live rates)
```

### Run
```bash
# Backend
cd "Currency Converter Application"
mvn spring-boot:run

# Frontend
cd "Currency Converter Application/frontend"
npm install && npm run dev
```

---

## 3. Student Management System

> **Folder:** [`Task 2 - Student Management System/`](./Task%202%20-%20Student%20Management%20System/)

A production-quality full-stack Student Management System — the most architecturally complete project in this internship. Built with strict layered architecture, database-level integrity, and a fully featured React UI.

### Features
- Full CRUD for student records (create, read, update, delete)
- Paginated, searchable, filterable student list with sortable columns
- Course management with FK-enforced enrollment
- Student status lifecycle (Active / Inactive / Graduated / Suspended)
- Profile image upload and removal
- CSV export of filtered student data
- Dashboard with live statistics (total, active, courses, new this month)
- Custom `@MinAge` Bean Validation annotation
- JPA Auditing for automatic `createdAt` / `updatedAt` timestamps
- HikariCP connection pooling
- OpenAPI / Swagger UI documentation
- Global exception handler covering 400 / 404 / 409 / 500 scenarios

### Tech Stack
```
Java 21 · Spring Boot 3.3 · Spring Data JPA · Hibernate · MySQL 8 · Lombok
React 18 · Vite · Tailwind CSS · Axios · React Router v6
```

### Architecture
```
React (Vite + Tailwind)
        ↓ Axios
Spring Boot REST Controllers
        ↓
Service Layer (business logic, DTO mapping)
        ↓
Spring Data JPA Repositories
        ↓
MySQL 8 (InnoDB, indexed, FK-constrained)
```

### Database Schema
- `courses` table — course code, name, department, duration
- `students` table — 15 fields, unique constraints on `roll_number` and `email`, FK to `courses`, 5 performance indexes

### Run
```bash
# 1. Create the MySQL database
mysql -u root -p < "Task 2 - Student Management System/database/schema.sql"
mysql -u root -p sms_db < "Task 2 - Student Management System/database/seed-data.sql"

# 2. Configure credentials
# Edit: Task 2 - Student Management System/backend/src/main/resources/application-dev.properties

# 3. Start backend (port 8080)
cd "Task 2 - Student Management System/backend"
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# 4. Start frontend (port 5173)
cd "Task 2 - Student Management System/frontend"
npm install && npm run dev
```

### API Documentation
Available at `http://localhost:8080/swagger-ui/index.html` when the backend is running.

---

## Skills Demonstrated

| Area | Skills |
|------|--------|
| **Core Java** | OOP, Collections, Exception Handling, File I/O, Enums |
| **Spring Boot** | REST APIs, Spring Data JPA, Bean Validation, Auditing, Multipart Upload |
| **Database** | MySQL schema design, indexing, FK constraints, HikariCP |
| **Frontend** | React functional components, custom hooks, Axios service layer, Tailwind CSS |
| **Software Design** | Layered architecture, DTO pattern, Global exception handling, Clean Code |
| **Tooling** | Maven, Vite, Git, OpenAPI/Swagger |

---

## Author

**Aditya Bachute** — [@AdiB1619](https://github.com/AdiB1619)

*CodSoft Java Development Internship · 2026*
