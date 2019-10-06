package org.gsoft.showcase.wallet.service;

import java.math.BigDecimal;
import org.gsoft.showcase.wallet.dto.TransactionRequest;

public class TransactionEngine {

    private final WalletsFacade walletsFacade;

    public TransactionEngine(WalletsFacade walletsFacade) {
        this.walletsFacade = walletsFacade;
    }

    public void process(TransactionRequest request) {
        walletsFacade.withdraw(request.getFrom(), request.getId(), new BigDecimal(request.getAmount()));
        walletsFacade.add(request.getTo(), request.getId(), new BigDecimal(request.getAmount()));
    }
}
