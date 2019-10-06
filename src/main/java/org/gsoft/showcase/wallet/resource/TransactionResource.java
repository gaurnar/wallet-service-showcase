package org.gsoft.showcase.wallet.resource;

import org.gsoft.showcase.wallet.dto.TransactionRequest;
import org.gsoft.showcase.wallet.service.TransactionEngine;
import org.gsoft.showcase.wallet.util.routing.RestApiRoutingBuilder;
import org.gsoft.showcase.wallet.util.routing.RestApiRoutingProvider;

public class TransactionResource implements RestApiRoutingProvider {

    private final TransactionEngine engine;

    public TransactionResource(TransactionEngine engine) {
        this.engine = engine;
    }

    @Override
    public void provide(RestApiRoutingBuilder apiRoutingBuilder) {
        apiRoutingBuilder.post("/api/v1/transaction", TransactionRequest.class,
                               (pathMatcher, body) -> {
                                   engine.process(body);
                                   return null;
                               });
    }
}
