package org.example.account;

import org.example.user.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AccountService {

    private final SessionFactory sessionFactory;
    private final AccountProperties accountProperties;

    public AccountService(SessionFactory sessionFactory, AccountProperties accountProperties) {
        this.sessionFactory = sessionFactory;
        this.accountProperties = accountProperties;
    }

    public Account createAccount(User user) {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();

        try {
            Account account = new Account(user, accountProperties.getDefaultAccountAmount());
            session.save(account);
            tx.commit();
            return account;
        } catch (Exception e) {
            tx.rollback();
            throw new RuntimeException("Failed to create account", e);
        } finally {
            session.close();
        }
    }

    public Optional<Account> findAccountById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            return Optional.ofNullable(session.get(Account.class, id));
        }
    }

    public List<Account> getAllAccounts(Long userId) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                            "FROM Account WHERE user.id = :userId", Account.class)
                    .setParameter("userId", userId)
                    .getResultList();
        }
    }

    public void depositAccount(Long accountId, int moneyToDeposit) {
        if (moneyToDeposit <= 0)
            throw new IllegalArgumentException("Deposit amount must be positive");

        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            Account account = session.get(Account.class, accountId);
            if (account == null) {
                throw new IllegalArgumentException("No such account id=" + accountId);
            }
            account.setMoneyAmmount(account.getMoneyAmmount() + moneyToDeposit);
            session.update(account);
            tx.commit();
        }
    }

    public void withdrawFromAccount(Long accountId, int amountToWithdraw) {
        if (amountToWithdraw <= 0)
            throw new IllegalArgumentException("Withdraw amount must be positive");

        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            Account account = session.get(Account.class, accountId);
            if (account == null || account.getMoneyAmmount() < amountToWithdraw) {
                throw new IllegalArgumentException("Not enough funds or no such account");
            }
            account.setMoneyAmmount(account.getMoneyAmmount() - amountToWithdraw);
            session.update(account);
            tx.commit();
        }
    }

    public Account closeAccount(Long accountId) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();

            Account accountToRemove = session.get(Account.class, accountId);
            if (accountToRemove == null) {
                throw new IllegalArgumentException("No such account id=" + accountId);
            }

            List<Account> accounts = getAllAccounts(accountToRemove.getUser().getId());
            if (accounts.size() <= 1) {
                throw new IllegalArgumentException("Cannot close the last account");
            }

            Account accountToDeposit = accounts.stream()
                    .filter(a -> !a.getId().equals(accountId))
                    .findFirst()
                    .orElseThrow();

            accountToDeposit.setMoneyAmmount(accountToDeposit.getMoneyAmmount() + accountToRemove.getMoneyAmmount());

            session.update(accountToDeposit);
            session.remove(accountToRemove);

            tx.commit();
            return accountToRemove;
        }
    }

    public void transfer(Long fromAccountId, Long toAccountId, int amountToTransfer) {
        if (amountToTransfer <= 0)
            throw new IllegalArgumentException("Transfer amount must be positive");

        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();

            Account from = session.get(Account.class, fromAccountId);
            Account to = session.get(Account.class, toAccountId);

            if (from == null || to == null)
                throw new IllegalArgumentException("Invalid account IDs");

            if (from.getMoneyAmmount() < amountToTransfer)
                throw new IllegalArgumentException("Insufficient funds");

            int finalAmount = from.getUser().getId().equals(to.getUser().getId())
                    ? amountToTransfer
                    : (int) (amountToTransfer * (1 - accountProperties.getTransferCommission()));

            from.setMoneyAmmount(from.getMoneyAmmount() - amountToTransfer);
            to.setMoneyAmmount(to.getMoneyAmmount() + finalAmount);

            session.update(from);
            session.update(to);

            tx.commit();
        }
    }
}
