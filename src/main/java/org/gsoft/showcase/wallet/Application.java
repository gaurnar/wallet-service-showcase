package org.gsoft.showcase.wallet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.gsoft.showcase.wallet.resource.HealthCheckResource;
import org.gsoft.showcase.wallet.resource.TransactionResource;
import org.gsoft.showcase.wallet.resource.WalletManagementResource;
import org.gsoft.showcase.wallet.service.TransactionWalletSynchronizedProcessor;
import org.gsoft.showcase.wallet.service.WalletConcurrentHashMapRegistry;
import org.gsoft.showcase.wallet.service.WalletManager;
import org.gsoft.showcase.wallet.util.routing.RestApiRouter;
import org.gsoft.showcase.wallet.util.routing.RestApiRoutingBuilder;
import org.gsoft.showcase.wallet.util.routing.RestApiRoutingProvider;

public class Application {
    private final HttpServer httpServer;

    public Application(InetSocketAddress address) throws IOException {
        initializeLogging();

        httpServer = HttpServer.create(address, 0);

        ObjectMapper objectMapper = buildObjectMapper();

        RestApiRoutingBuilder routingBuilder = new RestApiRoutingBuilder(objectMapper);

        initializeServicesAndBuildRoutingProviders()
            .forEach(provider -> provider.provide(routingBuilder));

        RestApiRouter router = routingBuilder.build();

        httpServer.createContext("/", router);

        configureRequestHandlingExecutor(httpServer);
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
        WalletConcurrentHashMapRegistry registry = new WalletConcurrentHashMapRegistry();
        WalletManager walletManager = new WalletManager(registry);
        TransactionWalletSynchronizedProcessor transactionProcessor = new TransactionWalletSynchronizedProcessor(registry);

        return Arrays.asList(
            new HealthCheckResource(),
            new WalletManagementResource(walletManager),
            new TransactionResource(transactionProcessor)
        );
    }

    private ObjectMapper buildObjectMapper() {
        return new ObjectMapper();
    }

    private void initializeLogging() {
        ConsoleAppender console = new ConsoleAppender();

        String pattern = "%d %p [%c] %m%n";
        console.setLayout(new PatternLayout(pattern));
        console.setThreshold(Level.INFO); // TODO configure
        console.activateOptions();

        // TODO add file logging
        Logger.getRootLogger().addAppender(console);
    }

    private void configureRequestHandlingExecutor(HttpServer httpServer) {
        // TODO make configurable
        BlockingQueue<Runnable> executorQueue = new LinkedBlockingQueue<>(100);
        Executor executor = new ThreadPoolExecutor(5, 50,
                                                   1, TimeUnit.MINUTES,
                                                   executorQueue);
        httpServer.setExecutor(executor);
    }
}
