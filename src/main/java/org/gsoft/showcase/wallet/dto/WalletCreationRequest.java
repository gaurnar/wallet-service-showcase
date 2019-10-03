package org.gsoft.showcase.wallet.dto;

import java.math.BigDecimal;
import java.util.UUID;

public final class WalletCreationRequest {
    private UUID id;
    private BigDecimal initialBalance;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public BigDecimal getInitialBalance() {
        return initialBalance;
    }

    public void setInitialBalance(BigDecimal initialBalance) {
        this.initialBalance = initialBalance;
    }
}
