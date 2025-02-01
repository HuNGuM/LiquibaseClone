-- V2__add_constraints.sql

-- Adding a unique index on the "name" field in the "employees" table to ensure that each employee has a unique name
CREATE UNIQUE INDEX idx_employees_name ON employees (name);

-- Adding a NOT NULL constraint on the "name" field in the "projects" table
ALTER TABLE projects
    ALTER COLUMN name SET NOT NULL;

-- Adding a check constraint on the "name" field in the "departments" table
ALTER TABLE departments
    ADD CONSTRAINT chk_departments_name_length CHECK (char_length(name) >= 1);

-- Adding an index on the "department_id" field in the "employees" table to optimize searches by department
CREATE INDEX idx_employees_department_id ON employees (department_id);

-- Adding a foreign key constraint on the "employee_id" and "project_id" fields in the "employee_projects" table
ALTER TABLE employee_projects
    ADD CONSTRAINT fk_employee FOREIGN KEY (employee_id) REFERENCES employees (id) ON DELETE CASCADE,
    ADD CONSTRAINT fk_project FOREIGN KEY (project_id) REFERENCES projects (id) ON DELETE CASCADE;

-- Adding an index on the "employee_id" field in the "employee_projects" table to optimize employee-based lookups
CREATE INDEX idx_employee_projects_employee ON employee_projects (employee_id);

-- Adding an index on the "project_id" field in the "employee_projects" table to optimize project-based lookups
CREATE INDEX idx_employee_projects_project ON employee_projects (project_id);

