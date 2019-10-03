package org.gsoft.showcase.wallet.error;

import java.util.UUID;

public class WalletNotFoundException extends ExceptionWithHttpCode {

    public WalletNotFoundException(UUID id) {
        super(404, "wallet not found: " + id);
    }
}
