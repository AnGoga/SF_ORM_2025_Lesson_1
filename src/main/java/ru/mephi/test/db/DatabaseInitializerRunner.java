package ru.mephi.test.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

/**
 * Раннер для запуска инициализаторов таблиц при старте приложения.
 * Собирает все бины, реализующие интерфейс TableInitializer, и выполняет их в порядке приоритета.
 */
@Component
public class DatabaseInitializerRunner implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseInitializerRunner.class);

    private final List<TableInitializer> tableInitializers;

    @Autowired
    public DatabaseInitializerRunner(List<TableInitializer> tableInitializers) {
        this.tableInitializers = tableInitializers;
    }

    @Override
    public void run(String... args) {
        logger.info("Начало инициализации базы данных");

        tableInitializers.sort(Comparator.comparingInt(TableInitializer::getOrder));

        tableInitializers.forEach(initializer -> {
            logger.info("Инициализация таблицы: {}", initializer.getTableName());
            try {
                initializer.initialize();
                logger.info("Таблица {} успешно инициализирована", initializer.getTableName());
            } catch (Exception e) {
                logger.error("Ошибка при инициализации таблицы {}: {}",
                        initializer.getTableName(), e.getMessage(), e);
            }
        });

        logger.info("Инициализация базы данных успешно завершена");
    }
}