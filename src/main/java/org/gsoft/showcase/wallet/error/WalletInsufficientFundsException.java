package org.gsoft.showcase.wallet.error;

public class WalletInsufficientFundsException extends ExceptionWithHttpCode {

    public WalletInsufficientFundsException() {
        super(400, "insufficient funds");
    }
}
