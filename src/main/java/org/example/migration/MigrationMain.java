package org.example.migration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

public class MigrationMain {

    private static final Logger logger = LoggerFactory.getLogger(MigrationMain.class); // Создание логгера

    public static void main(String[] args) {

        logger.info("Запуск миграции...");
        try {
            MigrationManager manager = new MigrationManager();
            manager.runMigrations();
            logger.info("Миграция успешно завершена.");
        } catch (SQLException e) {
            logger.error("Ошибка при работе с базой данных: {}", e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Произошла ошибка: {}", e.getMessage(), e);
        }
    }
}

