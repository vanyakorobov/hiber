package org.example.user;

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

    public UserService(SessionFactory sessionFactory, AccountService accountService) {
        this.sessionFactory = sessionFactory;
        this.accountService = accountService;
    }

    public User createUser(String login) {
        Session session = null;
        Transaction tx = null;

        try {
            session = sessionFactory.openSession();
            tx = session.beginTransaction();

            // Проверка: логин уже занят?
            var query = session.createQuery("FROM User WHERE login = :login", User.class);
            query.setParameter("login", login);
            if (!query.getResultList().isEmpty()) {
                throw new IllegalArgumentException("Login already exists=" + login);
            }

            // Создаём пользователя
            User newUser = new User(login);

            // Создаём аккаунт с нулевым балансом
            Account newAccount = new Account(newUser, 0);

            // Двусторонняя связь
            newUser.getAccountList().add(newAccount);

            // Сохраняем пользователя, аккаунт сохранится каскадно
            session.save(newUser);

            tx.commit();
            return newUser;

        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw new RuntimeException("Can't create user", e);
        } finally {
            if (session != null) session.close();
        }
    }

    public Optional<User> findUserById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            return Optional.ofNullable(session.get(User.class, id));
        }
    }

    public List<User> getAllUsers() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM User", User.class).list();
        }
    }
}
