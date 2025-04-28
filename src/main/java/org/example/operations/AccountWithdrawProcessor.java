package org.example.operations;

import org.example.account.AccountService;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class AccountWithdrawProcessor implements  OperationCommandProcessor{

    private final Scanner scanner;
    private final AccountService accountService;

    public AccountWithdrawProcessor(Scanner scanner, AccountService accountService) {
        this.scanner = scanner;
        this.accountService = accountService;
    }

    @Override
    public void processOperation() {
        System.out.println("Enter account id");
        Long accountId = Long.parseLong(scanner.nextLine());
        System.out.println("Enter amount to withdraw");
        int amountToWithdraw = Integer.parseInt(scanner.nextLine());
        accountService.withdrawFromAccount(accountId, amountToWithdraw);
        System.out.println("Withdrawn " + amountToWithdraw + " from account " + accountId);
    }

    @Override
    public ConsolOperationType getOperationType() {
        return ConsolOperationType.ACCOUNT_WITHDRAW;
    }
}
