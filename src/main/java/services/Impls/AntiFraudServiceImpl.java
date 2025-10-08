package services.Impls;

import model.enums.AccountStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import repository.AccountRepository;
import services.AntiFraudService;
import services.RedisService;
import java.time.Duration;

@Service
public class AntiFraudServiceImpl implements AntiFraudService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AntiFraudServiceImpl.class);
    private final RedisService redisService;
    private final AccountRepository accountRepository;

    public AntiFraudServiceImpl(RedisService redisService, AccountRepository accountRepository) {
        this.redisService = redisService;
        this.accountRepository = accountRepository;
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
        if (count != null && count >= 10){
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
        if (warnings != null && warnings >= 3){
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
}
