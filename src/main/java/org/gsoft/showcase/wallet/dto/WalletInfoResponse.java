package org.gsoft.showcase.wallet.dto;

import java.math.BigDecimal;
import java.util.UUID;

public final class WalletInfoResponse {

    private UUID id;

    private BigDecimal balance;

    public WalletInfoResponse(UUID id, BigDecimal balance) {
        this.id = id;
        this.balance = balance;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
