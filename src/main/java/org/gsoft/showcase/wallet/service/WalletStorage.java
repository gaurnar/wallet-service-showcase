package org.gsoft.showcase.wallet.service;

import java.util.UUID;
import org.gsoft.showcase.wallet.domain.Wallet;

/**
 * TODO rework to be easily modifiable for actual persistence
 */
public interface WalletStorage {
    Wallet get(UUID id);

    void put(Wallet wallet);

    void remove(UUID id);
}
