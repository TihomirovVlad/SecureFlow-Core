package services;

import config.AccountProperties;
import model.Account;
import org.springframework.transaction.annotation.Transactional;
import repository.AccountRepository;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final AccountProperties accountProperties;

    public AccountServiceImpl(AccountRepository accountRepository, AccountProperties accountProperties) {
        this.accountRepository = accountRepository;
        this.accountProperties = accountProperties;
    }

    @Override
    public Account createAccount(Long userId) {
        if (accountProperties.getDefaultAmount() == null || accountProperties.getDefaultAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Amount cannot be null or negative");
        }
        return accountRepository.createAccount(userId);
    }

    @Override
    public void topUpAccount(Long accountId, BigDecimal amount) {
        accountRepository.topUpAccount(accountId, amount);
    }

    @Override
    public void withdrawAccount(Long accountId, BigDecimal amount) {
        accountRepository.withdrawAccount(accountId, amount);
    }

    @Override
    @Transactional
    public void deleteAccount(Long accountId) {
        Account currentAccount = accountRepository
                .getAccountById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));

        Long userAccountId = currentAccount.getUserId();

        List<Account> userAccounts = accountRepository.findAccountsByUserId(userAccountId);

        if (userAccounts.size() <= 1) {
            throw new IllegalArgumentException("Cannot delete an account, there is only one account");
        }

        Account firstAccount = userAccounts.stream()
                        .min(Comparator.comparing(Account::getId))
                        .orElseThrow(() -> new IllegalArgumentException("User has no other accounts to transfer funds to"));

        accountRepository.topUpAccount(firstAccount.getId(), currentAccount.getBalance());
        accountRepository.deleteAccount(accountId);
    }

    @Override
    public Optional<Account> getAccountById(Long accountId) {
        return accountRepository.getAccountById(accountId);
    }

    @Override
    public List<Account> findAccountsByUserId(Long userId) {
        return accountRepository.findAccountsByUserId(userId);
    }

    @Override
    @Transactional
    public void transferMoney(Long fromAccountId, Long toAccountId, BigDecimal amount){
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transfer amount must be positive");
        }

        if (fromAccountId.equals(toAccountId)) {
            throw new IllegalArgumentException("Cannot transfer to the same account");
        }

        Account fromAccount = accountRepository
                .getAccountById(fromAccountId)
                .orElseThrow(() -> new IllegalArgumentException("Source account not found"));

        Account toAccount = accountRepository
                .getAccountById(toAccountId)
                .orElseThrow(() -> new IllegalArgumentException("Destination account not found"));

        BigDecimal commission = accountProperties.getTransferCommission();
        BigDecimal creditToRecipient = amount;

        if (!fromAccount.getUserId().equals(toAccount.getUserId())) {
            BigDecimal commissionAmount = amount.multiply(commission);
            creditToRecipient = amount.subtract(commissionAmount);
        }
        accountRepository.withdrawAccount(fromAccountId, amount);
        accountRepository.topUpAccount(toAccountId, creditToRecipient);

    }
}
