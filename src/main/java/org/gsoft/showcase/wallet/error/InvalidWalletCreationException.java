package org.gsoft.showcase.wallet.error;

public class InvalidWalletCreationException extends ExceptionWithHttpCode {

    public InvalidWalletCreationException(String message) {
        super(400, message);
    }
}
