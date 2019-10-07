package org.gsoft.showcase.wallet.domain;

import java.math.BigDecimal;
import java.util.UUID;

public class SimpleWallet implements Wallet {

    private final UUID id;
    private final BigDecimal initialBalance;

    private BigDecimal balance;

    public SimpleWallet(UUID id, BigDecimal balance) {
        this.id = id;
        this.balance = balance;
        initialBalance = balance;
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public BigDecimal getBalance() {
        return balance;
    }

    @Override
    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    @Override
    public BigDecimal getInitialBalance() {
        return initialBalance;
    }
}
