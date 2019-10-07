package org.gsoft.showcase.wallet.domain;

import java.math.BigDecimal;
import java.util.UUID;

public final class WalletFactory {

    public static Wallet createWallet(UUID id, BigDecimal initialBalance) {
        return new SimpleWallet(id, initialBalance);
    }
}
