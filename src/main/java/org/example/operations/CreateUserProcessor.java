package org.example.operations;

import org.example.user.User;
import org.example.user.UserService;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class CreateUserProcessor implements OperationCommandProcessor {

    private final Scanner scanner;
    private final UserService userService;

    public CreateUserProcessor(Scanner scanner, UserService userService) {
        this.scanner = scanner;
        this.userService = userService;
    }

    @Override
    public void processOperation() {
        System.out.println("Enter login");
        String login = scanner.nextLine();
        User user = userService.createUser(login);
        System.out.println("user created" + user.toString());
    }

    @Override
    public ConsolOperationType getOperationType() {
        return ConsolOperationType.USER_CREATE;
    }
}
