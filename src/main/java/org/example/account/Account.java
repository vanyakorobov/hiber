package org.example.account;

import jakarta.persistence.*;
import org.example.user.User;

@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "money_ammount", nullable = false)
    private int moneyAmmount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Account() {
    }

    public Account(User user, int moneyAmmount) {
        this.user = user;
        this.moneyAmmount = moneyAmmount;
    }

    public Long getId() {
        return id;
    }

    public int getMoneyAmmount() {
        return moneyAmmount;
    }

    public void setMoneyAmmount(int moneyAmmount) {
        this.moneyAmmount = moneyAmmount;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", moneyAmmount=" + moneyAmmount +
                ", userId=" + (user != null ? user.getId() : null) +
                '}';
    }
}
