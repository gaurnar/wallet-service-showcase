package org.gsoft.showcase.wallet.error;

public class RequestBodyIsNotExpectedException extends ExceptionWithHttpCode {

    public RequestBodyIsNotExpectedException() {
        super(400, "request body is not expected");
    }
}
