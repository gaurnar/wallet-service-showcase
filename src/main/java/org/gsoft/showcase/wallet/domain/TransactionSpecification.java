package org.gsoft.showcase.wallet.domain;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public final class TransactionSpecification {
    private final UUID id;
    private final UUID from;
    private final UUID to;
    private final BigDecimal amount;

    public TransactionSpecification(UUID id, UUID from, UUID to, BigDecimal amount) {
        this.id = id;
        this.from = from;
        this.to = to;
        this.amount = amount;
    }

    public UUID getId() {
        return id;
    }

    public UUID getFrom() {
        return from;
    }

    public UUID getTo() {
        return to;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TransactionSpecification that = (TransactionSpecification) o;
        return id.equals(that.id) &&
            from.equals(that.from) &&
            to.equals(that.to) &&
            amount.equals(that.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, from, to, amount);
    }
}
