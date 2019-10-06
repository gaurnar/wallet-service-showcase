package org.gsoft.showcase.wallet.service;

import java.math.BigDecimal;
import java.util.UUID;

public interface WalletsFacade {

    void withdraw(UUID walletId, UUID transactionId, BigDecimal amount);

    void add(UUID walletId, UUID transactionId, BigDecimal amount);

    void createWallet(UUID id, BigDecimal balance);

    void removeWallet(UUID id);

    BigDecimal getBalance(UUID walletId);
}
