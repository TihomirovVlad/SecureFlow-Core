package repository;

import model.Account;

import java.math.BigDecimal;

public interface AccountRepository {
    void createAccount(Account account);
    void topUpAccount(Long accountId, BigDecimal amount);
    void withdrawAccount(Long accountId, BigDecimal amount);
    void transferMoney(Long fromAccountId, Long toAccountId, BigDecimal amount);
    void deleteAccount(Long accountId);
}
