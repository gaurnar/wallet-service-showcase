package org.gsoft.showcase.wallet;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static org.gsoft.showcase.wallet.util.ApiTestUtil.createWalletWithBalance;
import static org.gsoft.showcase.wallet.util.ApiTestUtil.getWalletBalance;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import org.gsoft.showcase.wallet.util.ParallelActionRunner;
import org.junit.Test;

public class WalletConcurrentApiTest extends BaseApplicationTest {

    @Test
    public void should_process_concurrent_transactions() {
        final int numberOfIterations = 100;

        for (int i = 0; i < numberOfIterations; i++) {
            processConcurrentTransactions();
        }
    }

    private void processConcurrentTransactions() {
        final int numberOfThreads = 10;

        UUID aWalletId = UUID.randomUUID();
        UUID bWalletId = UUID.randomUUID();

        createWalletWithBalance(aWalletId, BigDecimal.valueOf(100));
        createWalletWithBalance(bWalletId, BigDecimal.valueOf(0));

        AtomicBoolean error = new AtomicBoolean(false);

        ParallelActionRunner parallelActionRunner =
            new ParallelActionRunner(numberOfThreads, () -> {
                UUID transactionId = UUID.randomUUID();

                try {
                    given()
                        .body("{\"id\": \"" + transactionId + "\", "
                                  + "\"from\": \"" + aWalletId + "\", "
                                  + "\"to\": \"" + bWalletId + "\", "
                                  + "\"amount\": \"2\"}")
                        .post("/api/v1/transaction")
                        .then().statusCode(200);
                } catch (Exception e) {
                    error.set(true);
                    throw e;
                }
            });

        parallelActionRunner.run();

        assertFalse(error.get());

        BigDecimal aWalletBalance = getWalletBalance(aWalletId);
        BigDecimal bWalletBalance = getWalletBalance(bWalletId);

        assertEquals(BigDecimal.valueOf(100 - 2 * numberOfThreads), aWalletBalance);
        assertEquals(BigDecimal.valueOf(2 * numberOfThreads), bWalletBalance);
    }

    @Test
    public void should_process_idempotent_concurrent_requests() {
        final int numberOfIterations = 100;

        for (int i = 0; i < numberOfIterations; i++) {
            processIdempotentConcurrentRequests();
        }
    }

    private void processIdempotentConcurrentRequests() {
        final int numberOfThreads = 10;

        UUID transactionId = UUID.randomUUID();

        UUID aWalletId = UUID.randomUUID();
        UUID bWalletId = UUID.randomUUID();

        createWalletWithBalance(aWalletId, BigDecimal.valueOf(100));
        createWalletWithBalance(bWalletId, BigDecimal.valueOf(0));

        AtomicBoolean error = new AtomicBoolean(false);

        ParallelActionRunner parallelActionRunner =
            new ParallelActionRunner(numberOfThreads, () -> {
                try {
                    given()
                        .body("{\"id\": \"" + transactionId + "\", "
                                  + "\"from\": \"" + aWalletId + "\", "
                                  + "\"to\": \"" + bWalletId + "\", "
                                  + "\"amount\": \"10\"}")
                        .post("/api/v1/transaction")
                        .then().statusCode(200);
                } catch (Exception e) {
                    error.set(true);
                    throw e;
                }
            });

        parallelActionRunner.run();

        assertFalse(error.get());

        BigDecimal aWalletBalance = getWalletBalance(aWalletId);
        BigDecimal bWalletBalance = getWalletBalance(bWalletId);

        assertEquals(BigDecimal.valueOf(90), aWalletBalance);
        assertEquals(BigDecimal.valueOf(10), bWalletBalance);
    }

    @Test
    public void should_process_concurrent_opposite_transactions() throws Exception {
        final int numberOfIterations = 100;

        for (int i = 0; i < numberOfIterations; i++) {
            processConcurrentOppositeTransactions();
        }
    }

    private void processConcurrentOppositeTransactions() throws Exception {
        UUID aTransactionId = UUID.randomUUID();
        UUID bTransactionId = UUID.randomUUID();

        UUID aWalletId = UUID.randomUUID();
        UUID bWalletId = UUID.randomUUID();

        createWalletWithBalance(aWalletId, BigDecimal.valueOf(5));
        createWalletWithBalance(bWalletId, BigDecimal.valueOf(7));

        CountDownLatch startLatch = new CountDownLatch(1);

        AtomicBoolean errorFlag = new AtomicBoolean(false);

        Thread firstThread = new Thread(() -> sendTransactionRequestAfterLatch(aTransactionId, aWalletId, bWalletId,
                                                                           BigDecimal.valueOf(3), startLatch, errorFlag));

        Thread secondThread = new Thread(() -> sendTransactionRequestAfterLatch(bTransactionId, bWalletId, aWalletId,
                                                                            BigDecimal.valueOf(5), startLatch, errorFlag));

        firstThread.start();
        secondThread.start();

        startLatch.countDown();

        firstThread.join();
        secondThread.join();

        assertFalse(errorFlag.get());

        BigDecimal aWalletBalance = getWalletBalance(aWalletId);
        BigDecimal bWalletBalance = getWalletBalance(bWalletId);

        assertEquals(BigDecimal.valueOf(7), aWalletBalance);
        assertEquals(BigDecimal.valueOf(5), bWalletBalance);
    }

    private void sendTransactionRequestAfterLatch(UUID transactionId, UUID from, UUID to,
                                                  BigDecimal amount, CountDownLatch latch,
                                                  AtomicBoolean errorFlag) {
        try {
            latch.await();
        } catch (InterruptedException ignored) {
        }

        try {
            given()
                .body("{\"id\": \"" + transactionId + "\", "
                          + "\"from\": \"" + from + "\", "
                          + "\"to\": \"" + to + "\", "
                          + "\"amount\": \"" + amount + "\"}")
                .post("/api/v1/transaction")
                .then().statusCode(200);
        } catch (Exception e) {
            errorFlag.set(true);
            throw e;
        }
    }

    @Test
    public void should_create_wallet_concurrent_requests() {
        final int numberOfIterations = 100;

        for (int i = 0; i < numberOfIterations; i++) {
            createWalletConcurrentRequests();
        }
    }

    private void createWalletConcurrentRequests() {
        final int threadsCount = 10;

        List<UUID> uuids = Collections.synchronizedList(new ArrayList<>());

        ParallelActionRunner parallelActionRunner =
            new ParallelActionRunner(threadsCount, () -> {
                UUID walletId = UUID.randomUUID();

                given().body("{\"id\":\"" + walletId + "\"}")
                    .post("/api/v1/wallet")
                    .then().statusCode(200);

                get("/api/v1/wallet/" + walletId)
                    .then().statusCode(200);

                uuids.add(walletId);
            });

        parallelActionRunner.run();

        assertEquals(threadsCount, uuids.size());
    }
}
