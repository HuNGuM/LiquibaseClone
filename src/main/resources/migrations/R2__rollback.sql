-- R2__rollback_initial_schema.sql

-- Drop the unique index on the "name" field in the "employees" table
DROP INDEX IF EXISTS idx_employees_name;

-- Remove the NOT NULL constraint from the "name" field in the "projects" table
ALTER TABLE projects
    ALTER COLUMN name DROP NOT NULL;

-- Drop the name length constraint in the "departments" table
ALTER TABLE departments
    DROP CONSTRAINT IF EXISTS chk_departments_name_length;

-- Drop the index on the "department_id" field in the "employees" table
DROP INDEX IF EXISTS idx_employees_department_id;

-- Drop foreign key constraints from the "employee_projects" table
ALTER TABLE employee_projects
    DROP CONSTRAINT IF EXISTS fk_employee,
    DROP CONSTRAINT IF EXISTS fk_project;

-- Drop the index on the "employee_id" field in the "employee_projects" table
DROP INDEX IF EXISTS idx_employee_projects_employee;

-- Drop the index on the "project_id" field in the "employee_projects" table
DROP INDEX IF EXISTS idx_employee_projects_project;
