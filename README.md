# Учебный проект Spring с голым JDBC

Этот проект демонстрирует использование чистого JDBC в Spring без применения ORM-фреймворков и дополнительных библиотек. Проект является **исключительно учебным** и намеренно содержит некоторые неоптимальные практики для демонстрационных целей.

## Что показывает этот проект

1. Как использовать чистый JDBC в Spring-приложении
2. Как создать слой репозитория без Spring Data и других ORM
3. Как реализовать инициализацию БД через Java-код с JDBC
4. Как организовать взаимодействие между слоями приложения

## Структура проекта

```
src/main/java/ru/mephi/test/
├── TestApplication.java             # Точка входа в приложение
├── model/                           # Модели данных
│   └── User.java                    # Модель пользователя
├── repository/                      # Репозитории
│   ├── UserRepository.java          # Интерфейс репозитория
│   └── JdbcUserRepository.java      # JDBC-реализация репозитория
├── service/                         # Сервисный слой
│   ├── UserService.java             # Интерфейс сервиса
│   └── UserServiceImpl.java         # Реализация сервиса
├── controller/                      # Контроллеры
│   └── UserController.java          # REST-контроллер
├── config/                          # Конфигурации
│   └── JdbcConfig.java              # Конфигурация JDBC
└── db/                              # Инициализация БД
    ├── TableInitializer.java        # Интерфейс инициализаторов таблиц
    ├── UsersTableInitializer.java   # Инициализатор таблицы users
    └── DatabaseInitializerRunner.java # Запускает инициализаторы
```

## Работа с базой данных через чистый JDBC

В проекте демонстрируется прямое использование JDBC API для взаимодействия с базой данных:

1. **Настройка соединения** в `JdbcConfig.java`:
   ```java
   @Bean
   public DataSource dataSource() {
       DriverManagerDataSource dataSource = new DriverManagerDataSource();
       dataSource.setDriverClassName("org.mariadb.jdbc.Driver");
       dataSource.setUrl("jdbc:mariadb://localhost:3306/userdb");
       dataSource.setUsername("root");
       dataSource.setPassword("root");
       return dataSource;
   }
   ```

2. **Репозиторий с JDBC** в `JdbcUserRepository.java`:
   ```java
   @Override
   public List<User> findAll() {
       List<User> users = new ArrayList<>();
       String sql = "SELECT id, username, email, age FROM users";

       try (Connection connection = dataSource.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql)) {

           while (resultSet.next()) {
               User user = mapRowToUser(resultSet);
               users.add(user);
           }
       } catch (SQLException e) {
           throw new AppException("Ошибка при получении всех пользователей", e);
       }

       return users;
   }
   ```

3. **Инициализация таблиц** в `UsersTableInitializer.java`:
   ```java
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
           statement.execute(sql);
       } catch (SQLException e) {
           throw new AppException("Не удалось создать таблицу users", e);
       }
   }
   ```

## Инициализация базы данных

В проекте реализован подход к инициализации БД с использованием интерфейса `TableInitializer`.

1. **Интерфейс инициализатора**:
   ```java
   public interface TableInitializer {
       void createTable();
       void populateTable();
       String getTableName();
       int getOrder();
       default void initialize() {
           createTable();
           populateTable();
       }
   }
   ```

2. **Запуск инициализаторов** через `DatabaseInitializerRunner`:
   ```java
   @Component
   public class DatabaseInitializerRunner implements CommandLineRunner {
       private final List<TableInitializer> tableInitializers;

       @Override
       public void run(String... args) {
           tableInitializers.sort(Comparator.comparingInt(TableInitializer::getOrder));
           tableInitializers.forEach(initializer -> {
               initializer.initialize();
           });
       }
   }
   ```

## Конфигурация приложения

Настройки в `application.properties`:

```properties
spring.application.name=test

spring.datasource.url=jdbc:mariadb://localhost:3306/userdb
spring.datasource.username=root
spring.datasource.password=root
spring.datasource.driver-class-name=org.mariadb.jdbc.Driver

spring.sql.init.mode=never
```

## REST API

Приложение предоставляет REST API для работы с пользователями:

- `GET /api/users` — получить всех пользователей
- `GET /api/users/{id}` — получить пользователя по ID
- `POST /api/users` — создать пользователя
- `PUT /api/users/{id}` — обновить пользователя
- `DELETE /api/users/{id}` — удалить пользователя

## Запуск приложения

1. Создайте базу данных MySQL/MariaDB с именем `userdb`
2. Запустите приложение:
   ```
   ./gradlew bootRun
   ```

## Учебные моменты и вопросы к рассмотрению

В данном проекте намеренно использованы подходы, которые могут вызывать вопросы:

1. Как выполняется управление соединениями с базой данных? Каждый вызов `dataSource.getConnection()` создаёт новое соединение или переиспользует существующие?

2. Что происходит с транзакциями при работе с несколькими SQL-запросами?

3. Как в этом проекте обрабатываются исключения при работе с базой данных?

4. Как осуществляется маппинг между ResultSet и объектами Java?

5. Какие проблемы могут возникнуть при масштабировании такого решения?

6. Где могут быть узкие места в производительности?

7. Как реализована инициализация базы данных и в чём особенности этого подхода?
