# AGENTS.md

# Student Management System – AI Development Rules

## Project Overview

This project is a production-quality Java Full Stack Student Management System built using:

- Java 21
- Spring Boot
- Spring MVC
- Spring Data JPA
- Hibernate
- MySQL
- React
- Vite
- Tailwind CSS
- Axios

The complete Software Design Document (SDD) is located at:

docs/SDD.md

This document is the single source of truth for architecture, folder structure, REST APIs, database design, UI/UX, validation, development roadmap, and coding standards.

If any generated code conflicts with the SDD, the SDD always takes precedence.

---

# Development Rules

- Implement only the current prompt.
- Never implement future milestones early.
- Never make assumptions when requirements are unclear.
- Ask for clarification instead.
- Never redesign the architecture defined in the SDD.
- Never create additional folders or packages unless required by the SDD.

---

# Code Quality

Follow:

- SOLID
- Clean Code
- DRY
- KISS
- YAGNI

Rules:

- Keep methods focused on one responsibility.
- Avoid duplicate logic.
- Prefer composition over inheritance.
- Use meaningful names.
- Never use magic numbers.
- Extract constants.
- Write self-documenting code.
- Comment only when necessary.

---

# Java Standards

- Use Java 21.
- Use constructor injection only.
- Never use field injection.
- Use Spring Boot best practices.
- Keep packages modular.
- Use immutable DTOs where appropriate.

---

# Architecture

Strictly follow:

React

↓

Controller

↓

Service

↓

Repository

↓

Database

Rules:

- Controllers handle HTTP only.
- Services contain business logic.
- Repositories access data.
- Entities represent persistence only.
- Never expose entities through REST APIs.
- Always use Request DTOs and Response DTOs.
- Keep validation, configuration, utilities, and exceptions in their dedicated packages.

---

# REST API

Always:

- Use REST conventions.
- Return proper HTTP status codes.
- Return consistent JSON responses.
- Validate every request.
- Never expose stack traces.
- Never expose SQL exceptions.

---

# Validation

Always validate:

- Email
- Phone Number
- Roll Number
- Required Fields
- Dates
- Enum Values
- File Uploads

Backend validation is mandatory.

---

# Exception Handling

Use GlobalExceptionHandler.

Every exception must return:

- HTTP Status
- Error Message
- Timestamp
- Request Path

Never ignore errors.

---

# Database

Follow the schema defined in the SDD.

Use:

- Primary Keys
- Foreign Keys
- Constraints
- Indexes

Avoid duplicate data.

---

# Frontend

Use:

- React Functional Components
- Hooks
- Axios Service Layer

Separate:

- Pages
- Components
- Hooks
- API
- Context
- Utils

Never call APIs directly from reusable UI components.

---

# UI / UX

Strictly follow Section 9 of the SDD.

Use only the defined:

- Color palette
- Typography
- Spacing
- Component hierarchy
- Responsive breakpoints
- Motion guidelines

Do NOT use:

- Glassmorphism
- Neumorphism
- Heavy animations
- Fancy dashboard themes
- Random gradients
- Random UI libraries
- Unapproved colors

---

# Git

After every completed prompt:

- Ensure project builds successfully.
- Ensure no existing functionality is broken.
- Suggest a meaningful Git commit message.

Never combine unrelated features into one commit.

---

# Documentation

Whenever APIs or setup change:

- Update README.
- Update API documentation.
- Update Postman collection.
- Update screenshots if UI changes.

---

# Testing

Before completing every task verify:

- Backend builds successfully.
- Frontend builds successfully.
- Application starts.
- Existing functionality still works.
- No compilation errors.
- No runtime exceptions.

---

# AI Behaviour

Never:

- Generate placeholder code.
- Leave TODO comments.
- Leave incomplete methods.
- Skip validation.
- Skip exception handling.
- Modify unrelated files.
- Introduce unnecessary dependencies.
- Change the architecture without approval.

If an architectural conflict exists:

- Stop.
- Explain the conflict.
- Recommend a solution.
- Wait for approval.

---

# Task Completion

After every prompt provide:

1. Summary of completed work.
2. Files created.
3. Files modified.
4. Architecture explanation.
5. Manual testing steps.
6. Suggested Git commit message.
7. Confirmation that previous functionality still works.
8. Recommendation for the next prompt.

---

# Final Goal

The completed application must be:

- Internship Ready
- Production Quality
- Portfolio Quality
- GitHub Ready
- Resume Ready
- Recruiter Friendly
- Clean
- Modular
- Scalable
- Maintainable
- Well Documented