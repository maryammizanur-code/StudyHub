

--STUDYHUB DATABASE
--Project: Multi-User Personal Study Planner
--Purpose: Create the database and its four tables


--SECTION 1: CREATE DATABASE


CREATE DATABASE studyhub;

USE studyhub;



--SECTION 2: CREATE USERS TABLE
--Stores registered user information


CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


--SECTION 3: CREATE TASKS TABLE
--Stores assignments, exams, and projects


CREATE TABLE tasks (
    task_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    title VARCHAR(150) NOT NULL,
    description TEXT,
    task_type ENUM('Assignment', 'Exam', 'Project') NOT NULL,
    deadline DATETIME NOT NULL,
    estimated_minutes INT NOT NULL DEFAULT 60,
    status ENUM('Planned', 'In Progress', 'Completed')
        NOT NULL DEFAULT 'Planned',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_tasks_user
        FOREIGN KEY (user_id)
        REFERENCES users(user_id)
        ON DELETE CASCADE,

    CONSTRAINT chk_estimated_minutes
        CHECK (estimated_minutes > 0)
);



--SECTION 4: CREATE STUDY_SESSIONS TABLE
--Stores completed focus-timer study sessions

CREATE TABLE study_sessions (
    session_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    subject VARCHAR(100) NOT NULL,
    duration_minutes INT NOT NULL,
    started_at DATETIME NOT NULL,
    completed_at DATETIME NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_sessions_user
        FOREIGN KEY (user_id)
        REFERENCES users(user_id)
        ON DELETE CASCADE,

    CONSTRAINT chk_session_duration
        CHECK (duration_minutes > 0),

    CONSTRAINT chk_session_times
        CHECK (completed_at >= started_at)
);



--SECTION 5: CREATE RESOURCES TABLE
--Stores users' saved study links

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



--SECTION 6: INSERT TEST USERS
--Sample accounts for database testing only


INSERT INTO users (full_name, email, password_hash)
VALUES
    ('Test User One', 'user1@example.com', 'test_001'),
    ('Test User Two', 'user2@example.com', 'test_002');



--SECTION 7: INSERT TEST TASKS
--Adds one sample task for each test user


INSERT INTO tasks (
    user_id,
    title,
    description,
    task_type,
    deadline,
    estimated_minutes,
    status
)
VALUES
(
    1,
    'Complete OODP-II Assignment',
    'Practice object-oriented programming',
    'Assignment',
    '2026-10-25 23:59:00',
    120,
    'Planned'
),
(
    2,
    'Prepare for Mathematics Exam',
    'Revise Greens theorem and its related maths',
    'Exam',
    '2026-10-27 09:00:00',
    180,
    'In Progress'
);



--SECTION 8: INSERT TEST STUDY SESSIONS
--Adds sample focus-timer records

INSERT INTO study_sessions (
    user_id,
    subject,
    duration_minutes,
    started_at,
    completed_at
)
VALUES
(
    1,
    'Object-Oriented Programming',
    25,
    '2026-10-21 20:00:00',
    '2026-10-21 20:25:00'
),
(
    2,
    'Mathematics',
    45,
    '2026-10-21 21:00:00',
    '2026-10-21 21:45:00'
);



--SECTION 9: INSERT TEST RESOURCES
--Adds sample study links


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



--END OF STUDYHUB SQL SCRIPT
--Tables: users, tasks, study_sessions, resources

