package org.gsoft.showcase.wallet.service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.gsoft.showcase.wallet.domain.Wallet;

public class WalletStorage {
    private final Map<UUID, Wallet> walletMap = new ConcurrentHashMap<>();

    public Wallet get(UUID id) {
        return walletMap.get(id);
    }

    public void put(Wallet wallet) {
        walletMap.put(wallet.getId(), wallet);
    }

    public void delete(UUID id) {
        walletMap.remove(id);
    }
}
