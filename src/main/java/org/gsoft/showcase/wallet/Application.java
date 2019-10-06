package org.gsoft.showcase.wallet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import org.gsoft.showcase.wallet.resource.HealthCheckResource;
import org.gsoft.showcase.wallet.resource.TransactionResource;
import org.gsoft.showcase.wallet.resource.WalletManagementResource;
import org.gsoft.showcase.wallet.service.TransactionEngine;
import org.gsoft.showcase.wallet.service.WalletClusterEngine;
import org.gsoft.showcase.wallet.service.WalletInMemoryStorage;
import org.gsoft.showcase.wallet.service.WalletManager;
import org.gsoft.showcase.wallet.service.WalletSingleClusterEngineFacade;
import org.gsoft.showcase.wallet.service.WalletStorage;
import org.gsoft.showcase.wallet.util.routing.RestApiRouter;
import org.gsoft.showcase.wallet.util.routing.RestApiRoutingBuilder;
import org.gsoft.showcase.wallet.util.routing.RestApiRoutingProvider;

public class Application {
    private final HttpServer httpServer;

    public Application(InetSocketAddress address) throws IOException {
        httpServer = HttpServer.create(address, 0);

        ObjectMapper objectMapper = buildObjectMapper();

        RestApiRoutingBuilder routingBuilder = new RestApiRoutingBuilder(objectMapper);

        initializeServicesAndBuildRoutingProviders()
            .forEach(provider -> provider.provide(routingBuilder));

        RestApiRouter router = routingBuilder.build();

        httpServer.createContext("/", router);

        // TODO another executor
        // TODO make configurable
        httpServer.setExecutor(Executors.newFixedThreadPool(10));
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

    private List<RestApiRoutingProvider> initializeServicesAndBuildRoutingProviders() {
        WalletStorage storage = new WalletInMemoryStorage();
        WalletClusterEngine walletEngine = new WalletClusterEngine(storage);
        WalletSingleClusterEngineFacade facade = new WalletSingleClusterEngineFacade(walletEngine);
        WalletManager walletManager = new WalletManager(facade);
        TransactionEngine engine = new TransactionEngine(facade);

        return Arrays.asList(
            new HealthCheckResource(),
            new WalletManagementResource(walletManager),
            new TransactionResource(engine)
        );
    }

    private ObjectMapper buildObjectMapper() {
        return new ObjectMapper();
    }
}
