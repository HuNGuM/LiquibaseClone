-- V1__initial_schema.sql

-- Создание таблицы "departments"
CREATE TABLE departments (
                             id SERIAL PRIMARY KEY,
                             name VARCHAR(100) NOT NULL
);

-- Создание таблицы "employees"
CREATE TABLE employees (
                           id SERIAL PRIMARY KEY,
                           name VARCHAR(100) NOT NULL,
                           department_id INT REFERENCES departments(id)
);

-- Создание таблицы "projects"
CREATE TABLE projects (
                          id SERIAL PRIMARY KEY,
                          name VARCHAR(100) NOT NULL
);

-- Создание таблицы "employee_projects" (Many-to-Many связь между сотрудниками и проектами)
CREATE TABLE employee_projects (
                                   employee_id INT REFERENCES employees(id),
                                   project_id INT REFERENCES projects(id),
                                   PRIMARY KEY (employee_id, project_id)
);

-- Вставка начальных данных
-- Вставка начальных данных в таблицу "departments"
INSERT INTO departments (name)
VALUES
    ('HR'),
    ('Engineering'),
    ('Sales'),
    ('Marketing'),
    ('IT Support'),
    ('Customer Service'),
    ('Logistics'),
    ('Finance'),
    ('Legal'),
    ('Research and Development');
-- Вставка сотрудников
INSERT INTO employees (name, department_id)
VALUES
    ('Alice', 1),
    ('Bob', 2),
    ('Charlie', 3),
    ('Diana', 4),
    ('Eve', 5),
    ('Frank', 6),
    ('Grace', 7),
    ('Heidi', 8),
    ('Ivan', 9),
    ('Judy', 10),
    ('Kyle', 1),
    ('Laura', 2),
    ('Mike', 3),
    ('Nina', 4),
    ('Oscar', 5),
    ('Paul', 6),
    ('Quinn', 7),
    ('Rita', 8),
    ('Steve', 9),
    ('Tina', 10),
    ('Uma', 1),
    ('Victor', 2),
    ('Wendy', 3),
    ('Xander', 4),
    ('Yasmine', 5),
    ('Zane', 6),
    ('Aaron', 7),
    ('Bella', 8),
    ('Carl', 9),
    ('Dana', 10),
    ('Elliot', 1),
    ('Fiona', 2),
    ('Gabe', 3),
    ('Holly', 4),
    ('Ian', 5),
    ('Jack', 6),
    ('Kara', 7),
    ('Liam', 8),
    ('Mona', 9),
    ('Nolan', 10),
    ('Olivia', 1),
    ('Peter', 2),
    ('Quincy', 3),
    ('Rachel', 4),
    ('Sam', 5),
    ('Tara', 6),
    ('Ursula', 7),
    ('Vince', 8),
    ('Willow', 9),
    ('Xena', 10),
    ('Yuri', 1),
    ('Zoey', 2),
    ('Adrian', 3),
    ('Blair', 4),
    ('Cody', 5),
    ('Daphne', 6),
    ('Ethan', 7),
    ('Farrah', 8),
    ('Gordon', 9),
    ('Harper', 10),
    ('Isla', 1),
    ('Jonas', 2),
    ('Kelsey', 3),
    ('Logan', 4),
    ('Maya', 5),
    ('Nathan', 6),
    ('Olga', 7),
    ('Penny', 8),
    ('Quentin', 9),
    ('Rebecca', 10),
    ('Sophie', 1),
    ('Thomas', 2),
    ('Ulysses', 3),
    ('Valerie', 4),
    ('Warren', 5),
    ('Ximena', 6),
    ('Yara', 7),
    ('Zack', 8),
    ('Amber', 9),
    ('Brad', 10),
    ('Carmen', 1),
    ('Derek', 2),
    ('Ella', 3),
    ('Floyd', 4),
    ('Gwen', 5),
    ('Hank', 6),
    ('Ivy', 7),
    ('Jasper', 8),
    ('Kaitlyn', 9),
    ('Landon', 10),
    ('Wowa', 1),  -- Новый работник
    ('Jan', 2), -- Новый работник
    ('Misha', 3),  -- Новый работник
    ('Vanya', 4), -- Новый работник
    ('Arsenij', 5),  -- Новый работник
    ('Zhenia', 6), -- Новый работник
    ('Vita', 7),  -- Новый работник
    ('Iosif', 8),   -- Новый работник
    ('Katya', 9), -- Новый работник
    ('Petrosyan', 10);
-- Вставка начальных данных в таблицу "projects"
INSERT INTO projects (name)
VALUES
    ('Project Alpha'),
    ('Project Beta'),
    ('Project Gamma'),
    ('Project Delta'),
    ('Project Epsilon'),
    ('Project Zeta'),
    ('Project Eta'),
    ('Project Theta'),
    ('Project Iota'),
    ('Project Kappa'),
    ('Project Lambda'),
    ('Project Mu'),
    ('Project Nu'),
    ('Project Xi'),
    ('Project Omicron'),
    ('Project Pi'),
    ('Project Rho'),
    ('Project Sigma'),
    ('Project Tau'),
    ('Project Upsilon'),
    ('Project Phi'),
    ('Project Chi'),
    ('Project Psi'),
    ('Project Omega'),
    ('Project Apollo'),
    ('Project Hermes'),
    ('Project Artemis'),
    ('Project Demeter'),
    ('Project Hephaestus'),
    ('Project Poseidon');


-- Назначение каждому сотруднику уникального проекта
INSERT INTO employee_projects (employee_id, project_id)
SELECT e.id AS employee_id, p.id AS project_id
FROM employees e
         JOIN projects p
              ON e.id % 30 + 1 = p.id;

