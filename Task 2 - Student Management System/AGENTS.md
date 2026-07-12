# AGENTS.md

# Student Management System - AI Development Rules

These rules apply to every task generated during the development of this project.

The goal is to build a production-quality Student Management System using Java Full Stack while keeping the implementation aligned with the Software Design Document (SDD).

---

# General Principles

- Build production-quality software.
- Follow the Software Design Document exactly.
- Implement only the current prompt.
- Never implement future milestones early.
- Never make assumptions if requirements are unclear.
- Ask for clarification instead.

---

# Code Quality

- Follow SOLID principles.
- Follow Clean Code principles.
- Follow DRY.
- Follow KISS.
- Follow YAGNI.
- Keep methods focused on a single responsibility.
- Keep methods reasonably small.
- Avoid duplicated logic.
- Prefer composition over inheritance.
- Use meaningful and descriptive names.
- Never use names like temp, test, data1, foo, bar.
- Avoid magic numbers and hardcoded values.
- Extract constants into configuration or constants classes.
- Write self-documenting code.
- Add comments only when they explain intent rather than implementation.

---

# Java Standards

- Use Java 21.
- Use Spring Boot best practices.
- Use constructor injection only.
- Never use field injection.
- Prefer immutable DTOs.
- Follow standard Java package naming conventions.
- Keep packages modular and organized.

---

# Architecture Rules

Strictly follow the layered architecture.

React UI

↓

Controller

↓

Service

↓

Repository

↓

Database

Rules:

- Controllers only handle HTTP requests and responses.
- Controllers must never contain business logic.
- Services contain all business logic.
- Repositories only access the database.
- Entities only represent persistence models.
- Never expose JPA entities directly through REST APIs.
- Always use Request DTOs and Response DTOs.
- Keep configuration inside the config package.
- Keep utilities inside the util package.
- Keep validation logic inside the validation package.
- Keep exception classes inside the exception package.

---

# REST API Rules

Always follow RESTful design.

Use appropriate:

- HTTP methods
- Status codes
- Request validation
- Response structures

Never expose:

- Stack traces
- SQL errors
- Internal exception messages

Use a consistent API response structure.

---

# Validation Rules

Every endpoint must validate input.

Validate:

- Email
- Phone Number
- Roll Number
- Required Fields
- Dates
- Enum Values
- Uploaded Files

Never rely only on frontend validation.

Backend validation is mandatory.

---

# Exception Handling

Never write try-catch inside controllers unless absolutely necessary.

Use GlobalExceptionHandler.

Every exception must map to:

- HTTP Status
- Error Message
- Timestamp
- Request Path

Every new feature must include appropriate exception handling.

---

# Database Rules

Normalize the schema.

Use:

- Primary Keys
- Foreign Keys
- Constraints
- Indexes

Avoid duplicated data.

Never write unnecessary queries.

Use Spring Data JPA whenever possible.

---

# Frontend Rules

Use React Functional Components only.

Use Hooks.

Separate:

- Pages
- Components
- Hooks
- Context
- API
- Utils

Never place API calls directly inside reusable UI components.

Use Axios service classes.

Create reusable UI components whenever possible.

---

# UI / UX Rules

Follow the Software Design Document Section 9 exactly.

Use only the defined:

- Color palette
- Typography
- Spacing
- Component hierarchy
- Responsive breakpoints
- Motion guidelines

Do NOT introduce:

- Glassmorphism
- Neumorphism
- Random gradients
- Fancy dashboard themes
- Heavy animations
- Random UI libraries
- Random colors

Keep the interface clean, professional, and academic.

---

# Git Rules

Every completed prompt must:

- Build successfully.
- Preserve existing functionality.
- Pass previous tests.
- Include an appropriate Git commit message.

Never combine unrelated features into one commit.

---

# Documentation

Whenever a public API changes:

- Update README if necessary.
- Update API documentation.
- Update Postman collection.
- Update screenshots when UI changes.

Document public classes and methods where helpful.

---

# Testing

Before completing every task verify:

- Backend builds successfully.
- Frontend builds successfully.
- Application starts.
- Existing functionality still works.
- No compilation errors.
- No runtime exceptions.
- No unnecessary warnings.

---

# AI Behaviour Rules

Implement only the requested prompt.

Never generate placeholder code.

Never leave TODO comments.

Never generate incomplete methods.

Never skip:

- Validation
- Exception handling
- Error responses

Never modify unrelated files.

If an architectural conflict exists:

Stop.

Explain the conflict.

Recommend a solution.

Do not silently change the architecture.

---

# After Every Task

Always provide:

1. Summary of completed work.

2. Files created.

3. Files modified.

4. Explanation of architectural decisions.

5. Manual testing steps.

6. Suggested Git commit message.

7. Confirmation that no previous functionality was broken.

8. Recommendation for the next development prompt.

---

# Development Goal

The final application should be:

- Internship Ready
- Production Ready
- Portfolio Ready
- GitHub Ready
- Resume Ready
- Recruiter Friendly
- Clean
- Modular
- Scalable
- Maintainable
- Well Documented