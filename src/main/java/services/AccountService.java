package services;

import model.Account;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface AccountService {
    Account createAccount(Long userId);
    void topUpAccount(Long accountId, BigDecimal amount);
    void withdrawAccount(Long accountId, BigDecimal amount);
    void deleteAccount(Long accountId);
    Optional<Account> getAccountById(Long accountId);
    List<Account> findAccountsByUserId(Long userId);
    void transferMoney(Long accountId, Long toAccountId, BigDecimal amount);
}
