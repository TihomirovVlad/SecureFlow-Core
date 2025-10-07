package repository;

import model.Account;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface AccountRepository {
    Account createAccount(Long userId);
    void topUpAccount(Long accountId, BigDecimal amount);
    void withdrawAccount(Long accountId, BigDecimal amount);
    void deleteAccount(Long accountId);
    Optional<Account> getAccountById(Long accountId);
    List<Account> findAccountsByUserId(Long userId);
}
