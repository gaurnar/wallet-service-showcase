package org.gsoft.showcase.wallet.service;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.gsoft.showcase.wallet.domain.Wallet;
import org.gsoft.showcase.wallet.domain.WalletFactory;
import org.gsoft.showcase.wallet.error.InvalidWalletCreationException;
import org.gsoft.showcase.wallet.error.WalletNotFoundException;

public class WalletConcurrentHashMapRegistry implements WalletRegistry {
    private final ConcurrentHashMap<UUID, Wallet> walletMap = new ConcurrentHashMap<>();

    public Wallet get(UUID walletId) {
        return Optional.ofNullable(walletMap.get(walletId))
            .orElseThrow(() -> new WalletNotFoundException(walletId));
    }

    public void create(UUID walletId, BigDecimal initialBalance) {
        Wallet existingWallet = walletMap.putIfAbsent(walletId, WalletFactory.createWallet(walletId, initialBalance));

        if (existingWallet != null) {
            if (!existingWallet.getInitialBalance().equals(initialBalance)) {
                throw new InvalidWalletCreationException("different initial balance for known wallet");
            }
        }
    }

    public void remove(UUID id) {
        walletMap.remove(id);
    }
}
