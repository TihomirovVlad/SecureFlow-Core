package console.commands;

import console.ConsoleOperationType;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import services.UserService;

import java.util.List;
import java.util.Scanner;


@Component
public class ShowAllUsersCommand implements OperationCommand {
    private final static Logger LOGGER = LoggerFactory.getLogger(ShowAllUsersCommand.class);
    private final UserService userService;

    public ShowAllUsersCommand(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void execute(Scanner scanner) {
        System.out.println("All users in DB:");
        List<User> users = userService.findAllUsers();
        if (users.isEmpty()) {
            System.out.println("No users in DB");
            LOGGER.info("No users in DB");
            throw new RuntimeException("No users in DB");
        }
        users.forEach(System.out::println);
        LOGGER.info("Fetched {} users from DB", users.size());
    }

    @Override
    public ConsoleOperationType getOperationType() {
        return ConsoleOperationType.SHOW_ALL_USERS;
    }
}

