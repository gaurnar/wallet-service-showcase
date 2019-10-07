package org.gsoft.showcase.wallet.domain;

import java.math.BigDecimal;
import java.util.UUID;

public class Wallet {
    private final UUID id;
    private final BigDecimal initialBalance;

    private BigDecimal balance;

    Wallet(UUID id, BigDecimal balance) {
        this.id = id;
        this.balance = balance;
        initialBalance = balance;
    }

    public UUID getId() {
        return id;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public BigDecimal getInitialBalance() {
        return initialBalance;
    }
}
