package org.gsoft.showcase.wallet.error;

public class MethodNotFoundException extends ExceptionWithHttpCode {

    public MethodNotFoundException(String message) {
        super(404, message);
    }
}
