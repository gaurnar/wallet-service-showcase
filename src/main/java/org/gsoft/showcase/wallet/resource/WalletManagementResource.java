package org.gsoft.showcase.wallet.resource;

import java.util.UUID;
import java.util.regex.Pattern;
import org.gsoft.showcase.wallet.dto.WalletCreationRequest;
import org.gsoft.showcase.wallet.service.WalletManager;
import org.gsoft.showcase.wallet.util.routing.RestApiRoutingBuilder;
import org.gsoft.showcase.wallet.util.routing.RestApiRoutingProvider;

public class WalletManagementResource implements RestApiRoutingProvider {

    private static final Pattern WALLET_PATH_PATTERN = Pattern.compile("/api/v1/wallet/([^/]+)");

    private final WalletManager manager;

    public WalletManagementResource(WalletManager manager) {
        this.manager = manager;
    }

    @Override
    public void provide(RestApiRoutingBuilder apiRoutingBuilder) {
        apiRoutingBuilder
            .get(WALLET_PATH_PATTERN, pathMatcher -> manager.getWalletInfo(UUID.fromString(pathMatcher.group(1))))
            .post("/api/v1/wallet", WalletCreationRequest.class,
                  (pathMatcher, body) -> {
                      manager.createWallet(body);
                      return null;
                  })
            .delete(WALLET_PATH_PATTERN, pathMatcher -> {
                manager.removeWallet(UUID.fromString(pathMatcher.group(1)));
                return null;
            });
    }
}
