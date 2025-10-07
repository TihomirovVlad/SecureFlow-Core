package services;

import model.OperationLog;
import model.OperationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import repository.OperationLogRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class OperationLogServiceImpl implements OperationLogService {

    private final static Logger LOGGER = LoggerFactory.getLogger(OperationLogServiceImpl.class);
    private final OperationLogRepository operationLogRepository;

    public OperationLogServiceImpl(OperationLogRepository operationLogRepository) {
        this.operationLogRepository = operationLogRepository;
    }

    @Override
    public OperationLog save(OperationType operationType, Long fromAccountId, Long toAccountId,
                             BigDecimal amount, BigDecimal commission, Long userId) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            LOGGER.error("amount is null or negative");
            throw new IllegalArgumentException("Amount cannot be null or negative");
        }

        if (operationType == OperationType.TRANSFER) {
            if (fromAccountId == null || toAccountId == null) {
                LOGGER.error("fromAccountId or toAccountId is null for operationType: TRANSFER");
                throw new IllegalArgumentException("For TRANSFER: fromAccountId and toAccountId cannot be null");
            }
        } else if (operationType == OperationType.TOP_UP) {
            if (toAccountId == null) {
                LOGGER.error("toAccountId is null for operationType: TOP_UP");
                throw new IllegalArgumentException("For TOP_UP: toAccountId cannot be null");
            }
        } else if (operationType == OperationType.WITHDRAW) {
            if (fromAccountId == null) {
                LOGGER.error("fromAccountId is null for operationType: WITHDRAW");
                throw new IllegalArgumentException("For WITHDRAW: fromAccountId cannot be null");
            }
        }
        LOGGER.info("Saving operation log");
        OperationLog operationLog = operationLogRepository.save(
                operationType, fromAccountId,
                toAccountId, amount,
                commission, userId
        );
        LOGGER.info("Operation log successfully saved with id {}", operationLog.getId());
        return operationLog;
    }

    @Override
    public Optional<OperationLog> findByOperationLogId(Long operationLogId) {
        LOGGER.info("Finding operation log by id {}", operationLogId);
        Optional<OperationLog> log = operationLogRepository.findByOperationLogId(operationLogId);
        if (log.isPresent()) {
            LOGGER.info("Operation log successfully found with id {}", operationLogId);
        } else {
            LOGGER.info("Operation log not found with id {}", operationLogId);
        }
        return log;
    }

    @Override
    public List<OperationLog> findByUserId(Long userId) {
        if (userId == null) {
            LOGGER.error("userId is null");
            throw new IllegalArgumentException("userId cannot be null");
        }
        LOGGER.info("Finding operations log by userId {}", userId);
        return operationLogRepository.findByUserId(userId);
    }
}
