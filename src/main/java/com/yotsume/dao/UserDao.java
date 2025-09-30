package com.yotsume.dao;

import com.yotsume.config.DatabaseConfig;
import com.yotsume.entity.User;
import com.zaxxer.hikari.HikariConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserDao.class);

    public static final String SAVE_SQL = "insert into users (email, balance) values (?, ?)";
    public static final String FIND_BY_EMAIL = "select * from users where email = ?";
    public static final String FIND_BY_ID = "select * from users where id = ?";
    public static final String FIND_ALL = "select * from users";
    public static final String DELETE_SQL = "delete from users where id = ?";


    public User save(User user) {
        try(var connection = DatabaseConfig.getConnection();
            var prepareStatement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)
        ) {

            if (user.getEmail() == null || !user.getEmail().contains("@")) {
                throw new IllegalArgumentException("Email address is invalid");
            }

            prepareStatement.setString(1, user.getEmail());
            prepareStatement.setBigDecimal(2, user.getBalance());

            int affectedRows = prepareStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new IllegalArgumentException("todo exception");
            }

            try(var resultSet = prepareStatement.getGeneratedKeys()) {
                if (resultSet.next()){
                    user.setId(resultSet.getLong(1));
                } else {
                    throw new RuntimeException("todo exception");
                }
            }
            return user;

        } catch (SQLException e) {
            if (e.getSQLState().equals("23505")) {
                throw new RuntimeException("Email already exists " + user.getEmail());
            }
            throw new RuntimeException("Database error during user save ", e);
        }
    }

    public Optional<User> findByEmail(String email) {
        try(var connection = DatabaseConfig.getConnection();
            var prepareStatement = connection.prepareStatement(FIND_BY_EMAIL)
        ) {
            prepareStatement.setString(1, email);
            try (var resultSet = prepareStatement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapResultSetToUser(resultSet));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<User> findById(Long id) {
        try(var connection = DatabaseConfig.getConnection();
            var prepareStatement = connection.prepareStatement(FIND_BY_ID)
        ) {
            prepareStatement.setLong(1, id);
            try(var rs = prepareStatement.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<User> findAll() {
        try(var connection = DatabaseConfig.getConnection();
            var prepareStatement = connection.prepareStatement(FIND_ALL);
            var rs = prepareStatement.executeQuery()
        ) {
            List<User> users = new ArrayList<>();
            while (rs.next()) {
                User user = mapResultSetToUser(rs);
                users.add(user);
            }
            return users;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(User user) {
        try(var connection = DatabaseConfig.getConnection();
        var prepareStatement = connection.prepareStatement(DELETE_SQL)) {

            prepareStatement.setLong(1, user.getId());
            int affectedRows = prepareStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new RuntimeException("Deleting user failed, no rows affected.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting user with id = " + user.getId(), e);
        }
    }

    private User mapResultSetToUser(ResultSet resultSet) throws SQLException {
        User user = new User();
        user.setId(resultSet.getLong("id"));
        user.setEmail(resultSet.getString("email"));
        user.setBalance(resultSet.getBigDecimal("balance"));
        return user;
    }
}
