package repository;

import model.OperationLog;
import model.OperationType;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Repository
public class OperationLogRepositoryImpl implements OperationLogRepository {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public OperationLogRepositoryImpl(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    private static final RowMapper<OperationLog> ROW_MAPPER = (rs, i) -> {
        OperationLog operationLog = new OperationLog();
        operationLog.setId(rs.getLong("id"));
        operationLog.setOperationType(OperationType.valueOf(rs.getString("operation_type")));
        operationLog.setFromAccountId(rs.getObject("from_account_id", Long.class));
        operationLog.setToAccountId(rs.getObject("to_account_id", Long.class));
        operationLog.setAmount(rs.getBigDecimal("amount"));
        operationLog.setCommission(rs.getBigDecimal("commission"));
        Timestamp timestamp = rs.getTimestamp("created_at");
        operationLog.setCreatedAt(timestamp != null ? timestamp.toLocalDateTime() : null);
        operationLog.setUserId(rs.getObject("user_id", Long.class));
        return operationLog;
    };

    @Override
    public OperationLog save(OperationType operationType, Long fromAccountId, Long toAccountId,
                     BigDecimal amount, BigDecimal commission, Long userId) {
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }

        if (operationType == OperationType.TRANSFER) {
            if (fromAccountId == null || toAccountId == null) {
                throw new IllegalArgumentException("For TRANSFER: fromAccountId and toAccountId cannot be null");
            }
        } else if (operationType == OperationType.TOP_UP) {
            if (toAccountId == null) {
                throw new IllegalArgumentException("For TOP_UP: toAccountId cannot be null");
            }
        } else if (operationType == OperationType.WITHDRAW) {
            if (fromAccountId == null) {
                throw new IllegalArgumentException("For WITHDRAW: fromAccountId cannot be null");
            }
        }

        String sql = """
                        INSERT INTO operations_log (operation_type, from_account_id, to_account_id, amount, commission, created_at, user_id)
                        VALUES (:operation_type, :from_account_id, :to_account_id,
                                :amount, :commission, :created_at, :user_id)
                    """;
        Map<String, Object> params = Map.of(
                "operation_type",operationType,
                "from_account_id", fromAccountId,
                "to_account_id", toAccountId,
                "amount", amount,
                "commission", commission,
                "created_at", LocalDateTime.now(),
                "user_id", userId

        );
        KeyHolder keyHolder = new GeneratedKeyHolder();
        namedParameterJdbcTemplate.update(
                sql,
                new MapSqlParameterSource(params),
                keyHolder,
                new String[]{"id"}
        );

        Long id = keyHolder.getKey().longValue();
        OperationLog operationLog = new OperationLog();
        operationLog.setId(id);
        operationLog.setOperationType(operationType);
        operationLog.setFromAccountId(fromAccountId);
        operationLog.setToAccountId(toAccountId);
        operationLog.setAmount(amount);
        operationLog.setCommission(commission);
        operationLog.setCreatedAt(LocalDateTime.now());
        operationLog.setUserId(userId);
        return operationLog;
    }
}
