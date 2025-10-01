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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.SQLException;

public class TransferService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransferService.class);
    private final UserDao userDao = new UserDao();
    private final TransactionDao transactionDao = new TransactionDao();

    public void transferMoney(Long fromUserId, Long toUserId, BigDecimal amount) {
        validateTransferArgs(fromUserId, toUserId, amount);


        getUserOrThrow(fromUserId);
        getUserOrThrow(toUserId);

        try {
            executeInTransaction(() -> {
                userDao.downBalance(fromUserId, amount);
                userDao.addBalance(toUserId, amount);

                Transaction tx = createTransaction(TransactionType.P2P_TRANSFER, fromUserId, toUserId, amount);
                transactionDao.save(tx);
            });
        } catch (SQLException e) {
            throw new TransferException("Transfer failed, transaction rolled back", e);
        }
    }

    public void topUpBalance(Long userId, BigDecimal amount) {

        getUserOrThrow(userId);

        try {
            executeInTransaction(() -> {
                userDao.addBalance(userId, amount);

                Transaction tx = createTransaction(TransactionType.TOP_UP, 0L, userId, amount);
                transactionDao.save(tx);
            });
        } catch (SQLException e) {
            throw new TransferException("Top-up failed, transaction rolled back", e);
        }
    }

    private void validateTransferArgs(Long from, Long to, BigDecimal amount) {
        if (from == null || to == null || amount == null) {
            throw new IllegalArgumentException("IDs and amount must not be null");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        if (from.equals(to)) {
            throw new IllegalArgumentException("Cannot transfer to the same user");
        }
    }

    private User getUserOrThrow(Long userId) {
        return userDao.findById(userId).orElseThrow(
                () -> new UserNotFoundException("User not found: ID = " + userId)
        );
    }

    private Transaction createTransaction(TransactionType type, Long fromId, Long toId, BigDecimal amount) {
        Transaction tx = new Transaction();
        tx.setType(type);
        tx.setFromUserId(fromId);
        tx.setToUserId(toId);
        tx.setAmount(amount);
        return tx;
    }

    private void executeInTransaction(ThrowingRunnable action) throws SQLException {
        try (var conn = DatabaseConfig.getConnection()) {
            conn.setAutoCommit(false);
            try {
                action.run();
                conn.commit();
                LOGGER.debug("Transaction committed successfully");
            } catch (Exception e) {
                conn.rollback();
                LOGGER.error("Transaction rolled back due to error", e);
                if (e instanceof SQLException) {
                    throw (SQLException) e;
                }
                throw new SQLException("Transaction failed", e);
            }
        }
    }
}
