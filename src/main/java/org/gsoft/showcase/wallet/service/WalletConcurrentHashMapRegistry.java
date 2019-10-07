package org.gsoft.showcase.wallet.service;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.gsoft.showcase.wallet.domain.Wallet;
import org.gsoft.showcase.wallet.domain.WalletFactory;
import org.gsoft.showcase.wallet.error.WalletNotFoundException;

public class WalletConcurrentHashMapRegistry implements WalletRegistry {
    private final ConcurrentHashMap<UUID, Wallet> walletMap = new ConcurrentHashMap<>();

    @Override
    public Wallet get(UUID walletId) {
        return Optional.ofNullable(walletMap.get(walletId))
            .orElseThrow(() -> new WalletNotFoundException(walletId));
    }

    @Override
    public Wallet create(BigDecimal initialBalance) {
        while (true) {
            UUID walletId = UUID.randomUUID();

            Wallet wallet = WalletFactory.createWallet(walletId, initialBalance);

            Wallet existingWallet = walletMap.putIfAbsent(walletId, wallet);

            if (existingWallet != null) {
                // should not happen because UUID collision is rare
                continue;
            }

            return wallet;
        }
    }

    @Override
    public void remove(UUID id) {
        walletMap.remove(id);
    }
}
