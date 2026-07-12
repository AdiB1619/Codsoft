# API Documentation


### 7.1 Conventions

- **Base path:** `/api/v1`
- **Format:** JSON request/response bodies; `multipart/form-data` for the image upload endpoint only.
- **Response envelope** — every non-export response is wrapped for consistency:

```json
{
  "success": true,
  "message": "Human-readable summary",
  "data": { "...": "..." },
  "timestamp": "2026-07-07T10:15:30Z"
}
```

- **Paginated data** additionally shapes `data` as:

```json
{
  "content": [ { "...": "..." } ],
  "pageNumber": 0,
  "pageSize": 10,
  "totalElements": 57,
  "totalPages": 6,
  "last": false
}
```

- **Errors** always return the shape defined in Section 11.2 — never a bare stack trace or an ad hoc shape.

### 7.2 Endpoint Summary

| # | Method | Endpoint | Purpose |
|---|---|---|---|
| 1 | POST | `/api/v1/students` | Create a student |
| 2 | GET | `/api/v1/students` | List students (paginated, sorted, filtered, searched) |
| 3 | GET | `/api/v1/students/{id}` | Get one student |
| 4 | PUT | `/api/v1/students/{id}` | Update a student |
| 5 | PATCH | `/api/v1/students/{id}/status` | Change only a student's status |
| 6 | DELETE | `/api/v1/students/{id}` | Delete a student |
| 7 | POST | `/api/v1/students/{id}/profile-image` | Upload/replace profile image |
| 8 | DELETE | `/api/v1/students/{id}/profile-image` | Remove profile image |
| 9 | GET | `/api/v1/students/export` | Export filtered students as CSV |
| 10 | GET | `/api/v1/courses` | List all courses |
| 11 | POST | `/api/v1/courses` | Create a course |

### 7.3 `POST /api/v1/students` — Create Student

**Purpose:** Create a new student record.

**Request Body:**
```json
{
  "firstName": "Aarav",
  "lastName": "Sharma",
  "email": "aarav.sharma@example.com",
  "phoneNumber": "+919876543210",
  "dateOfBirth": "2005-08-14",
  "gender": "MALE",
  "address": "221B Baker Street, Pune, MH",
  "rollNumber": "CS2023045",
  "courseId": 3,
  "enrollmentDate": "2023-07-01",
  "status": "ACTIVE"
}
```

**Success Response — `201 Created`:**
```json
{
  "success": true,
  "message": "Student created successfully",
  "data": {
    "id": 101,
    "firstName": "Aarav",
    "lastName": "Sharma",
    "email": "aarav.sharma@example.com",
    "rollNumber": "CS2023045",
    "course": { "id": 3, "courseCode": "CSE", "courseName": "Computer Science" },
    "status": "ACTIVE",
    "profileImageUrl": null,
    "createdAt": "2026-07-07T10:15:30Z"
  },
  "timestamp": "2026-07-07T10:15:30Z"
}
```

**Status Codes:**

| Code | Meaning |
|---|---|
| 201 | Created successfully |
| 400 | Validation failure (missing/malformed field) |
| 404 | `courseId` does not reference an existing course |
| 409 | `email` or `rollNumber` already exists |
| 500 | Unexpected server error |

**Validation:** full field-level rules in Section 10.

**Possible Errors:** missing required field → 400 · invalid email format → 400 · duplicate email → 409 · duplicate roll number → 409 · non-existent `courseId` → 404.

### 7.4 `GET /api/v1/students` — List Students

**Purpose:** Retrieve a paginated, sortable, filterable, searchable list of students.

**Query Parameters:**

| Param | Type | Default | Notes |
|---|---|---|---|
| `page` | int | `0` | Zero-indexed |
| `size` | int | `10` | Max `100` |
| `sortBy` | string | `id` | One of: `firstName`, `lastName`, `rollNumber`, `enrollmentDate`, `grade` |
| `sortDir` | string | `asc` | `asc` or `desc` |
| `search` | string | — | Matches partial, case-insensitive `firstName`/`lastName`/`email`/`rollNumber` |
| `courseId` | long | — | Filter to one course |
| `status` | string | — | One of the status enum values |

**Success Response — `200 OK`:** the paginated envelope from Section 7.1; `data.content` is an array of student summaries.

