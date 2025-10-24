package ru.mephi.test.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * Конфигурация для подключения к базе данных через JDBC.
 * В реальных приложениях эту конфигурацию можно опустить,
 * так как Spring Boot автоматически настраивает DataSource
 * на основе свойств в application.properties.
 */
@Configuration
public class JdbcConfig {

    /**
     * Создает и настраивает DataSource для подключения к базе данных.
     * Этот бин не требуется, если вы используете стандартную автоконфигурацию Spring Boot
     * с настройками в application.properties. Он добавлен здесь ТОЛЬКО как нглядный учебный пример.
     */
    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.mariadb.jdbc.Driver");
        dataSource.setUrl("jdbc:mariadb://localhost:3306/userdb");
        dataSource.setUsername("root");
        dataSource.setPassword("root");

        return dataSource;
    }
}
