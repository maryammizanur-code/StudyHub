
-- STUDYHUB DATABASE
-- Project: Multi-User Personal Study Planner
-- Purpose: Create the database and its three tables


-- SECTION 1: CREATE DATABASE

CREATE DATABASE IF NOT EXISTS studyhub;

USE studyhub;



-- SECTION 2: CREATE USERS TABLE
-- Stores registered user information

CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);



-- SECTION 3: CREATE TASKS TABLE
-- Stores assignments, exams, and projects

CREATE TABLE tasks (
    task_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    title VARCHAR(150) NOT NULL,
    description TEXT,
    task_type ENUM('Assignment', 'Exam', 'Project') NOT NULL,
    deadline DATE NOT NULL,
    status ENUM('Planned', 'In Progress', 'Completed')
        NOT NULL DEFAULT 'Planned',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_tasks_user
        FOREIGN KEY (user_id)
        REFERENCES users(user_id)
        ON DELETE CASCADE
);



-- SECTION 4: CREATE RESOURCES TABLE
-- Stores users' saved study links

CREATE TABLE resources (
    resource_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    title VARCHAR(150) NOT NULL,
    url VARCHAR(2048) NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_resources_user
        FOREIGN KEY (user_id)
        REFERENCES users(user_id)
        ON DELETE CASCADE
);



-- SECTION 5: INSERT TEST USERS
-- Sample accounts for database testing only
-- Password values are placeholders, not real login credentials.

INSERT INTO users (full_name, email, password_hash)
VALUES
    ('Test User One', 'user1@example.com', 'test_hash_001'),
    ('Test User Two', 'user2@example.com', 'test_hash_002');



-- SECTION 6: INSERT TEST TASKS
-- Adds sample tasks for each test user

INSERT INTO tasks (
    user_id,
    title,
    description,
    task_type,
    deadline,
    status
)
VALUES
(
    1,
    'Complete OODP-II Assignment',
    'Practice object-oriented programming',
    'Assignment',
    '2026-11-10',
    'Planned'
),
(
    2,
    'Prepare for Mathematics Exam',
    'Revise Greens theorem and related mathematics',
    'Exam',
    '2026-11-12',
    'In Progress'
);



-- SECTION 7: INSERT TEST RESOURCES
-- Adds sample study links

INSERT INTO resources (
    user_id,
    title,
    url,
    description
)
VALUES
(
    1,
    'Java Documentation',
    'https://docs.oracle.com/en/java/',
    'Official Java documentation for programming practice'
),
(
    2,
    'Mathematics Study Resource',
    'https://www.khanacademy.org/math',
    'Mathematics lessons and practice exercises'
);



-- END OF STUDYHUB SQL SCRIPT
-- Tables: users, tasks, resources

