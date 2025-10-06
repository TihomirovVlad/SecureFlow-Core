package repository;

import model.Account;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface AccountRepository {
    void createAccount(Long userId, BigDecimal moneyAmount);
    void topUpAccount(Long accountId, BigDecimal amount);
    void withdrawAccount(Long accountId, BigDecimal amount);
    void transferMoney(Long fromAccountId, Long toAccountId, BigDecimal amount);
    void deleteAccount(Long accountId);
    boolean accountExistByUserId(Long userId);
    Optional<Account> getAccountById(Long accountId);
    List<Account> findAccountsByUserId(Long userId);
}
