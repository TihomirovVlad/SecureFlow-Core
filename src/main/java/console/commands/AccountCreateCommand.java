package console.commands;

import console.ConsoleOperationType;
import model.Account;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import services.AccountService;
import java.util.Scanner;

@Component
public class AccountCreateCommand implements OperationCommand {
    private final static Logger LOGGER = LoggerFactory.getLogger(AccountCreateCommand.class);
    private final AccountService accountService;

    public AccountCreateCommand(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public void execute(Scanner scanner) {
        System.out.println("Enter userId for creating account: ");
        Long userId = scanner.nextLong();
        scanner.nextLine();
        LOGGER.info("Creating account for userId: {}", userId);
        Account account = accountService.createAccount(userId);
        System.out.println("Account created with ID: " + account.getId());
        LOGGER.info("Account created");
    }

    @Override
    public ConsoleOperationType getOperationType() {
        return ConsoleOperationType.ACCOUNT_CREATE;
    }
}
