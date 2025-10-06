package model;

import config.AccountProperties;

import java.math.BigDecimal;

public class Account {
    private Long id;
    private Long userId;
    private BigDecimal moneyAmount;

    public Account() {}

    public Account(BigDecimal balance) {
        this.moneyAmount = balance;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getBalance() {
        return moneyAmount;
    }

    public void setUserId(Long userId) {
        if (userId == null) throw new IllegalArgumentException("userId cannot be null");
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setBalance(BigDecimal balance) {
        if (balance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Balance must be greater than zero");
        }
        this.moneyAmount = balance;
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", userId=" + userId +
                ", balance=" + moneyAmount +
                '}';
    }
}
