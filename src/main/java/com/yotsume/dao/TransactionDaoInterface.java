package com.yotsume.dao;

import com.yotsume.entity.Transaction;
import com.yotsume.enums.TransactionType;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionDaoInterface {

    RowMapper<Transaction> TRANSACTION_ROW_MAPPER = (rs, numRows) -> {
        Transaction transaction = new Transaction();
        transaction.setId(rs.getLong("id"));
        transaction.setType(TransactionType.valueOf(rs.getString("type")));
        transaction.setFromUserId(rs.getLong("from_user_id"));
        transaction.setToUserId(rs.getLong("to_user_id"));
        transaction.setAmount(rs.getBigDecimal("amount"));
        transaction.setTimestamp(rs.getTimestamp("created_at").toLocalDateTime());
        return transaction;
    };

    Transaction save(Transaction transaction);
    List<Transaction> findByUserId(Long userId);
}
