package com.yotsume.service;

import com.yotsume.config.HikariCPDataSource;
import com.yotsume.dao.TransactionDao;
import com.yotsume.dao.UserDao;
import com.yotsume.exeption.InsufficientBalanceException;
import com.yotsume.exeption.UserNotFoundException;
import com.yotsume.model.Transaction;
import com.yotsume.model.User;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;

public class TransferService {
    private final UserDao userDao = new UserDao();
    private final TransactionDao  transactionDao = new TransactionDao();

    public void transferMoney(Long fromUserId, Long toUserId, BigDecimal amount) {
        if (fromUserId.equals(toUserId) && amount.compareTo(BigDecimal.ZERO) > 0) {
            throw new IllegalArgumentException("Amount must be positive or users must be different");
        }

        try(Connection connection = HikariCPDataSource.getConnection()) {
            connection.setAutoCommit(false);

            try {
                User fromUser = userDao.findById(fromUserId).orElseThrow(
                        () -> new UserNotFoundException("User with id " + fromUserId + " not found")
                );
                User toUser = userDao.findById(toUserId).orElseThrow(
                        () -> new UserNotFoundException("User with id " + toUserId + " not found")
                );

                userDao.findById(fromUserId)
                        .filter(user -> user.getBalance().compareTo(amount) >= 0)
                        .orElseThrow(() -> new InsufficientBalanceException("not enough money on balance"));

                userDao.updateBalance(fromUserId, fromUser.getBalance().subtract(amount));
                userDao.updateBalance(toUserId, toUser.getBalance().add(amount));

                Transaction tx = new Transaction();
                tx.setFromUserId(fromUserId);
                tx.setToUserId(toUserId);
                tx.setAmount(amount);
                transactionDao.save(tx);

                connection.commit();

            } catch (Exception e) {
                connection.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
