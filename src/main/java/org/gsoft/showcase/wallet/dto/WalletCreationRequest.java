package org.gsoft.showcase.wallet.dto;

import java.math.BigDecimal;

public final class WalletCreationRequest {
    private BigDecimal initialAmount;

    public WalletCreationRequest(BigDecimal initialAmount) {
        this.initialAmount = initialAmount;
    }

    public BigDecimal getInitialAmount() {
        return initialAmount;
    }

    public void setInitialAmount(BigDecimal initialAmount) {
        this.initialAmount = initialAmount;
    }
}
