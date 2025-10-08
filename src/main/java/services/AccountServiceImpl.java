package services;

import config.AccountProperties;
import model.Account;
import model.OperationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repository.AccountRepository;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class AccountServiceImpl implements AccountService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountServiceImpl.class);
    private final AccountRepository accountRepository;
    private final AccountProperties accountProperties;
    private final OperationLogService operationLogService;

    public AccountServiceImpl(AccountRepository accountRepository, AccountProperties accountProperties, OperationLogService operationLogService) {
        this.accountRepository = accountRepository;
        this.accountProperties = accountProperties;
        this.operationLogService = operationLogService;
    }

    @Override
    public Account createAccount(Long userId) {
        if (accountProperties.getDefaultAmount() == null || accountProperties.getDefaultAmount().compareTo(BigDecimal.ZERO) < 0) {
            LOGGER.error("Account default amount is negative");
            throw new IllegalArgumentException("Amount cannot be null or negative");
        }
        LOGGER.info("Creating account with userId {}", userId);
        Account account = accountRepository.createAccount(userId);
        LOGGER.info("Created account with id {} for user {}", account.getId(), account.getUserId());
        return account;
    }

    @Override
    @Transactional
    public void topUpAccount(Long accountId, BigDecimal amount) {
        LOGGER.info("Topping up account {} with amount {}", accountId, amount);

        Account account = accountRepository.getAccountById(accountId).orElseThrow(
                () -> new IllegalStateException("Account with id " + accountId + " not found")
        );

        accountRepository.topUpAccount(accountId, amount);
        operationLogService.save(
                OperationType.TOP_UP, null,
                accountId, amount, null, account.getUserId()
        );

        LOGGER.info("Successfully topped up account {}", accountId);
        LOGGER.info("Log saved for account {}", accountId);
    }

    @Override
    @Transactional
    public void withdrawAccount(Long accountId, BigDecimal amount) {
        LOGGER.info("Withdrawing account {} with amount {}", accountId, amount);

        Account account = accountRepository.getAccountById(accountId).orElseThrow(
                () -> new IllegalStateException("Account with id " + accountId + " not found")
        );
        accountRepository.withdrawAccount(accountId, amount);
        operationLogService.save(
                OperationType.WITHDRAW, accountId,
                null, amount, null, account.getUserId()
        );

        LOGGER.info("Successfully withdraw account {}", accountId);
        LOGGER.info("Log saved for account {}", accountId);
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
            LOGGER.error("Cannot delete account {}: user has only one account", accountId);
            throw new IllegalArgumentException("Cannot delete an account, there is only one account");
        }

        Account firstAccount = userAccounts.stream()
                        .min(Comparator.comparing(Account::getId))
                        .orElseThrow(() -> new IllegalArgumentException("User has no other accounts to transfer funds to"));
        LOGGER.info("Transferring remaining funds ({}) from account {} to account {}",
                currentAccount.getBalance(), accountId, firstAccount.getId());
        accountRepository.topUpAccount(firstAccount.getId(), currentAccount.getBalance());
        LOGGER.info("Successful transfer funds from {} to {}", accountId, firstAccount.getId());
        LOGGER.info("Deleting account with accountId {}", accountId);
        accountRepository.deleteAccount(accountId);
        LOGGER.info("Successful deletion of account with accountId {}", accountId);
    }

    @Override
    public Optional<Account> getAccountById(Long accountId) {
        LOGGER.info("Getting account with accountId {}", accountId);
        return accountRepository.getAccountById(accountId);
    }

    @Override
    public List<Account> findAccountsByUserId(Long userId) {
        LOGGER.info("Finding accounts by userId {}", userId);
        return accountRepository.findAccountsByUserId(userId);
    }

    @Override
    @Transactional
    public void transferMoney(Long fromAccountId, Long toAccountId, BigDecimal amount){
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            LOGGER.error("Amount ({}) cannot be null or negative", amount);
            throw new IllegalArgumentException("Transfer amount must be positive");
        }

        if (fromAccountId.equals(toAccountId)) {
            LOGGER.error("FromAccountId {} and ToAccountId {} cannot be the same", fromAccountId, toAccountId);
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
            LOGGER.info("Applied commission {} for inter-user transfer", commissionAmount);
        }
        LOGGER.info("Starting to transfer funds from {} to {}", fromAccountId, toAccountId);
        accountRepository.withdrawAccount(fromAccountId, amount);
        accountRepository.topUpAccount(toAccountId, creditToRecipient);
        if (amount.equals(creditToRecipient)) {
            operationLogService.save(
                    OperationType.TRANSFER, fromAccountId, toAccountId,
                    amount, null, fromAccount.getUserId()
            );
        } else {
            BigDecimal commissionAmount = amount.multiply(commission);
            operationLogService.save(
                    OperationType.TRANSFER, fromAccountId, toAccountId,
                    amount, commissionAmount, fromAccount.getUserId()
            );
        }
        LOGGER.info("Successful transfer funds from {} to {}", fromAccountId, toAccountId);
        LOGGER.info("Successful log saved for account {}", fromAccountId);
    }
}
