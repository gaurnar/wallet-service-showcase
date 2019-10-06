package org.gsoft.showcase.wallet.domain;

import java.math.BigDecimal;
import java.util.UUID;
import org.gsoft.showcase.wallet.error.WalletInsufficientFundsException;

/**
 * TODO rework for persistence
 */
public class Wallet {
    private final UUID id;
    private volatile BigDecimal balance;

    Wallet(UUID id, BigDecimal balance) {
        this.id = id;
        this.balance = balance;
    }

    public synchronized void withdraw(BigDecimal amount) {
        if (balance.compareTo(amount) < 0) {
            throw new WalletInsufficientFundsException();
        }
        balance = balance.subtract(amount).stripTrailingZeros();
    }

    public synchronized void add(BigDecimal amount) {
        balance = balance.add(amount).stripTrailingZeros();
    }

    public UUID getId() {
        return id;
    }

    public BigDecimal getBalance() {
        return balance;
    }
}
