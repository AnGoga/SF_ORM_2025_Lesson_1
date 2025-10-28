package ru.mephi.test.repository;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.mephi.test.exceptions.AppException;
import ru.mephi.test.model.User;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Repository
@AllArgsConstructor
public class JdbcCopyUserRepository implements UserRepository {
    private final DataSource dataSource;


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

    @Override
    public Optional<User> findById(Long id) {
        String sql = "SELECT id, username, email, age FROM users WHERE id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet resultSet = ps.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapRowToUser(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new AppException("Ошибка при поиске пользователя с id: " + id, e);
        }

        return Optional.empty();
    }

    @Override
    public User save(User user) {
        String sql = "INSERT INTO users (username, email, age) VALUES (?, ?, ?)";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, user.getUsername());
            ps.setString(2, user.getEmail());
            ps.setObject(3, user.getAge(), Types.INTEGER);

            int affectedRows = ps.executeUpdate();

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Не удалось создать пользователя, ID не получен");
                }
            }
        } catch (SQLException e) {
            throw new AppException("Ошибка при сохранении пользователя", e);
        }

        return user;
    }

    @Override
    public List<User> saveAll(List<User> users) {
        String sql = "INSERT INTO users (username, email, age) VALUES (?, ?, ?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            connection.setAutoCommit(false);

            try {
                for (User user : users) {
                    ps.setString(1, user.getUsername());
                    ps.setString(2, user.getEmail());
                    if (user.getAge() != null) {
                        ps.setInt(3, user.getAge());
                    } else {
                        ps.setNull(3, Types.INTEGER);
                    }
                    ps.addBatch();
                }
                int[] affectedRows = ps.executeBatch();

                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    int index = 0;
                    // Порядок ID в ResultSet соответствует порядку операций в batch
                    while (generatedKeys.next()) {
                        long generatedId = generatedKeys.getLong(1);
                        users.get(index).setId(generatedId);
                        index++;
                    }
                }
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new AppException("Ошибка при сохранении списка пользователей", e);
        }
        return users;
    }

    @Override
    public User update(User user) {
        String sql = "UPDATE users SET username = ?, email = ?, age = ? WHERE id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, user.getUsername());
            ps.setString(2, user.getEmail());
            ps.setObject(3, user.getAge(), Types.INTEGER);
            ps.setLong(4, user.getId());

            int rowsAffected = ps.executeUpdate();
        } catch (SQLException e) {
            throw new AppException("Ошибка при обновлении пользователя", e);
        }

        return user;
    }

    @Override
    public boolean deleteById(Long id) {
        String sql = "DELETE FROM users WHERE id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, id);
            int rowsAffected = ps.executeUpdate();

            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new AppException("Ошибка при удалении пользователя с id: " + id, e);
        }
    }


    private User mapRowToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setUsername(rs.getString("username"));
        user.setEmail(rs.getString("email"));
        user.setAge(rs.getInt("age"));

        return user;
    }
}