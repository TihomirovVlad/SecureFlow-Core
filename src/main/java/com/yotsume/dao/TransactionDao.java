package com.yotsume.dao;

import com.yotsume.config.HikariCPDataSource;
import com.yotsume.model.Transaction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TransactionDao {
    public void save(Transaction transaction) {
        String sql = "insert into transactions (from_user_id, to_user_id, amount) values (?, ?, ?)";
        try(Connection connection = HikariCPDataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setLong(1, transaction.getFromUserId());
            preparedStatement.setLong(2, transaction.getToUserId());
            preparedStatement.setBigDecimal(3, transaction.getAmount());
            int affected = preparedStatement.executeUpdate();
            if (affected == 0) {
                throw new RuntimeException("Error inserting transaction");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error inserting transaction", e);
        }
    }
}
