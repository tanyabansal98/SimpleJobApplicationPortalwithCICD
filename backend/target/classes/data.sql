-- ============================================================================
-- Job Application Portal — Initial Seed Data
-- ============================================================================

-- 1. Default Admin (Password: 'admin123')
INSERT INTO USERS (USER_ID, EMAIL, PASSWORD_HASH, ROLE, IS_ACTIVE, CREATED_AT)
VALUES (1, 'admin@jobportal.com', '$2a$10$8.UnVuG9HHgffUDAlk8qnO7y4/7v3xM9U9y6U9y6U9y6U9y6U9y6U', 'ADMIN', 1, CURRENT_TIMESTAMP);

-- 2. Sample Employer (Password: 'employer123')
INSERT INTO USERS (USER_ID, EMAIL, PASSWORD_HASH, ROLE, IS_ACTIVE, CREATED_AT)
VALUES (2, 'employer@google.com', '$2a$10$8.UnVuG9HHgffUDAlk8qnO7y4/7v3xM9U9y6U9y6U9y6U9y6U9y6U', 'EMPLOYER', 1, CURRENT_TIMESTAMP);

INSERT INTO EMPLOYER_PROFILES (USER_ID, COMPANY_NAME, INDUSTRY, WEBSITE, DESCRIPTION, LOCATION)
VALUES (2, 'Google', 'Technology', 'https://google.com', 'Search engine and cloud computing leader.', 'Mountain View, CA');

-- 3. Sample Student (Password: 'student123')
INSERT INTO USERS (USER_ID, EMAIL, PASSWORD_HASH, ROLE, IS_ACTIVE, CREATED_AT)
VALUES (3, 'student@northeastern.edu', '$2a$10$8.UnVuG9HHgffUDAlk8qnO7y4/7v3xM9U9y6U9y6U9y6U9y6U9y6U', 'STUDENT', 1, CURRENT_TIMESTAMP);

INSERT INTO STUDENT_PROFILES (USER_ID, FIRST_NAME, LAST_NAME, UNIVERSITY, MAJOR, GRADUATION_YEAR)
VALUES (3, 'John', 'Doe', 'Northeastern University', 'Computer Science', 2025);

-- 4. Sample Job
INSERT INTO JOBS (JOB_ID, EMPLOYER_USER_ID, TITLE, DESCRIPTION, LOCATION, SALARY_RANGE, JOB_TYPE, IS_ACTIVE, CREATED_AT)
VALUES (100, 2, 'Software Engineer Intern', 'Join our team as a summer intern working on core search features.', 'Boston, MA', '$40 - $50 / hr', 'Internship', 1, CURRENT_TIMESTAMP);

COMMIT;
