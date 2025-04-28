package org.example.operations;

import org.example.account.Account;
import org.example.account.AccountService;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class AccountTransferProcessor implements OperationCommandProcessor{

    private final Scanner scanner;
    private final AccountService accountService;

    public AccountTransferProcessor(Scanner scanner, AccountService accountService) {
        this.scanner = scanner;
        this.accountService = accountService;
    }

    @Override
    public void processOperation() {
        System.out.println("Enter account id");
        Long fromAccountId = Long.parseLong(scanner.nextLine());
        Long toAccountId = Long.parseLong(scanner.nextLine());
        System.out.println("Enter amount to be transferred");
        int amountToTransfer = Integer.parseInt(scanner.nextLine());
        accountService.transfer(fromAccountId, toAccountId, amountToTransfer);
        System.out.println("Account transfer successful");
    }

    @Override
    public ConsolOperationType getOperationType() {
        return ConsolOperationType.ACCOUNT_TRANSFER;
    }
}
