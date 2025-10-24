package ru.mephi.test.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.mephi.test.exceptions.AppException;

import javax.sql.DataSource;
import java.sql.*;

/**
 * Инициализатор таблицы пользователей.
 * Создает таблицу users и заполняет ее начальными данными.
 */
@Component
public class UsersTableInitializer implements TableInitializer {

    private static final Logger logger = LoggerFactory.getLogger(UsersTableInitializer.class);

    private final DataSource dataSource;

    @Autowired
    public UsersTableInitializer(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS users (" +
                "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                "username VARCHAR(100) NOT NULL, " +
                "email VARCHAR(255) NOT NULL, " +
                "age INT" +
                ")";

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {

            logger.info("Создание таблицы users");
            statement.execute(sql);
            logger.info("Таблица users успешно создана");

        } catch (SQLException e) {
            logger.error("Ошибка при создании таблицы users", e);
            throw new AppException("Не удалось создать таблицу users", e);
        }
    }

    @Override
    public void populateTable() {
        String clearSql = "DELETE FROM users";

        String insertSql = "INSERT INTO users (username, email, age) VALUES (?, ?, ?)";

        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);

            try (Statement statement = connection.createStatement();
                 PreparedStatement preparedStatement = connection.prepareStatement(insertSql)) {

                statement.execute(clearSql);

                logger.info("Заполнение таблицы users тестовыми данными");

                // Пользователь 1
                preparedStatement.setString(1, "user1");
                preparedStatement.setString(2, "user1@example.com");
                preparedStatement.setInt(3, 25);
                preparedStatement.addBatch();

                // Пользователь 2
                preparedStatement.setString(1, "user2");
                preparedStatement.setString(2, "user2@example.com");
                preparedStatement.setInt(3, 30);
                preparedStatement.addBatch();

                // Пользователь 3
                preparedStatement.setString(1, "user3");
                preparedStatement.setString(2, "user3@example.com");
                preparedStatement.setInt(3, 35);
                preparedStatement.addBatch();

                preparedStatement.executeBatch();

                connection.commit();
                logger.info("Таблица users успешно заполнена тестовыми данными");

            } catch (SQLException e) {
                connection.rollback();
                logger.error("Ошибка при заполнении таблицы users", e);
                throw new AppException("Не удалось заполнить таблицу users", e);
            } finally {
                connection.setAutoCommit(true);
            }

        } catch (SQLException e) {
            logger.error("Ошибка при работе с соединением", e);
            throw new AppException("Не удалось выполнить операции с таблицей users", e);
        }
    }

    @Override
    public String getTableName() {
        return "users";
    }

    @Override
    public int getOrder() {
        return 10;
    }
}