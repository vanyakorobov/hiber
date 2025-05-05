package org.example.user;

import org.example.TransactionService;
import org.example.account.Account;
import org.example.account.AccountService;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final SessionFactory sessionFactory;
    private final AccountService accountService;
    private final TransactionService transactionService;

    public UserService(SessionFactory sessionFactory,
                       AccountService accountService,
                       TransactionService transactionService) {
        this.sessionFactory = sessionFactory;
        this.accountService = accountService;
        this.transactionService = transactionService;
    }

    public User createUser(String login) {
        return transactionService.executeInTransaction(() -> {
            Session session = sessionFactory.getCurrentSession();

            // Проверка: логин уже занят?
            var query = session.createQuery("FROM User WHERE login = :login", User.class);
            query.setParameter("login", login);
            if (!query.getResultList().isEmpty()) {
                throw new IllegalArgumentException("Login already exists=" + login);
            }

            // Создаём пользователя и аккаунт
            User newUser = new User(login);
            Account newAccount = new Account(newUser, 0);
            newUser.getAccountList().add(newAccount);

            session.save(newUser);
            return newUser;
        });
    }

    public Optional<User> findUserById(Long id) {
        return transactionService.executeInTransaction(() -> {
            Session session = sessionFactory.getCurrentSession();
            return Optional.ofNullable(session.get(User.class, id));
        });
    }

    public List<User> getAllUsers() {
        return transactionService.executeInTransaction(() -> {
            Session session = sessionFactory.getCurrentSession();
            return session.createQuery("FROM User", User.class).list();
        });
    }
}
