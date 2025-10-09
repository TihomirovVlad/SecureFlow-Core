package services.Impls;

import config.AntiFraudProperties;
import model.enums.AccountStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import repository.AccountRepository;
import services.AntiFraudService;
import services.RedisService;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;

@Service
public class AntiFraudServiceImpl implements AntiFraudService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AntiFraudServiceImpl.class);
    private final RedisService redisService;
    private final AccountRepository accountRepository;
    private final AntiFraudProperties antiFraudProperties;

    public AntiFraudServiceImpl(RedisService redisService, AccountRepository accountRepository, AntiFraudProperties antiFraudProperties) {
        this.redisService = redisService;
        this.accountRepository = accountRepository;
        this.antiFraudProperties = antiFraudProperties;
    }

    private void validateAccountId(Long accountId) {
        if (accountId == null) {
            throw new IllegalArgumentException("AccountId cannot be null");
        }
    }


    @Override
    public boolean hasTooManyOperations(Long accountId) {
        validateAccountId(accountId);

        String key = "operations:account:" + accountId + ":count:last_hour";
        Long count = redisService.getCounter(key);
        if (count != null && count >= antiFraudProperties.getMaxOperationsPerHour()){
            LOGGER.warn("Account {} has {} operations in last hour", accountId, count);
            return true;
        }
        return false;
    }

    @Override
    public boolean hasTooManyWarnings(Long accountId) {
        validateAccountId(accountId);

        String key = "warnings:account:" + accountId + ":count:last_7_days";
        Long warnings = redisService.getCounter(key);
        if (warnings != null && warnings >= antiFraudProperties.getMaxWarnings()){
            LOGGER.warn("Account {} has {} warnings in last 7 days", accountId, warnings);
            return true;
        }
        return false;
    }

    @Override
    public boolean isAccountBlocked(Long accountId) {
        validateAccountId(accountId);

        AccountStatus status = accountRepository.getAccountStatus(accountId);
        return AccountStatus.BLOCKED.equals(status);
    }

    @Override
    public void addWarning(Long accountId) {
        validateAccountId(accountId);

        String key = "warnings:account:" + accountId + ":last_7_days";
        redisService.increment(key);
        redisService.setTtl(key, Duration.ofDays(7));

        Long warnings = redisService.getCounter(key);
        LOGGER.info("Account {} now has {} warnings", accountId, warnings);

        if (hasTooManyWarnings(accountId)){
            blockAccount(accountId);
        }
    }

    @Override
    public void blockAccount(Long accountId) {
        validateAccountId(accountId);

        accountRepository.updateAccountStatus(accountId, AccountStatus.BLOCKED);
        LOGGER.warn("Account {} has been permanently blocked", accountId);
    }

    @Override
    public boolean isSuspicious(Long accountId){
        validateAccountId(accountId);

        return hasTooManyOperations(accountId) || hasTooManyWarnings(accountId);
    }

    @Override
    public void addOperation(Long accountId){
        validateAccountId(accountId);

        String key = "operations:account:" + accountId + ":count:last_hour";
        redisService.increment(key);
        redisService.setTtl(key, Duration.ofHours(1));

        Long operations = redisService.getCounter(key);
        LOGGER.info("Account {} now has {} operations", accountId, operations);

        if (hasTooManyOperations(accountId)){
            addWarning(accountId);
        }
    }

    @Override
    public void addOperationWithAmount(Long accountId, BigDecimal amount) {
        validateAccountId(accountId);

        String key = "operations:account:" + accountId + ":last_3";
        String strAmount = amount.toString();

        redisService.saveToList(key, strAmount);
        redisService.setTtl(key, Duration.ofHours(1));

        if (hasThreeSuspiciousOperations(accountId)){
            addWarning(accountId);
        }
    }

    private boolean hasThreeSuspiciousOperations(Long accountId){
        validateAccountId(accountId);

        String key = "operations:account:" + accountId + ":last_3";
        List<Object> operations = redisService.getList(key);

        if (operations == null  || operations.size() < 3){
            return false;
        }
        int suspiciousCount = 0;
        for (Object operation : operations) {
            BigDecimal amount = new BigDecimal(operation.toString());
            if (amount.compareTo(new BigDecimal(antiFraudProperties.getSuspiciousAmountThreshold())) > 0) {
                suspiciousCount++;
            }
        }
        return suspiciousCount >= 3;
    }

    @Override
    public void addWithdrawOperation(Long accountId) {
        validateAccountId(accountId);

        String key = "operations:account:" + accountId + ":withdraw:last_5";

        redisService.increment(key);
        redisService.setTtl(key, Duration.ofMinutes(5));

        if (hasFiveWithdrawOperations(accountId)) {
            addWarning(accountId);
        }
    }

    private boolean hasFiveWithdrawOperations(Long accountId) {
        String key = "operations:account:" + accountId + ":withdraw:last_5";

        Long operations = redisService.getCounter(key);

        return operations != null && operations >= antiFraudProperties.getMaxWithdrawalsPer5Min();
    }
}
