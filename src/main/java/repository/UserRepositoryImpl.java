package repository;

import model.User;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public UserRepositoryImpl(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final RowMapper<User> USER_ROW_MAPPER = (rs, rowNum) -> {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setLogin(rs.getString("login"));
        return user;
    };

    @Override
    public void createUser(String login) {
        if (login == null || login.isBlank()) {
            throw new IllegalArgumentException("login cannot be null or blank");
        }

        if (existsByLogin(login)) {
            throw new IllegalArgumentException("User with login '" + login + "' already exists");
        }

        String sql = "INSERT INTO users (login) VALUES (:login)";
        Map<String, Object> params = Map.of("login", login);
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(
                sql,
                new MapSqlParameterSource(params),
                keyHolder,
                new String[]{"id"}
        );
        Long id = keyHolder.getKey().longValue();
        User user = new User();
        user.setId(id);
        user.setLogin(login);
    }

    @Override
    public void updateUserLogin(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        if (user.getId() == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        if (user.getLogin() == null || user.getLogin().isBlank()) {
            throw new IllegalArgumentException("User login cannot be null or blank");
        }

        String sql = "UPDATE users SET login = :login WHERE id = :id";
        Map<String, Object> params = Map.of("id", user.getId(), "login", user.getLogin());
        jdbcTemplate.update(sql, params);
    }

    @Override
    public void deleteUser(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }

        String sql = "DELETE FROM users WHERE id = :id";
        Map<String, Object> params = Map.of("id", id);
        jdbcTemplate.update(sql, params);
    }

    @Override
    public Optional<User> findUserById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }

        String sql = "SELECT * FROM users WHERE id = :id";
        Map<String, Object> params = Map.of("id", id);
        try {
            User user = jdbcTemplate.queryForObject(sql, params, USER_ROW_MAPPER);
            return Optional.ofNullable(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<User> findAllUsers() {
        return jdbcTemplate.query("SELECT * FROM users ORDER BY id", USER_ROW_MAPPER);

    }

    @Override
    public boolean existsByLogin(String login) {
        if (login == null || login.isBlank()) {
            throw new IllegalArgumentException("User login cannot be null or blank");
        }

        String sql = "SELECT COUNT(*) FROM users WHERE login = :login";
        Map<String, Object> params = Map.of("login", login);
        Integer count = jdbcTemplate.queryForObject(sql, params, Integer.class);
        return count > 0;
    }
}
