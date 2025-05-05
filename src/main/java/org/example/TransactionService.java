package org.example;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.resource.transaction.spi.TransactionStatus;
import org.springframework.stereotype.Service;

import java.util.function.Supplier;

@Service
public class TransactionService {

    private final SessionFactory sessionFactory;

    public TransactionService(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public <T> T executeInTransaction(Supplier<T> action) {
        Session session = sessionFactory.getCurrentSession();
        Transaction transaction = session.getTransaction();

        boolean isNewTransaction = transaction.getStatus() == TransactionStatus.NOT_ACTIVE;
        if (isNewTransaction) {
            session.beginTransaction();
        }

        try {
            T result = action.get();
            if (isNewTransaction) {
                transaction.commit();
            }
            return result;
        } catch (Exception e) {
            if (isNewTransaction && transaction.getStatus().canRollback()) {
                transaction.rollback();
            }
            throw new RuntimeException("Transaction failed", e);
        } finally {
            if (isNewTransaction && session.isOpen()) {
                session.close();
            }
        }
    }
}
