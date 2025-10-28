
package ru.mephi.test.repository;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.mephi.test.model.User;

import java.util.List;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class JdbcClientUserRepository implements UserRepository {
    private final JdbcClient jdbcClient;

    private final RowMapper<User> userRowMapper = (rs, rowNum) -> {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setUsername(rs.getString("username"));
        user.setEmail(rs.getString("email"));
        user.setAge(rs.getInt("age"));
        return user;
    };

    @Override
    public List<User> findAll() {
        String sql = "SELECT id, username, email, age FROM users";
        return jdbcClient.sql(sql)
                .query(userRowMapper)
                .list();
    }

    @Override
    public Optional<User> findById(Long id) {
        String sql = "SELECT id, username, email, age FROM users WHERE id = ?";
        return jdbcClient.sql(sql)
                .param(id)
                .query(userRowMapper)
                .optional();
    }

    @Override
    public User save(User user) {
        String sql = "INSERT INTO users (username, email, age) VALUES (?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcClient.sql(sql)
                .param(user.getUsername())
                .param(user.getEmail())
                .param(user.getAge())
                .update(keyHolder);

        user.setId(keyHolder.getKey().longValue());
        return user;
    }

    @Override
    public List<User> saveAll(List<User> users) {
        String sql = "INSERT INTO users (username, email, age) VALUES (?, ?, ?)";

        for (User user : users) {
            jdbcClient.sql(sql)
                    .param(user.getUsername())
                    .param(user.getEmail())
                    .param(user.getAge())
                    .update();
        }

        return users;
    }

    @Override
    public User update(User user) {
        String sql = "UPDATE users SET username = ?, email = ?, age = ? WHERE id = ?";
        jdbcClient.sql(sql)
                .param(user.getUsername())
                .param(user.getEmail())
                .param(user.getAge())
                .param(user.getId())
                .update();
        return user;
    }

    @Override
    public boolean deleteById(Long id) {
        String sql = "DELETE FROM users WHERE id = ?";
        return jdbcClient.sql(sql)
                .param(id)
                .update() > 0;
    }
}