package config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class AntiFraudProperties {
    private final int maxOperationsPerHour;
    private final int maxWarnings;
    private final String suspiciousAmountThreshold;
    private final int maxWithdrawalsPer5Min;

    public AntiFraudProperties(@Value("${antiFraud.maxOperationsPerHour}") int maxOperationsPerHour,
                               @Value("${antiFraud.maxWarnings}") int maxWarnings,
                               @Value("${antiFraud.suspiciousAmountThreshold}") String suspiciousAmountThreshold,
                               @Value("${antiFraud.maxWithdrawalsPer5Min}") int getMaxWithdrawalsPer5Min){
        this.maxOperationsPerHour = maxOperationsPerHour;
        this.maxWarnings = maxWarnings;
        this.suspiciousAmountThreshold = suspiciousAmountThreshold;
        this.maxWithdrawalsPer5Min = getMaxWithdrawalsPer5Min;
    }

    public int getMaxOperationsPerHour() {
        return maxOperationsPerHour;
    }

    public int getMaxWarnings() {
        return maxWarnings;
    }

    public String getSuspiciousAmountThreshold() {
        return suspiciousAmountThreshold;
    }

    public int getMaxWithdrawalsPer5Min() {
        return maxWithdrawalsPer5Min;
    }
}
