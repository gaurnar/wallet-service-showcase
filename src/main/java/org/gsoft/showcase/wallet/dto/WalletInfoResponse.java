package org.gsoft.showcase.wallet.dto;

import java.util.UUID;

/**
 * Using strings for balance to avoid hassle with losing precision
 * in clients (e.g. if double is used for representing JSON floating point numbers)
 */
public final class WalletInfoResponse {

    private UUID id;

    private String balance;

    public WalletInfoResponse(UUID id, String balance) {
        this.id = id;
        this.balance = balance;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }
}
