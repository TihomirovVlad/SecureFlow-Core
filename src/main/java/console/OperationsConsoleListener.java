package console;

import console.commands.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

@Component
public class OperationsConsoleListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(OperationsConsoleListener.class);

    private final Map<ConsoleOperationType, OperationCommand> commands;
    private final Scanner scanner = new Scanner(System.in);

    public OperationsConsoleListener(List<OperationCommand> commandList) {
        this.commands = commandList.stream()
                .collect(Collectors.toMap(OperationCommand::getOperationType, cmd -> cmd));
    }

    public void start() {
        System.out.println("=== Bank System Console ===");
        System.out.println("Available commands:");
        System.out.println("- USER_CREATE");
        System.out.println("- SHOW_ALL_USERS");
        System.out.println("- ACCOUNT_CREATE");
        System.out.println("- ACCOUNT_CLOSE");
        System.out.println("- ACCOUNT_DEPOSIT");
        System.out.println("- ACCOUNT_WITHDRAW");
        System.out.println("- ACCOUNT_TRANSFER");
        System.out.println("- exit");
        System.out.println("=============================");

        while (true) {
            System.out.print("Enter command: ");
            String input = scanner.nextLine().trim();

            if ("exit".equalsIgnoreCase(input)) {
                System.out.println("Goodbye!");
                LOGGER.info("Console listener stopped");
                break;
            }

            try {
                ConsoleOperationType operationType = ConsoleOperationType.valueOf(input.toUpperCase());
                OperationCommand command = commands.get(operationType);

                if (command != null) {
                    LOGGER.info("Executing command: {}", operationType);
                    command.execute(scanner);
                    System.out.println("Command executed successfully.\n");
                } else {
                    System.out.println("Command not found: " + operationType);
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Unknown command: " + input);
                System.out.println("Available commands: " +
                        Arrays.stream(ConsoleOperationType.values())
                                .map(Enum::name)
                                .map(String::toLowerCase)
                                .collect(Collectors.joining(", ")));
            } catch (Exception e) {
                System.out.println("Error executing command: " + e.getMessage());
                LOGGER.error("Error in command execution", e);
            }
        }
    }
}
