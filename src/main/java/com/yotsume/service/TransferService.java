package com.yotsume.service;

import com.yotsume.config.DatabaseConfig;
import com.yotsume.dao.TransactionDao;
import com.yotsume.dao.UserDao;
import com.yotsume.entity.Transaction;
import com.yotsume.entity.User;
import com.yotsume.enums.TransactionType;
import com.yotsume.exeptions.InsufficientBalanceException;
import com.yotsume.exeptions.TransferException;
import com.yotsume.exeptions.UserNotFoundException;

import java.math.BigDecimal;
import java.sql.SQLException;

public class TransferService {

    private final UserDao userDao = new UserDao();
    private final TransactionDao transactionDao = new TransactionDao();

    public void transferMoney(Long fromUserId, Long toUserId, BigDecimal amount) {
        if (fromUserId == null || toUserId == null || amount == null) {
            throw new IllegalArgumentException("IDs and amount must not be null");
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        User userFrom = userDao.findById(fromUserId).orElseThrow(
                () -> new UserNotFoundException("User not found " + fromUserId)
        );
        User userTo = userDao.findById(toUserId).orElseThrow(
                () -> new UserNotFoundException("User not found " + toUserId)
        );

        if (userFrom.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException(
                    "Insufficient balance. Required: " + amount + ", Available: " + userFrom.getBalance()
            );
        }

        try(var connection = DatabaseConfig.getConnection()) {
            connection.setAutoCommit(false);

            try {
                userDao.downBalance(fromUserId, amount);
                userDao.addBalance(toUserId, amount);

                Transaction tx = new Transaction();
                tx.setType(TransactionType.P2P_TRANSFER);
                tx.setFromUserId(fromUserId);
                tx.setToUserId(toUserId);
                tx.setAmount(amount);
                transactionDao.save(tx);
                connection.commit();

            } catch (SQLException e) {
                connection.rollback();
                throw new TransferException("Transfer failed, transaction rolled back", e);
            }


        } catch (SQLException e) {
            throw new TransferException("Database error during transaction", e);
        }

    }
}
