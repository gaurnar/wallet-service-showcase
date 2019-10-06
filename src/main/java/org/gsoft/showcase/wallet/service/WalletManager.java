package org.gsoft.showcase.wallet.service;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import org.gsoft.showcase.wallet.dto.WalletCreationRequest;
import org.gsoft.showcase.wallet.dto.WalletInfoResponse;

public class WalletManager {

    private final WalletsFacade walletsFacade;

    public WalletManager(WalletsFacade walletsFacade) {
        this.walletsFacade = walletsFacade;
    }

    public void createWallet(WalletCreationRequest creationRequest) {
        BigDecimal initialBalance = Optional.ofNullable(creationRequest.getInitialBalance())
            .map(BigDecimal::new)
            .orElse(BigDecimal.ZERO);
        walletsFacade.createWallet(creationRequest.getId(), initialBalance);
    }

    public WalletInfoResponse getWalletInfo(UUID id) {
        BigDecimal balance = walletsFacade.getBalance(id);
        return new WalletInfoResponse(id, balance.toPlainString());
    }

    public void removeWallet(UUID id) {
        walletsFacade.removeWallet(id);
    }
}
