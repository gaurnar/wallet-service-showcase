package org.gsoft.showcase.wallet.resource;

import org.gsoft.showcase.wallet.util.routing.RestApiRoutingBuilder;
import org.gsoft.showcase.wallet.util.routing.RestApiRoutingProvider;

public class HealthCheckResource implements RestApiRoutingProvider {

    @Override
    public void provide(RestApiRoutingBuilder apiRoutingBuilder) {
        apiRoutingBuilder.get("/api/v1/health-check", pathMatcher -> null);
    }
}
