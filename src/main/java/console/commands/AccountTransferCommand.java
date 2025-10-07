package console.commands;

import console.ConsoleOperationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import services.AccountService;
import java.math.BigDecimal;
import java.util.Scanner;

@Component
public class AccountTransferCommand implements OperationCommand {
    private final static Logger LOGGER = LoggerFactory.getLogger(AccountTransferCommand.class);
    private final AccountService accountService;

    public AccountTransferCommand(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public void execute(Scanner scanner) {
        System.out.print("Enter fromAccountId for transfer: ");
        Long fromAccountId = scanner.nextLong();
        scanner.nextLine();

        System.out.print("Enter toAccountId for transfer: ");
        Long toAccountId = scanner.nextLong();
        scanner.nextLine();

        System.out.print("Enter amount to transfer: ");
        BigDecimal transferAmount = scanner.nextBigDecimal();
        scanner.nextLine();

        LOGGER.info("Transferring fromAccountId: {} toAccountId: {}, amount: {}", fromAccountId, toAccountId, transferAmount);
        System.out.println("Transferring " + transferAmount + " from account " + fromAccountId + " to account " + toAccountId);

        accountService.transferMoney(fromAccountId, toAccountId, transferAmount);

        System.out.println("Transferred " + transferAmount + " from account " + fromAccountId + " to account " + toAccountId);
        LOGGER.info("Transferred successful fromAccountId: {} toAccountId: {}", fromAccountId, toAccountId);
    }

    @Override
    public ConsoleOperationType getOperationType() {
        return ConsoleOperationType.ACCOUNT_TRANSFER;
    }
}




