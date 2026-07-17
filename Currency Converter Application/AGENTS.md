## 23. AI Coding Rules

- Always write **clean, maintainable code**. Adhere to **SOLID** and **Clean Architecture** principles.
- Do not duplicate code; extract common logic into shared methods or utilities.
- Keep methods small and focused (Single Responsibility). Use helper functions where needed.
- In Java, follow naming conventions: classes `PascalCase`, methods/vars `camelCase`. In React, use functional components and hooks consistently.
- Write **reusable React components** (e.g. generic `Button`, `Input`, `Table`) to avoid repetition.
- Use **environment variables** (e.g. `process.env`) for all configurable values (API keys, URLs). Never hardcode secrets or credentials.
- Include **input validation** on both frontend and backend. On the backend, use JSR-303 annotations and global handlers.
- Include **exception handling** for all error-prone operations. For example, catch HTTP timeouts and throw meaningful custom exceptions.
- Protect existing functionality: adding new code should not break previously working features.
- Write code that is **production-ready**: consider edge cases, error messages, and logging.
- Always explain new files or classes with comments or README updates as needed.
- Ensure code is **scalable and maintainable** for future features (e.g. use interfaces, avoid monolithic blocks).
- Follow project style guidelines (indentation, file naming, etc.).
- Before finishing a task, think of tests or checks that should be added (unit tests for service logic, etc.).
- Use meaningful commit messages summarizing changes.
- Do not skip any validation steps or exception branches.

