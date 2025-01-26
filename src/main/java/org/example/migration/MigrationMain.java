package org.example.migration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Scanner;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class MigrationMain {

    private static final Logger logger = LoggerFactory.getLogger(MigrationMain.class); // Создание логгера

    public static void main(String[] args) {

        logger.info("Запуск программы управления миграциями...");
        try (Scanner scanner = new Scanner(System.in)) {
            MigrationManager manager = new MigrationManager();

            logger.info("Выберите действие:");
            logger.info("1: Выполнить миграции");
            logger.info("2: Откатить до определённой даты");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1 -> {
                    logger.info("Запуск миграций...");
                    manager.runMigrations();
                    logger.info("Миграции успешно завершены.");
                }
                case 2 -> {
                    logger.info("Введите дату отката в формате 'YYYY-MM-DD HH:MM:SS':");
                    scanner.nextLine(); // Пропуск символа новой строки
                    String dateString = scanner.nextLine();

                    try {
                        // Преобразование строки в LocalDateTime
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        LocalDateTime rollbackDate = LocalDateTime.parse(dateString, formatter);

                        logger.info("Попытка откатить миграции до {}", rollbackDate);
                        manager.rollbackToDate(rollbackDate);
                        logger.info("Откат миграций выполнен до {}", rollbackDate);
                    } catch (DateTimeParseException e) {
                        logger.error("Неверный формат даты: {}", dateString);
                    }
                }
                default -> logger.warn("Некорректный выбор. Завершение работы.");
            }
        } catch (SQLException e) {
            logger.error("Ошибка при работе с базой данных: {}", e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Произошла ошибка: {}", e.getMessage(), e);
        }
    }
}
