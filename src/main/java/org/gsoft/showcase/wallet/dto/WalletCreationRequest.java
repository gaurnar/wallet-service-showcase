package org.gsoft.showcase.wallet.dto;

/**
 * Using strings for initialBalance to avoid hassle with losing precision
 * in clients (e.g. if double is used for representing JSON floating point numbers)
 */
public final class WalletCreationRequest {
    private String initialBalance;

    public String getInitialBalance() {
        return initialBalance;
    }

    public void setInitialBalance(String initialBalance) {
        this.initialBalance = initialBalance;
    }
}
