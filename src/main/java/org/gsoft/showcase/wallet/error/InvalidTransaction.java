package org.gsoft.showcase.wallet.error;

public class InvalidTransaction extends ExceptionWithHttpCode {

    public InvalidTransaction(String message) {
        super(400, message);
    }
}
