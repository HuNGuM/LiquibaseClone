-- V2__add_constraints.sql

-- Добавление уникального индекса на поле "name" в таблице "employees", чтобы гарантировать уникальность имени сотрудника
CREATE UNIQUE INDEX idx_employees_name ON employees(name);

-- Добавление не NULL ограничения на поле "name" в таблице "projects"
ALTER TABLE projects ALTER COLUMN name SET NOT NULL;

-- Добавление ограничения на длину имени в таблице "departments"
ALTER TABLE departments ADD CONSTRAINT chk_departments_name_length CHECK (char_length(name) >= 1);

-- Добавление индекса на поле "department_id" в таблице "employees" для ускорения поиска сотрудников по отделу
CREATE INDEX idx_employees_department_id ON employees(department_id);

-- Добавление ограничения на поля "employee_id" и "project_id" в таблице "employee_projects" для обеспечения целостности данных
ALTER TABLE employee_projects
    ADD CONSTRAINT fk_employee FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE CASCADE,
    ADD CONSTRAINT fk_project FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE;

-- Добавление ограничения на уникальность в таблице "employee_projects", чтобы избежать дублирования записей
CREATE UNIQUE INDEX idx_employee_projects ON employee_projects(employee_id, project_id);
