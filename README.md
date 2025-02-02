# LiquibaseClone Documentation

## Table of Contents
- [Introduction](#introduction)
- [Installation and Setup](#installation-and-setup)
- [Project Structure](#project-structure)
- [Usage](#usage)
- [Testing](#testing)
- [License](#license)

## Introduction
LiquibaseClone is a project that serves as a clone or adaptation of the popular database migration tool, Liquibase. The main goal of the project is to provide a simplified or extended version of Liquibase's functionality, potentially adding new features or optimizations.

### Key Features:
- Database migration management
- Support for multiple database management systems (DBMS)
- Ability to write tests to verify migration correctness
- Logging to `logs/application.log`

---

## Installation and Setup

### Prerequisites:
- Java (version 21.0.5)
- Gradle (version 8.12)
- Database (e.g., PostgreSQL, MySQL, etc.)

### Installation:
Clone the repository:
```bash
git clone https://github.com/HuNGuM/LiquibaseClone.git
cd LiquibaseClone
```

Build the project using Gradle:
```bash
gradle build
```

Configure the database connection in the configuration file (e.g., `application.properties` or `config.yml`).

---

## Project Structure
```
LiquibaseClone/
├── src/
│   ├── main/
│   │   ├── java/              # Source code
│   │   ├── resources/         # Resources (configurations, scripts, etc.)
│   └── test/
│       ├── java/              # Tests
│       └── resources/         # Test resources
├── build.gradle               # Gradle configuration file
├── README.md                  # Project documentation
├── logs/                      # Logs directory
│   ├── application.log        # Log file for all operations
└── .gitignore                 # Ignored files
```

---

## Usage
To apply migrations to the database, run the `main` method in the `MigrationMain` class.

- Enter `1` to apply migrations.
- Enter `2` to roll back to a specific date.

### Migration Rules:
- The `main/resources/migrations/` folder contains migration files. All migration files must start with `V`, followed by a version number, a meaningful name, and `.sql` at the end.
- Rollback files should be named as `R` + the version number of the migration to be rolled back + `.sql`.
- When we use rollback to a specific date, we should enter the date and time, programm will check the "migrations" table and cancel all the migrations that were applied after the date and time the user entered (by using rollback files we created)
- All the migrations are transactional (they're using "migration_lock" table to check whether any other migration is being applied at the moment)
- The `resources/sql-order.txt` file specifies the order in which migrations are applied.
- All logs are stored in `logs/application.log`.

### Migration files:
Currently, the src/main/resources/migrations/ folder contains the following migration files:

- V1__initial_schema.sql
Adds initial tables and fills them with data

- V2__add_constraints.sql
This file adds several constraints to the existing tables:

**Employees:**
Adds a unique index on the name field to ensure each employee has a unique name.

**Projects:**
Adds a NOT NULL constraint on the name field to ensure all projects have a name.

**Departments:**
Adds a check constraint on the name field to ensure the department name has at least one character.

**Employees:**
Adds an index on the department_id field to optimize searches by department.

**Employee_Projects:**
Adds foreign key constraints on the employee_id and project_id fields to ensure referential integrity and enable cascading deletes.
Adds indexes on the employee_id and project_id fields to optimize lookups by employee and project

---

## Testing
The `test/` directory contains unit tests for various methods.
Code coverage reports can be found in `build/jacocoHtml/index.html`. Some methods in the `MigrationManager` class are excluded from coverage (annotated with `@Generated`).
Docker engine running on the background is recommended

---

## License
This project is open-source.
