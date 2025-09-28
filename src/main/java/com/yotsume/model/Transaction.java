package com.yotsume.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class Transaction {
    private Long id;
    private Long fromUserId;
    private Long toUserId;
    private BigDecimal amount;
    private LocalDateTime timestamp;
}
