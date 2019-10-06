package org.gsoft.showcase.wallet.dto;

import java.util.UUID;

/**
 * Using strings for initialBalance to avoid hassle with losing precision
 * in clients (e.g. if double is used for representing JSON floating point numbers)
 */
public final class WalletCreationRequest {
    private UUID id;
    private String initialBalance;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getInitialBalance() {
        return initialBalance;
    }

    public void setInitialBalance(String initialBalance) {
        this.initialBalance = initialBalance;
    }
}
