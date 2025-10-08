package services;

public interface AntiFraudService {
    boolean hasTooManyOperations(Long accountId);
    boolean hasTooManyWarnings(Long accountId);
    boolean isAccountBlocked(Long accountId);
    void addWarning(Long accountId);
    void blockAccount(Long accountId);
    boolean isSuspicious(Long accountId);
    void addOperation(Long accountId);
}
