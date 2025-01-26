-- R2__rollback_initial_schema.sql

-- Удаление уникального индекса на поле "name" в таблице "employees"
DROP INDEX IF EXISTS idx_employees_name;

-- Снятие NOT NULL ограничения с поля "name" в таблице "projects"
ALTER TABLE projects ALTER COLUMN name DROP NOT NULL;

-- Удаление ограничения на длину имени в таблице "departments"
ALTER TABLE departments DROP CONSTRAINT IF EXISTS chk_departments_name_length;

-- Удаление индекса на поле "department_id" в таблице "employees"
DROP INDEX IF EXISTS idx_employees_department_id;

-- Удаление ограничений внешнего ключа из таблицы "employee_projects"
ALTER TABLE employee_projects
    DROP CONSTRAINT IF EXISTS fk_employee,
    DROP CONSTRAINT IF EXISTS fk_project;

-- Удаление уникального индекса на поля "employee_id" и "project_id" в таблице "employee_projects"
DROP INDEX IF EXISTS idx_employee_projects;