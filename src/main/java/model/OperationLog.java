package model;

import model.enums.OperationType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class OperationLog {
    private Long id;
    private OperationType operationType;
    private Long fromAccountId;
    private Long toAccountId;
    private BigDecimal amount;
    private BigDecimal commission;
    private LocalDateTime createdAt;
    private Long userId;

    public OperationLog() {}

    public OperationLog(Long id, OperationType operationType,
                        Long fromAccountId, Long toAccountId,
                        BigDecimal amount, BigDecimal commission) {
        this.id = id;
        this.operationType = operationType;
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
        this.amount = amount;
        this.commission = commission;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OperationType getOperationType() {
        return operationType;
    }

    public void setOperationType(OperationType operationType) {
        this.operationType = operationType;
    }

    public Long getFromAccountId() {
        return fromAccountId;
    }

    public void setFromAccountId(Long fromAccountId) {
        this.fromAccountId = fromAccountId;
    }

    public Long getToAccountId() {
        return toAccountId;
    }

    public void setToAccountId(Long toAccountId) {
        this.toAccountId = toAccountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getCommission() {
        return commission;
    }

    public void setCommission(BigDecimal commission) {
        this.commission = commission;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "OperationLog{" +
                "id=" + id +
                ", operationType='" + operationType + '\'' +
                ", fromAccountId=" + fromAccountId +
                ", toAccountId=" + toAccountId +
                ", amount=" + amount +
                ", commission=" + commission +
                ", createdAt=" + createdAt +
                ", userId=" + userId +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OperationLog that)) return false;
        return Objects.equals(id, that.id) && Objects.equals(operationType, that.operationType) && Objects.equals(fromAccountId, that.fromAccountId) && Objects.equals(toAccountId, that.toAccountId) && Objects.equals(amount, that.amount) && Objects.equals(commission, that.commission) && Objects.equals(createdAt, that.createdAt) && Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, operationType, fromAccountId, toAccountId, amount, commission, createdAt, userId);
    }
}
