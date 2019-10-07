package org.gsoft.showcase.wallet.domain;

import java.math.BigDecimal;
import java.util.UUID;

public interface Wallet {

    UUID getId();

    BigDecimal getBalance();

    void setBalance(BigDecimal balance);

    BigDecimal getInitialBalance();
}
