package com.yotsume.dao;

import com.yotsume.config.DatabaseConfig;
import com.yotsume.entity.User;
import com.yotsume.exeptions.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDao implements UserDaoInterface{

    private static final Logger LOGGER = LoggerFactory.getLogger(UserDao.class);

    public static final String SAVE_SQL = "insert into users (email, balance) values (?, ?)";
    public static final String FIND_BY_EMAIL = "select * from users where email = ?";
    public static final String FIND_BY_ID = "select * from users where id = ?";
    public static final String FIND_ALL = "select * from users";
    public static final String DELETE_SQL = "delete from users where id = ?";
    public static final String UPDATE_SQL = "update users set email = ?, balance = ? where id = ?";
    public static final String UPDATE_BALANCE = "update users set balance = balance + ? where id = ?";
    public static final String UPDATE_DOWN_BALANCE = "update users set balance = balance - ? where id = ?";

    private User getUserOrThrow(Long userId) {
        return findById(userId).orElseThrow(
                () -> new UserNotFoundException("User not found: ID = " + userId)
        );
    }

    @Override
    public User save(User user) {

        if (user.getEmail() == null || !user.getEmail().contains("@")) {
            throw new IllegalArgumentException("Email address is invalid " + user.getEmail());
        }

        try(var connection = DatabaseConfig.getConnection();
            var prepareStatement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)
        ) {
            prepareStatement.setString(1, user.getEmail());
            prepareStatement.setBigDecimal(2, user.getBalance());

            int affectedRows = prepareStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new UserCreationException("Failed to save user: no rows affected");
            }

            try(var resultSet = prepareStatement.getGeneratedKeys()) {
                if (resultSet.next()){
                    user.setId(resultSet.getLong(1));
                } else {
                    throw new UserCreationException("Failed to get generated ID");
                }
            }
            return user;

        } catch (SQLException e) {
            if (e.getSQLState().equals("23505")) {
                throw new EmailAlreadyExistsException("Email already exists " + user.getEmail());
            }
            throw new DatabaseException("Database error during user save", e);
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {
        try(var connection = DatabaseConfig.getConnection();
            var prepareStatement = connection.prepareStatement(FIND_BY_EMAIL)
        ) {
            prepareStatement.setString(1, email);
            try (var resultSet = prepareStatement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(ROW_MAPPER.mapRow(resultSet, 1));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new DatabaseException("Database error during findByEmail", e);
        }
    }

    @Override
    public Optional<User> findById(Long id) {
        try(var connection = DatabaseConfig.getConnection();
            var prepareStatement = connection.prepareStatement(FIND_BY_ID)
        ) {
            prepareStatement.setLong(1, id);
            try(var rs = prepareStatement.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(ROW_MAPPER.mapRow(rs, 1));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new DatabaseException("Database error during findById", e);
        }
    }

    @Override
    public List<User> findAll() {
        try(var connection = DatabaseConfig.getConnection();
            var prepareStatement = connection.prepareStatement(FIND_ALL);
            var rs = prepareStatement.executeQuery()
        ) {
            List<User> users = new ArrayList<>();
            int numRow = 1;
            while (rs.next()) {
                User user = ROW_MAPPER.mapRow(rs, numRow++);
                users.add(user);
            }
            return users;
        } catch (SQLException e) {
            throw new DatabaseException("Database error during findAll", e);
        }
    }

    @Override
    public void update(User user) {
        try(var connection = DatabaseConfig.getConnection();
        var prepareStatement = connection.prepareStatement(UPDATE_SQL)) {

            prepareStatement.setString(1, user.getEmail());
            prepareStatement.setBigDecimal(2, user.getBalance());
            prepareStatement.setLong(3, user.getId());

            int affectedRows = prepareStatement.executeUpdate();
            if (affectedRows == 0){
                throw new UserNotFoundException("Updating user failed, no rows affected.");
            }


        } catch (SQLException e) {
            throw new DatabaseException("Error updating user with id = " + user.getId(), e);
        }
    }

    @Override
    public void addBalance(Long userId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        getUserOrThrow(userId);

        try(var connection = DatabaseConfig.getConnection();
            var prepareStatement = connection.prepareStatement(UPDATE_BALANCE)) {

            prepareStatement.setBigDecimal(1, amount);
            prepareStatement.setLong(2, userId);

            int affectedRows = prepareStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new UserUpdateException("Failed to update balance for user ID: " + userId);
            }

        } catch (SQLException e) {
            throw new DatabaseException("Database error during addBalance", e);
        }
    }

    @Override
    public void downBalance(Long userId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        User user = getUserOrThrow(userId);

        if (user.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException(
                    "Insufficient balance for user ID: " + userId +
                            ", required: " + amount + ", available: " + user.getBalance()
            );
        }

        try(var connection = DatabaseConfig.getConnection();
            var prepareStatement = connection.prepareStatement(UPDATE_DOWN_BALANCE)) {

            prepareStatement.setBigDecimal(1, amount);
            prepareStatement.setLong(2, userId);

            int affectedRows = prepareStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new UserUpdateException("Failed to update balance for user ID: " + userId);
            }

        } catch (SQLException e) {
            throw new DatabaseException("Database error during downBalance", e);
        }
    }

    @Override
    public void delete(User user) {
        try(var connection = DatabaseConfig.getConnection();
        var prepareStatement = connection.prepareStatement(DELETE_SQL)) {

            prepareStatement.setLong(1, user.getId());
            int affectedRows = prepareStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new UserDeleteException("Deleting user failed, no rows affected.");
            }
        } catch (SQLException e) {
            throw new DatabaseException("Database error during deleting user", e);
        }
    }
}
