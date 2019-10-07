package org.gsoft.showcase.wallet.service;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import org.gsoft.showcase.wallet.domain.Wallet;
import org.gsoft.showcase.wallet.dto.WalletCreationRequest;
import org.gsoft.showcase.wallet.dto.WalletInfoResponse;

public class WalletManager {

    private final WalletRegistry registry;

    public WalletManager(WalletRegistry registry) {
        this.registry = registry;
    }

    public WalletInfoResponse createWallet(WalletCreationRequest creationRequest) {
        BigDecimal initialBalance = Optional.ofNullable(creationRequest.getInitialBalance())
            .map(BigDecimal::new)
            .orElse(BigDecimal.ZERO);
        Wallet wallet = registry.create(initialBalance);
        return new WalletInfoResponse(wallet.getId(), wallet.getBalance().toPlainString());
    }

    public WalletInfoResponse getWalletInfo(UUID id) {
        BigDecimal balance = registry.get(id).getBalance();
        return new WalletInfoResponse(id, balance.toPlainString());
    }

    public void removeWallet(UUID id) {
        registry.remove(id);
    }
}
