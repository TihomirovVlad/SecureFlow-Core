package console.commands;

import console.ConsoleOperationType;

import java.util.Scanner;

public interface OperationCommand {
    void execute(Scanner scanner);
    ConsoleOperationType getOperationType();
}
