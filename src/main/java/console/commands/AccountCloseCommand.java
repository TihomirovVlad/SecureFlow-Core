package console.commands;

import console.ConsoleOperationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import services.AccountService;
import java.util.Scanner;


@Component
public class AccountCloseCommand implements OperationCommand {
    private final static Logger LOGGER = LoggerFactory.getLogger(AccountCloseCommand.class);
    private final AccountService accountService;

    public AccountCloseCommand(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public void execute(Scanner scanner) {
        System.out.println("Enter accountId for closing");
        Long accountId = scanner.nextLong();
        scanner.nextLine();
        LOGGER.info("Start closing account {}", accountId);
        accountService.deleteAccount(accountId);
        System.out.println("Account " + accountId + " has been closed");
        LOGGER.info("Account {} closed", accountId);
    }

    @Override
    public ConsoleOperationType getOperationType() {
        return ConsoleOperationType.ACCOUNT_CLOSE;
    }
}

