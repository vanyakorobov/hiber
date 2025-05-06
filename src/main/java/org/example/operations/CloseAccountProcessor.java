package org.example.operations;

import org.example.account.Account;
import org.example.account.AccountService;
import org.example.user.User;
import org.example.user.UserService;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class CloseAccountProcessor implements OperationCommandProcessor {

    private final Scanner scanner;
    private final AccountService accountService;
    private final UserService userService;

    public CloseAccountProcessor(Scanner scanner, AccountService accountService, UserService userService) {
        this.scanner = scanner;
        this.accountService = accountService;
        this.userService = userService;
    }

    @Override
    public void processOperation() {
        System.out.println("Enter account id to close");
        Long accountId = Long.parseLong(scanner.nextLine());
        Account account = accountService.closeAccount(accountId);
        User user = account.getUser();
        if (user == null) {
            throw new IllegalArgumentException("No such user associated with account id=" + accountId);
        }
        user.getAccountList().remove(account);
        System.out.println("Account closed");
    }

    @Override
    public ConsolOperationType getOperationType() {
        return ConsolOperationType.ACCOUNT_CLOSE;
    }
}
