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
    private final BigDecimal initialBalance;

    Wallet(UUID id, BigDecimal balance) {
        this.id = id;
        this.balance = balance;
        initialBalance = balance;
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

    public BigDecimal getInitialBalance() {
        return initialBalance;
    }
}
