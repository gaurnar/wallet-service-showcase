package org.gsoft.showcase.wallet.service;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import org.gsoft.showcase.wallet.domain.Wallet;
import org.gsoft.showcase.wallet.domain.WalletFactory;
import org.gsoft.showcase.wallet.dto.WalletCreationRequest;
import org.gsoft.showcase.wallet.dto.WalletInfoResponse;
import org.gsoft.showcase.wallet.error.WalletNotFoundException;

public class WalletManager {

    private final WalletStorage storage;

    public WalletManager(WalletStorage storage) {
        this.storage = storage;
    }

    public void createWallet(WalletCreationRequest creationRequest) {
        BigDecimal initialBalance = Optional.ofNullable(creationRequest.getInitialBalance())
            .orElse(BigDecimal.ZERO);
        Wallet wallet = WalletFactory.createWallet(creationRequest.getId(), initialBalance);
        storage.put(wallet);
    }

    public WalletInfoResponse getWalletInfo(UUID id) {
        Wallet wallet = Optional.ofNullable(storage.get(id))
            .orElseThrow(() -> new WalletNotFoundException(id));
        return new WalletInfoResponse(id, wallet.getBalance());
    }

    public void removeWallet(UUID id) {
        storage.delete(id);
    }
}
