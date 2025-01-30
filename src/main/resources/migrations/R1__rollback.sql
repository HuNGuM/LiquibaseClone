
-- R1__rollback_initial_schema.sql

-- Удаление всех данных из таблицы "employee_projects"
DELETE FROM employee_projects;

-- Удаление всех данных из таблицы "employees"
DELETE FROM employees;

-- Удаление всех данных из таблицы "projects"
DELETE FROM projects;

-- Удаление всех данных из таблицы "departments"
DELETE FROM departments;

-- Удаление таблицы "employee_projects"
DROP TABLE IF EXISTS employee_projects;

-- Удаление таблицы "employees"
DROP TABLE IF EXISTS employees;

-- Удаление таблицы "projects"
DROP TABLE IF EXISTS projects;

-- Удаление таблицы "departments"
DROP TABLE IF EXISTS departments;
