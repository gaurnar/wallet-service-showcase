package org.gsoft.showcase.wallet.service;

import org.gsoft.showcase.wallet.dto.TransactionRequest;

public interface TransactionProcessor {

    void process(TransactionRequest transactionRequest);
}
