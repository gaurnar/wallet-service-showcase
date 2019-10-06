package org.gsoft.showcase.wallet.dto;

import java.util.UUID;

/**
 * Using strings for amount to avoid hassle with losing precision
 * in clients (e.g. if double is used for representing JSON floating point numbers)
 */
public final class TransactionRequest {

    private UUID id;
    private UUID from;
    private UUID to;
    private String amount;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getFrom() {
        return from;
    }

    public void setFrom(UUID from) {
        this.from = from;
    }

    public UUID getTo() {
        return to;
    }

    public void setTo(UUID to) {
        this.to = to;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
