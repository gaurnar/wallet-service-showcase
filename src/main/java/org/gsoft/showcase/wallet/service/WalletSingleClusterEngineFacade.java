package org.gsoft.showcase.wallet.service;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * TODO polish docs
 *
 * For production modify this implementation for cluster mode support.
 * Each node in cluster holds its own engine, here we should implement routing.
 */
public class WalletSingleClusterEngineFacade implements WalletsFacade {

    private final WalletClusterEngine walletEngine;

    public WalletSingleClusterEngineFacade(WalletClusterEngine walletEngine) {
        this.walletEngine = walletEngine;
    }

    @Override
    public void withdraw(UUID walletId, UUID transactionId, BigDecimal amount) {
        walletEngine.withdraw(walletId, transactionId, amount);
    }

    @Override
    public void add(UUID walletId, UUID transactionId, BigDecimal amount) {
        walletEngine.add(walletId, transactionId, amount);
    }

    @Override
    public void createWallet(UUID id, BigDecimal balance) {
        walletEngine.createWallet(id, balance);
    }

    @Override
    public void removeWallet(UUID id) {
        walletEngine.removeWallet(id);
    }

    @Override
    public BigDecimal getBalance(UUID walletId) {
        return walletEngine.getBalance(walletId);
    }
}
