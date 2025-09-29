package com.yotsume.dao;

import com.yotsume.config.DatabaseConfig;
import com.yotsume.entity.User;
import com.zaxxer.hikari.HikariConfig;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDao {
    public static final String SAVE_SQL = "insert into users (email, balance) values (?, ?)";
    public static final String FIND_BY_EMAIL = "select * from users where email = ?";
    public static final String FIND_BY_ID = "select * from users where id = ?";
    public static final String FIND_ALL = "select * from users";


    public User save(User user) {
        try(var connection = DatabaseConfig.getConnection();
            var prepareStatement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)
        ) {

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
            throw new RuntimeException(e);
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



    private User mapResultSetToUser(ResultSet resultSet) throws SQLException {
        User user = new User();
        user.setId(resultSet.getLong("id"));
        user.setEmail(resultSet.getString("email"));
        user.setBalance(resultSet.getBigDecimal("balance"));
        return user;
    }
}
