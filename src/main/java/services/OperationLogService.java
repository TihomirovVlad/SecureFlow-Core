package services;

import model.OperationLog;
import model.enums.OperationType;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface OperationLogService {
    OperationLog save(OperationType operationType, Long fromAccountId, Long toAccountId,
                      BigDecimal amount, BigDecimal commission, Long userId);

    Optional<OperationLog> findByOperationLogId(Long operationLogId);
    List<OperationLog> findByUserId(Long userId);
}