**Status Codes:** `200` · `400` (invalid `sortBy` or `status` value) · `500`.

**Possible Errors:** unrecognized `sortBy` → 400 (never silently falls back, and the raw string is never passed straight into a query) · invalid `status` value → 400.

### 7.5 `GET /api/v1/students/{id}` — Get Student by ID

**Purpose:** Retrieve one student's full profile.

**Status Codes:** `200` · `404` (no student with that id) · `500`.

### 7.6 `PUT /api/v1/students/{id}` — Update Student

**Purpose:** Full update of an existing student.

**Request Body:** same shape as Create (Section 7.3).

**Status Codes:** `200` · `400` · `404` (student or referenced course not found) · `409` (email/rollNumber collides with a *different* student) · `500`.

### 7.7 `PATCH /api/v1/students/{id}/status` — Update Status Only

**Purpose:** Quickly transition a student's status (e.g. `ACTIVE` → `GRADUATED`) without resubmitting the whole form.

**Request Body:**
```json
{ "status": "GRADUATED" }
```

**Status Codes:** `200` · `400` (invalid enum value) · `404` · `500`.

### 7.8 `DELETE /api/v1/students/{id}` — Delete Student

**Purpose:** Permanently remove a student record.

**Status Codes:** `204` (no body) · `404` · `500`.

### 7.9 `POST /api/v1/students/{id}/profile-image` — Upload Profile Image

**Purpose:** Upload or replace a student's profile photo.

**Request Body:** `multipart/form-data`, field name `file`.

**Success Response — `200 OK`:**
```json
{
  "success": true,
  "message": "Profile image uploaded successfully",
  "data": { "profileImageUrl": "/uploads/students/8f14e-avatar.jpg" },
  "timestamp": "2026-07-07T10:20:00Z"
}
```

**Status Codes:** `200` · `400` (wrong type or over 2MB) · `404` (student not found) · `500`.

**Validation:** JPG/PNG only · ≤2MB · the server checks the actual file signature, not just the client-supplied `Content-Type`.

### 7.10 `DELETE /api/v1/students/{id}/profile-image` — Remove Profile Image

**Purpose:** Delete the stored image and revert `profileImageUrl` to `null`.

**Status Codes:** `204` · `404` · `500`.

### 7.11 `GET /api/v1/students/export` — Export CSV

**Purpose:** Export the current filtered/searched/sorted student list as a downloadable CSV — same query params as Section 7.4, minus pagination (exports the full matching set, capped at 10,000 rows as a safety limit).

**Response:** `Content-Type: text/csv`, `Content-Disposition: attachment; filename="students-export-2026-07-07.csv"`, streamed body.

**Status Codes:** `200` · `400` · `500`.

### 7.12 `GET /api/v1/courses` — List Courses

**Purpose:** Retrieve all courses, primarily to populate dropdowns and filters.

**Status Codes:** `200` · `500`.

### 7.13 `POST /api/v1/courses` — Create Course

**Purpose:** Add a new course. This is an administrative utility endpoint — full Course CRUD (update/delete) is future scope (Section 20).

**Request Body:**
```json
{
  "courseCode": "CSE",
  "courseName": "Computer Science",
  "department": "Engineering",
  "durationYears": 4
}
```

**Status Codes:** `201` · `400` · `409` (duplicate `courseCode`) · `500`.

> **Recommended addition:** expose Spring Boot Actuator's `GET /actuator/health` for uptime checks once the app is deployed (Section 13, Prompt 34).

---

## 8. Frontend Design

### 8.1 Page Inventory

| Page | Route | Purpose | Key States |
|---|---|---|---|
| Dashboard | `/` | At-a-glance overview: totals, quick actions | Loading, populated |
| Student List | `/students` | Browse, search, sort, filter, paginate students | Loading, populated, empty, error |
| Add Student | `/students/new` | Create a new student | Idle, validating, submitting, error |
| Edit Student | `/students/:id/edit` | Update an existing student | Loading, idle, validating, submitting, error, not-found |
| Student Details | `/students/:id` | Read-only full profile view | Loading, populated, not-found |
| 404 Not Found | `*` | Catch-all for unknown routes | Static |

Every page shares a persistent **Navbar** and **Footer** via `AppLayout` (Section 5.3), and every mutating action produces a **Toast** (success/error/warning/info).