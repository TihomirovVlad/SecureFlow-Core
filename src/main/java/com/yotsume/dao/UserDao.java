package com.yotsume.dao;

import com.yotsume.config.HikariCPDataSource;
import com.yotsume.model.User;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDao {

    public User save(User user) {

        String sql = "insert into users (email, balance) values (?, ?)";

        try(Connection connection = HikariCPDataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, user.getEmail());
            statement.setBigDecimal(2, user.getBalance());

            int affectedRows = statement.executeUpdate();
            if(affectedRows == 0) {
                throw new RuntimeException("Creating user failed, no rows affected.");
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if(generatedKeys.next()) {
                    user.setId(generatedKeys.getLong(1));
                }else  {
                    throw new RuntimeException("Creating user failed, no ID obtained.");
                }
            }

            return user;

        } catch (SQLException e) {
            throw new RuntimeException("Error saving user", e);
        }
    }

    public Optional<User> findById(long id) {
        String sql = "select * from users where id = ?";
        try(Connection connection = HikariCPDataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);
            try(ResultSet resultSet = statement.executeQuery()) {
                if(resultSet.next()) {
                    return Optional.of(mapResultSetToUser(resultSet));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding user by id" + id, e);
        }
    }

    public List<User> findAll() {
        String sql = "select * from users";
        List<User> users = new ArrayList<>();

        try(Connection connection = HikariCPDataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ResultSet resultSet = preparedStatement.executeQuery()) {

            while(resultSet.next()) {
                users.add(mapResultSetToUser(resultSet));
            }
            return users;

        } catch (SQLException e) {
            throw new RuntimeException("Error finding users", e);
        }
    }

    public void update(User user) {
        String sql = "update users set email = ?, balance = ? where id = ?";
        try(Connection connection = HikariCPDataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, user.getEmail());
            preparedStatement.setBigDecimal(2, user.getBalance());
            preparedStatement.setLong(3, user.getId());

            int affectedRows = preparedStatement.executeUpdate();
            if(affectedRows == 0) {
                throw new RuntimeException("Updating user failed, no rows affected.");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error updating user with id = " + user.getId(), e);
        }
    }

    public void updateBalance(long id, BigDecimal balance) {
        String sql = "update users set balance = ? where id = ?";
        try(Connection connection = HikariCPDataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setBigDecimal(1, balance);
            preparedStatement.setLong(2, id);
            int affectedRows = preparedStatement.executeUpdate();
            if(affectedRows == 0) {
                throw new RuntimeException("Updating user failed, no rows affected.");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error updating user balance with id = " + id, e);
        }
    }

    public void delete(User user) {
        String sql = "delete from users where id = ?";
        try(Connection connection = HikariCPDataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, user.getId());
            int affectedRows = preparedStatement.executeUpdate();
            if(affectedRows == 0) {
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
