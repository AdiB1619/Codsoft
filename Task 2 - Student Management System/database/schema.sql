-- =============================================================================
-- Student Management System — Database Schema
-- Target:   MySQL 8.0+
-- Charset:  utf8mb4 (full Unicode, including emoji)
-- Engine:   InnoDB (for foreign-key enforcement and ACID transactions)
--
-- Run order: this file first, then seed-data.sql
-- Usage:     mysql -u <user> -p sms_db < schema.sql
-- =============================================================================

-- Safety: ensure we are working in the correct database
CREATE DATABASE IF NOT EXISTS sms_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE sms_db;

-- =============================================================================
-- TABLE: courses
-- Created before students because students.course_id references it.
-- =============================================================================
CREATE TABLE IF NOT EXISTS courses (
    id             BIGINT          NOT NULL AUTO_INCREMENT,
    course_code    VARCHAR(15)     NOT NULL,
    course_name    VARCHAR(100)    NOT NULL,
    department     VARCHAR(100)    NOT NULL,
    duration_years INT             NOT NULL,

    CONSTRAINT pk_courses          PRIMARY KEY (id),
    -- idx_courses_course_code (SDD §6.5): unique index enforces course_code uniqueness
    -- and speeds up duplicate-check queries.
    CONSTRAINT uq_courses_code     UNIQUE KEY idx_courses_course_code (course_code)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

-- =============================================================================
-- TABLE: students
-- =============================================================================
CREATE TABLE IF NOT EXISTS students (
    id                BIGINT          NOT NULL AUTO_INCREMENT,
    roll_number       VARCHAR(20)     NOT NULL,
    first_name        VARCHAR(50)     NOT NULL,
    last_name         VARCHAR(50)     NOT NULL,
    email             VARCHAR(100)    NOT NULL,
    phone_number      VARCHAR(15)     NOT NULL,
    date_of_birth     DATE            NOT NULL,
    gender            VARCHAR(10)     NOT NULL,
    address           VARCHAR(200)    NOT NULL,
    course_id         BIGINT          NOT NULL,
    enrollment_date   DATE            NOT NULL,
    grade             DECIMAL(5, 2)   NULL,
    status            VARCHAR(15)     NOT NULL DEFAULT 'ACTIVE',
    profile_image_url VARCHAR(255)    NULL,
    created_at        DATETIME        NOT NULL,
    updated_at        DATETIME        NOT NULL,

    CONSTRAINT pk_students               PRIMARY KEY (id),

    -- idx_students_roll_number (SDD §6.5): unique index enforces roll_number
    -- uniqueness and speeds up roll-number lookups.
    CONSTRAINT uq_students_roll_number   UNIQUE KEY idx_students_roll_number (roll_number),

    -- idx_students_email (SDD §6.5): unique index enforces email uniqueness
    -- and speeds up duplicate-email checks.
    CONSTRAINT uq_students_email         UNIQUE KEY idx_students_email (email),

    -- idx_students_course_id (SDD §6.5): speeds up the FK join and
    -- course-based list filtering.
    INDEX idx_students_course_id (course_id),

    -- idx_students_status (SDD §6.5): speeds up status-based filtering
    -- (ACTIVE / INACTIVE / GRADUATED / SUSPENDED).
    INDEX idx_students_status (status),

    -- idx_students_enrollment_date (SDD §6.5): speeds up sort-by-enrollment-date.
    INDEX idx_students_enrollment_date (enrollment_date),

    -- Foreign key: every student must belong to an existing course.
    -- ON DELETE RESTRICT prevents accidental deletion of a course that still
    -- has enrolled students, preserving referential integrity (SDD §6.4).
    CONSTRAINT fk_students_course_id
        FOREIGN KEY (course_id)
        REFERENCES courses (id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;
