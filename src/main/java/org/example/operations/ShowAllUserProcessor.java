package org.example.operations;

import org.example.user.User;
import org.example.user.UserService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ShowAllUserProcessor implements OperationCommandProcessor{

    private final UserService userService;

    public ShowAllUserProcessor(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void processOperation() {
        List<User> users = userService.getAllUsers();
        users.forEach(user -> System.out.println(user));
    }

    @Override
    public ConsolOperationType getOperationType() {
        return ConsolOperationType.SHOW_ALL_USERS;
    }
}
