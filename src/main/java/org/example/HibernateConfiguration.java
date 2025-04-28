package org.example;

import org.example.account.Account;
import org.example.user.User;
import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HibernateConfiguration {

    @Bean
    public SessionFactory sessionFactory() {
        org.hibernate.cfg.Configuration configuration = new org.hibernate.cfg.Configuration();
        configuration
                .addAnnotatedClass(Account.class)
                .addAnnotatedClass(User.class)
                .addPackage("org.example")
                .setProperty("hibernate.connection.driver_class", "org.postgresql.Driver")
                                .setProperty("hibernate.connection.url", "jdbc:postgresql://localhost:5432/postgres")
                                                .setProperty("hibernate.connection.username", "postgres")
                                                .setProperty("hibernate.connection.password", "mysecretpassword")
                                                .setProperty("hibernate.show_sql", "true")
                                                .setProperty("hibernate.hbm2ddl.auto", "update")
                                                .setProperty("hibernate.current_session_context_class", "thread");
        return configuration.buildSessionFactory();
    }
}
