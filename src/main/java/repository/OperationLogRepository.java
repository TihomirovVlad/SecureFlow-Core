package repository;

import model.OperationLog;
import model.OperationType;

import java.math.BigDecimal;

public interface OperationLogRepository {
    OperationLog save(OperationType operationType, Long fromAccountId, Long toAccountId,
              BigDecimal amount, BigDecimal commission, Long userId);
}
