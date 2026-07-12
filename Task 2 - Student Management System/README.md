<div align="center">

# 🎓 Student Management System

**A full-stack CRUD application for managing student records — built with Spring Boot and React.**

[![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-18-61DAFB?style=for-the-badge&logo=react&logoColor=black)](https://react.dev)
[![MySQL](https://img.shields.io/badge/MySQL-8-4479A1?style=for-the-badge&logo=mysql&logoColor=white)](https://www.mysql.com)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg?style=for-the-badge)](LICENSE)

[Live Demo](https://student-management-system-aditya.vercel.app) · [Report Bug](https://github.com/AdiB1619/Codsoft/tree/main/Task%202%20-%20Student%20Management%20System/issues) · [Request Feature](https://github.com/AdiB1619/Codsoft/tree/main/Task%202%20-%20Student%20Management%20System/issues)

</div>

## 📖 Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Architecture](#architecture)
- [Tech Stack](#tech-stack)
- [Screenshots](#screenshots)
- [Getting Started](#getting-started)
- [API Documentation](#api-documentation)
- [Project Structure](#project-structure)
- [Testing](#testing)
- [Roadmap](#roadmap)
- [Contributing](#contributing)
- [License](#license)
- [Author](#author)

## Overview

The Student Management System is a production-style CRUD application for managing student records — built as part of the CodSoft Java Development Virtual Internship, and engineered to portfolio/production standards: layered Spring Boot backend, React frontend, full validation and error handling, and a documented REST API.

## Features

- ✅ Full student CRUD with server-side validation
- ✅ Paginated, searchable, sortable, filterable student list
- ✅ Course-based relational data model
- ✅ Profile image upload
- ✅ Filtered CSV export
- ✅ Toast notifications, loading skeletons, confirmation dialogs
- ✅ Fully responsive, accessible UI
- ✅ Centralized exception handling with consistent error responses
- ✅ OpenAPI/Swagger docs + Postman collection

## Architecture

![Architecture Diagram](docs/architecture-diagram.png)

Layered architecture — Controller → Service → Repository — with DTOs at every API boundary. Full breakdown in [`docs/api-documentation.md`](docs/api-documentation.md).

## Tech Stack

| Layer | Technology |
|---|---|
| Backend | Java 21, Spring Boot, Spring MVC, Spring Data JPA, Hibernate |
| Frontend | React, Vite, Tailwind CSS, Axios |
| Database | MySQL |
| Tooling | Maven, Postman, Git |
| Deployment | Render / Railway (backend), Vercel (frontend) |

## Screenshots

| Dashboard | Student List |
|---|---|
| ![Dashboard](docs/screenshots/dashboard.png) | ![Student List](docs/screenshots/student-list.png) |

| Add Student | Student Details |
|---|---|
| ![Add Student](docs/screenshots/add-student.png) | ![Student Details](docs/screenshots/student-details.png) |

## Getting Started

### Prerequisites

- Java 21+, Maven 3.9+
- Node.js 20+
- MySQL 8+

### 1. Clone the repository

```bash
git clone https://github.com/AdiB1619/Codsoft/tree/main/Task%202%20-%20Student%20Management%20System
cd student-management-system
```

### 2. Set up the database

```bash
mysql -u root -p < database/schema.sql
mysql -u root -p < database/seed-data.sql
```

### 3. Run the backend

```bash
cd backend
# set DB credentials via environment variables or application-dev.properties
mvn spring-boot:run
```

Backend runs at `http://localhost:8080`.

### 4. Run the frontend

```bash
cd frontend
cp .env.example .env
# set VITE_API_BASE_URL in .env
npm install
npm run dev
```

Frontend runs at `http://localhost:5173`.

### Environment Variables

| Variable | Location | Example |
|---|---|---|
| `DB_USERNAME` | backend | `root` |
| `DB_PASSWORD` | backend | `root` |
| `APP_CORS_ALLOWED_ORIGINS` | backend | `http://localhost:5173` |
| `VITE_API_BASE_URL` | frontend | `http://localhost:8080/api/v1` |

## API Documentation

- Interactive Swagger UI: `http://localhost:8080/swagger-ui.html` (once running locally)
- Postman collection: [`postman/Student-Management-System.postman_collection.json`](postman/Student-Management-System.postman_collection.json)
- Full endpoint reference: [`docs/api-documentation.md`](docs/api-documentation.md)

## Project Structure

See [Section 5 of the Software Design Document](#5-folder-structure) for the complete annotated tree.

## Testing

```bash
# backend
cd backend && mvn test

# postman (requires newman)
newman run postman/Student-Management-System.postman_collection.json \
  -e postman/Student-Management-System.postman_environment.json
```

## Roadmap

See [Section 20 — Future Scope](#20-future-scope) of the design document for the full list. Highlights: authentication & RBAC, Dockerization, CI/CD, cloud file storage, analytics dashboard.

## Contributing

This is a personal internship/portfolio project, but suggestions are welcome — open an issue or a PR.

## License

Distributed under the MIT License. See [`LICENSE`](LICENSE) for details.

## Author

**Aditya** — [GitHub](https://github.com/AdiB1619) · [LinkedIn](https://linkedin.com/in/Aditya)

## Acknowledgments

Built as part of the [CodSoft](https://www.codsoft.in) Java Development Virtual Internship.
`````