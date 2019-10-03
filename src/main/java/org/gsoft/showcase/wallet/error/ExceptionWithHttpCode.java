package org.gsoft.showcase.wallet.error;

public class ExceptionWithHttpCode extends RuntimeException {

    private final int httpCode;

    public ExceptionWithHttpCode(int httpCode, String message, Throwable cause) {
        super(message, cause);
        this.httpCode = httpCode;
    }

    public ExceptionWithHttpCode(int httpCode, String message) {
        this(httpCode, message, null);
    }

    public int getHttpCode() {
        return httpCode;
    }
}
