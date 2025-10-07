package console.commands;

import console.ConsoleOperationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import services.AccountService;
import java.math.BigDecimal;
import java.util.Scanner;

@Component
public class AccountDepositCommand implements OperationCommand {
    private final static Logger LOGGER = LoggerFactory.getLogger(AccountDepositCommand.class);
    private final AccountService accountService;

    public AccountDepositCommand(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public void execute(Scanner scanner) {
        System.out.println("Enter accountId for deposit: ");
        Long accountId = scanner.nextLong();
        scanner.nextLine();
        System.out.println("Enter amount to deposit: ");
        BigDecimal amount = scanner.nextBigDecimal();
        scanner.nextLine();
        LOGGER.info("Starting deposit to account {}, amount {}", accountId, amount);
        accountService.topUpAccount(accountId, amount);
        System.out.println("Deposit of " + amount + " to account " + accountId + " successful");
        LOGGER.info("Successful deposit to account {}, amount {}", accountId, amount);
    }

    @Override
    public ConsoleOperationType getOperationType() {
        return ConsoleOperationType.ACCOUNT_DEPOSIT;
    }
}


