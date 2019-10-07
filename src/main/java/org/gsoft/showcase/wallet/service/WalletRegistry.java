package org.gsoft.showcase.wallet.service;

import java.math.BigDecimal;
import java.util.UUID;
import org.gsoft.showcase.wallet.domain.Wallet;

public interface WalletRegistry {
    Wallet get(UUID id);

    Wallet create(BigDecimal initialBalance);

    void remove(UUID id);
}
