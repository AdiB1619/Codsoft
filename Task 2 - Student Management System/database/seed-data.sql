-- =============================================================================
-- Student Management System — Seed Data
-- Target:   MySQL 8.0+
--
-- Run AFTER schema.sql.
-- Usage:    mysql -u <user> -p sms_db < seed-data.sql
--
-- Courses are inserted first (students reference them via FK).
-- All created_at / updated_at values are set to a fixed past timestamp
-- so the seed data is deterministic and idempotent.
-- =============================================================================

USE sms_db;

-- =============================================================================
-- COURSES (6 sample courses spanning common university departments)
-- =============================================================================
INSERT IGNORE INTO courses (id, course_code, course_name, department, duration_years) VALUES
    (1,  'CSE',  'Computer Science and Engineering',  'Engineering',   4),
    (2,  'ECE',  'Electronics and Communication',      'Engineering',   4),
    (3,  'ME',   'Mechanical Engineering',             'Engineering',   4),
    (4,  'BBA',  'Bachelor of Business Administration','Management',    3),
    (5,  'BCA',  'Bachelor of Computer Applications',  'Computer Apps', 3),
    (6,  'MCA',  'Master of Computer Applications',    'Computer Apps', 2);

-- =============================================================================
-- STUDENTS (10 sample students across multiple courses and statuses)
-- Roll-number format: 2–4 uppercase letters + 3–6 digits (per Section 10)
-- Phone format: optional + followed by 10–13 digits
-- grade is NULL for some students (not yet assessed)
-- =============================================================================
INSERT IGNORE INTO students (
    id, roll_number, first_name, last_name, email, phone_number,
    date_of_birth, gender, address, course_id, enrollment_date,
    grade, status, profile_image_url, created_at, updated_at
) VALUES
    (1,  'CS2023001', 'Aarav',    'Sharma',    'aarav.sharma@example.com',    '+919876543210',
     '2005-03-14', 'MALE',   '12 MG Road, Pune, MH',            1, '2023-07-01', 87.50, 'ACTIVE',    NULL, '2023-07-01 09:00:00', '2023-07-01 09:00:00'),

    (2,  'CS2023002', 'Priya',    'Patel',     'priya.patel@example.com',     '+919823456789',
     '2005-06-22', 'FEMALE', '45 Gandhi Nagar, Ahmedabad, GJ',  1, '2023-07-01', 92.00, 'ACTIVE',    NULL, '2023-07-01 09:05:00', '2023-07-01 09:05:00'),

    (3,  'EC2022010', 'Rohan',    'Verma',     'rohan.verma@example.com',     '+919811223344',
     '2004-11-08', 'MALE',   '8 Lal Bagh, Bengaluru, KA',       2, '2022-07-15', 74.25, 'ACTIVE',    NULL, '2022-07-15 10:00:00', '2024-01-10 14:30:00'),

    (4,  'ME2021005', 'Sneha',    'Kulkarni',  'sneha.kulkarni@example.com',  '+919900112233',
     '2003-09-30', 'FEMALE', '3 Shivaji Park, Mumbai, MH',      3, '2021-07-01', 68.75, 'GRADUATED', NULL, '2021-07-01 08:00:00', '2025-06-15 12:00:00'),

    (5,  'BBA2024001','Arjun',    'Mehta',     'arjun.mehta@example.com',     '+919755667788',
     '2006-01-17', 'MALE',   '17 Jodhpur Colony, Jaipur, RJ',   4, '2024-07-01', NULL,  'ACTIVE',    NULL, '2024-07-01 09:00:00', '2024-07-01 09:00:00'),

    (6,  'BCA2023015', 'Kavya',   'Nair',      'kavya.nair@example.com',      '+919633445566',
     '2005-07-04', 'FEMALE', '22 Panampilly Nagar, Kochi, KL',  5, '2023-07-10', 95.00, 'ACTIVE',    NULL, '2023-07-10 09:15:00', '2023-07-10 09:15:00'),

    (7,  'CS2022008', 'Vikram',   'Singh',     'vikram.singh@example.com',    '+919788990011',
     '2004-05-25', 'MALE',   '56 Civil Lines, Kanpur, UP',       1, '2022-07-01', 55.50, 'INACTIVE',  NULL, '2022-07-01 10:00:00', '2024-11-20 16:00:00'),

    (8,  'MCA2024002','Deepa',    'Reddy',     'deepa.reddy@example.com',     '+919944332211',
     '2002-12-19', 'FEMALE', '9 Banjara Hills, Hyderabad, TS',  6, '2024-06-15', NULL,  'ACTIVE',    NULL, '2024-06-15 11:00:00', '2024-06-15 11:00:00'),

    (9,  'EC2023020', 'Nikhil',   'Joshi',     'nikhil.joshi@example.com',    '+919822113344',
     '2005-02-28', 'MALE',   '34 Aundh Road, Pune, MH',         2, '2023-07-12', 81.00, 'ACTIVE',    NULL, '2023-07-12 09:30:00', '2023-07-12 09:30:00'),

    (10, 'BBA2022003','Tanvi',    'Desai',     'tanvi.desai@example.com',     '+919711223355',
     '2004-08-11', 'FEMALE', '67 Satellite Road, Ahmedabad, GJ',4, '2022-07-05', 61.00, 'SUSPENDED', NULL, '2022-07-05 09:00:00', '2025-01-08 10:00:00');
