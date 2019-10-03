package org.gsoft.showcase.wallet.dto;

public final class ErrorVM {

    private String message;

    public ErrorVM(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
