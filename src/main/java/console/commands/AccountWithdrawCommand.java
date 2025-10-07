package console.commands;

import console.ConsoleOperationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import services.AccountService;
import java.math.BigDecimal;
import java.util.Scanner;

@Component
public class AccountWithdrawCommand implements OperationCommand {
    private final static Logger LOGGER = LoggerFactory.getLogger(AccountWithdrawCommand.class);
    private final AccountService accountService;

    public AccountWithdrawCommand(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public void execute(Scanner scanner) {
        System.out.println("Enter accountId for withdraw: ");
        Long accountId = scanner.nextLong();
        scanner.nextLine();
        System.out.println("Enter amount to withdraw: ");
        BigDecimal amount = scanner.nextBigDecimal();
        scanner.nextLine();
        LOGGER.info("Starting withdrawing from account {}, amount {}", accountId, amount);
        accountService.withdrawAccount(accountId, amount);
        System.out.println("Withdraw of " + amount + " from account " + accountId + " successful");
        LOGGER.info("Successful withdraw from account {}, amount {}", accountId, amount);
    }

    @Override
    public ConsoleOperationType getOperationType() {
        return ConsoleOperationType.ACCOUNT_WITHDRAW;
    }
}



