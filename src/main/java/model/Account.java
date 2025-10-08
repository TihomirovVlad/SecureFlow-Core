package model;

import config.AccountProperties;

import java.math.BigDecimal;

public class Account {
    private Long id;
    private Long userId;
    private BigDecimal balance;
    private AccountStatus accountStatus;

    public Account() {}

    public Account(BigDecimal balance) {
        this.balance = balance;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setAccountStatus(AccountStatus accountStatus) {
        this.accountStatus = accountStatus;
    }

    public AccountStatus getAccountStatus() {
        return accountStatus;
    }

    public BigDecimal getBalance() {
        return balance;
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
        this.balance = balance;
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", userId=" + userId +
                ", balance=" + balance +
                '}';
    }
}
