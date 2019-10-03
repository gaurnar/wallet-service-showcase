package org.gsoft.showcase.wallet.error;

public class InvalidJsonException extends ExceptionWithHttpCode {

    public InvalidJsonException(String message, Throwable cause) {
        super(400, message, cause);
    }
}
