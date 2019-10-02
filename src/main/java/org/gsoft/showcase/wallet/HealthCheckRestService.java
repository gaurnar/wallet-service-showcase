package org.gsoft.showcase.wallet;

import org.gsoft.showcase.wallet.util.routing.RestApiRoutingBuilder;
import org.gsoft.showcase.wallet.util.routing.RestApiRoutingProvider;

public class HealthCheckRestService implements RestApiRoutingProvider {

    @Override
    public void provide(RestApiRoutingBuilder apiRoutingBuilder) {
        apiRoutingBuilder.get("/api/health-check", pathMatcher -> null);
    }
}
