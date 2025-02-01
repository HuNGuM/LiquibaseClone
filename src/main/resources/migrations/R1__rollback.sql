-- R1__rollback_initial_schema.sql

-- Delete all data from the "employee_projects" table
DELETE FROM employee_projects;

-- Delete all data from the "employees" table
DELETE FROM employees;

-- Delete all data from the "projects" table
DELETE FROM projects;

-- Delete all data from the "departments" table
DELETE FROM departments;

-- Drop the "employee_projects" table
DROP TABLE IF EXISTS employee_projects;

-- Drop the "employees" table
DROP TABLE IF EXISTS employees;

-- Drop the "projects" table
DROP TABLE IF EXISTS projects;

-- Drop the "departments" table
DROP TABLE IF EXISTS departments;
