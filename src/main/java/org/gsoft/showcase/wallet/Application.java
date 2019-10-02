package org.gsoft.showcase.wallet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import org.gsoft.showcase.wallet.util.routing.RestApiRouter;
import org.gsoft.showcase.wallet.util.routing.RestApiRoutingBuilder;
import org.gsoft.showcase.wallet.util.routing.RestApiRoutingProvider;

public class Application {
    private final HttpServer httpServer;

    public Application(InetSocketAddress address) throws IOException {
        httpServer = HttpServer.create(address, 0);

        ObjectMapper objectMapper = buildObjectMapper();

        RestApiRoutingBuilder routingBuilder = new RestApiRoutingBuilder(objectMapper);

        initializeServicesAndBuildRoutingProviders(objectMapper)
            .forEach(provider -> provider.provide(routingBuilder));

        RestApiRouter router = routingBuilder.build();

        httpServer.createContext("/", router);
        httpServer.setExecutor(Executors.newFixedThreadPool(5)); // TODO another executor
    }

    public void start() {
        httpServer.start();
    }

    public void stop() {
        httpServer.stop(0);
    }

    public InetSocketAddress getAddress() {
        return httpServer.getAddress();
    }

    private List<RestApiRoutingProvider> initializeServicesAndBuildRoutingProviders(ObjectMapper objectMapper) {
        return Collections.singletonList(new HealthCheckRestService());
    }

    private ObjectMapper buildObjectMapper() {
        return new ObjectMapper();
    }
}
