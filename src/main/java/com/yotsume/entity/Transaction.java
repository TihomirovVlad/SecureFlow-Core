package com.yotsume.entity;

import com.yotsume.enums.TransactionType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class Transaction {
    private Long id;
    private TransactionType type;
    private Long fromUserId;
    private Long toUserId;
    private BigDecimal amount;
    private LocalDateTime timestamp;
}
