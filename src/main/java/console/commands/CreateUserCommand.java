package console.commands;

import console.ConsoleOperationType;
import model.Account;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import services.AccountService;
import services.UserService;

import java.util.Scanner;

@Component
public class CreateUserCommand implements OperationCommand {
    private final static Logger LOGGER = LoggerFactory.getLogger(CreateUserCommand.class);
    private final UserService userService;
    private final AccountService accountService;

    public CreateUserCommand(UserService userService, AccountService accountService) {
        this.userService = userService;
        this.accountService = accountService;
    }

    @Override
    public void execute(Scanner scanner) {
        System.out.println("Enter login: ");
        String login = scanner.nextLine().trim();

        if (login.isEmpty()) {
            System.out.println("Error: Login cannot be empty.");
            LOGGER.error("Login is empty");
            throw new IllegalArgumentException("Login is empty");
        }
        User user = userService.createUser(login);
        LOGGER.info("User created: {}", user);
        Account account = accountService.createAccount(user.getId());
        System.out.println("Account created with ID: " + account.getId());
        LOGGER.info("Account {} created for user {}", account.getId(), user.getId());
    }

    @Override
    public ConsoleOperationType getOperationType() {
        return ConsoleOperationType.USER_CREATE;
    }
}
