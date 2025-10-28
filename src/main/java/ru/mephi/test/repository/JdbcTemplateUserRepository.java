package ru.mephi.test.repository;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.*;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.mephi.test.model.User;

import javax.sql.RowSet;
import java.sql.*;
import java.util.*;

@Repository
@AllArgsConstructor
@Primary
public class JdbcTemplateUserRepository implements UserRepository {
    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedJdbc;
    @Lazy private final JdbcTemplateUserRepository repository;

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

        return jdbcTemplate.query(sql, userRowMapper);
    }

    @Override
    public Optional<User> findById(Long id) {
        String sql = "SELECT id, username, email, age FROM users WHERE id = ?";
        return jdbcTemplate.query(sql, userRowMapper, id)
                .stream()
                .findFirst();
    }

    @Override
    public User save(User user) {
        String sql = "INSERT INTO users (username, email, age) VALUES (?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getEmail());
            ps.setObject(3, user.getAge(), Types.INTEGER);
            return ps;
        }, keyHolder);

        user.setId(keyHolder.getKey().longValue());

        return user;
    }

    @Override
    public List<User> saveAll(List<User> users) {
        String sql = "INSERT INTO users (username, email, age) VALUES (?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, users, users.size(), (ps, user) -> {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getEmail());
            ps.setObject(3, user.getAge(), Types.INTEGER);
        });

        return users;
    }

    @Override
    public User update(User user) {
        String sql = "UPDATE users SET username = ?, email = ?, age = ? WHERE id = ?";
        jdbcTemplate.update(sql, user.getUsername(), user.getEmail(), user.getAge(), user.getId());
        return user;
    }


    public User namedUpdate(User user) {
        String sql = "UPDATE users SET username = :name, email = :email, age = :age WHERE id = :id";

        Map<String, Object> userMap = new HashMap<>();
        userMap.put("name", user.getUsername());
        userMap.put("email", user.getEmail());
        userMap.put("id", user.getId());
        userMap.put("age", user.getAge());

        namedJdbc.update(sql, userMap);

        return user;
    }

    public User namedUpdate2(User user) {
        String sql = "UPDATE users SET username = :name, email = :email, age = :age WHERE id = :id";

        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("name", user.getUsername())
                .addValue("email", user.getEmail())
                .addValue("name", user.getUsername())
                .addValue("name", user.getUsername());

        namedJdbc.update(sql, params);

        return user;
    }

    @Override
    @Transactional
    public boolean deleteById(Long id) {
        String sql = "DELETE FROM users WHERE id = ?";
        return jdbcTemplate.update(sql, id) > 0;
    }
}
