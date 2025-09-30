package com.yotsume.dao;

import com.yotsume.config.DatabaseConfig;
import com.yotsume.entity.Transaction;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class TransactionDao implements TransactionDaoInterface{

    public static final String SAVE_TRANSACTION = "INSERT INTO transactions (from_user_id, to_user_id, amount, type, created_at) VALUES (?, ?, ?, ?, ?)";
    public static final String FIND_TRANSACTION_BY_USER_ID =
            "SELECT id, type, from_user_id, to_user_id, amount, created_at " +
            "FROM transactions WHERE from_user_id = ? OR to_user_id = ? " +
            "ORDER BY created_at DESC";

    @Override
    public Transaction save(Transaction transaction) {
        try(var connection = DatabaseConfig.getConnection();
        var prepareStatement = connection.prepareStatement(SAVE_TRANSACTION, Statement.RETURN_GENERATED_KEYS)) {

            prepareStatement.setString(1, transaction.getType().name());
            prepareStatement.setLong(2, transaction.getFromUserId());
            prepareStatement.setLong(3, transaction.getToUserId());
            prepareStatement.setBigDecimal(4, transaction.getAmount());

            int rows = prepareStatement.executeUpdate();
            if (rows == 0) {
                throw new RuntimeException("Failed to save transaction, no rows affected.");
            }
            try(var rs = prepareStatement.getGeneratedKeys()) {
                if (rs.next()) {
                    transaction.setId(rs.getLong(1));
                } else {
                    throw new RuntimeException("Failed to get generated ID for transaction");
                }
            }

            return transaction;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to save transaction", e);
        }
    }

    @Override
    public List<Transaction> findByUserId(Long userId){
        try(var connection = DatabaseConfig.getConnection();
        var prepareStatement = connection.prepareStatement(FIND_TRANSACTION_BY_USER_ID)) {

            prepareStatement.setLong(1, userId);
            prepareStatement.setLong(2, userId);
            try (ResultSet rs = prepareStatement.executeQuery()) {
                List<Transaction> transactions = new ArrayList<>();
                int rowNum = 1;
                while (rs.next()) {
                    transactions.add(TRANSACTION_ROW_MAPPER.mapRow(rs, rowNum++));
                }

                return transactions;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to find transactions for user ID: " + userId, e);
        }
    }
}
