package org.gsoft.showcase.wallet;

import static io.restassured.RestAssured.delete;
import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.text.IsEmptyString.emptyString;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import org.junit.Test;

/**
 * TODO create utility methods
 * TODO move URLS to constants
 */
public class WalletManagementApiTest extends BaseApplicationTest {

    @Test
    public void should_create_wallet_without_specified_balance() {
        UUID walletId = UUID.randomUUID();

        given().body("{\"id\":\"" + walletId + "\"}")
            .post("/api/v1/wallet")
            .then().statusCode(200);

        get("/api/v1/wallet/" + walletId)
            .then().statusCode(200)
            .body("balance", equalTo(0));
    }

    @Test
    public void should_create_wallet_with_given_balance() {
        UUID walletId = UUID.randomUUID();

        given().body("{\"id\": \"" + walletId + "\", \"initialBalance\": 10}")
            .post("/api/v1/wallet")
            .then().statusCode(200);

        get("/api/v1/wallet/" + walletId)
            .then().statusCode(200)
            .body("balance", equalTo(10));
    }

    @Test
    public void should_not_create_wallet_with_non_uuid_id() {
        given().body("{\"id\": \"foobar\"}")
            .post("/api/v1/wallet")
            .then()
            .statusCode(400)
            .body("message", is(not(emptyString())));
    }

    @Test
    public void should_return_404_when_asked_for_balance_unknown_wallet() {
        get("/api/v1/wallet/" + UUID.randomUUID())
            .then().statusCode(404);
    }

    @Test
    public void should_create_wallet_concurrent_requests() throws Exception {
        // TODO move to util classes for reuse

        final int threadsCount = 10;

        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch finishLatch = new CountDownLatch(threadsCount);

        List<UUID> uuids = Collections.synchronizedList(new ArrayList<>());

        for (int i = 0; i < threadsCount; i++) {
            new Thread(() -> {
                UUID walletId = UUID.randomUUID();

                try {
                    startLatch.await();
                } catch (InterruptedException ignored) {
                }

                try {
                    given().body("{\"id\":\"" + walletId + "\"}")
                        .post("/api/v1/wallet")
                        .then().statusCode(200);

                    get("/api/v1/wallet/" + walletId)
                        .then().statusCode(200);

                    uuids.add(walletId);
                } finally {
                    finishLatch.countDown();
                }
            }).start();
        }

        startLatch.countDown();
        finishLatch.await();

        assertEquals(threadsCount, uuids.size());
    }

    @Test
    public void should_remove_wallet() {
        UUID walletId = UUID.randomUUID();

        given().body("{\"id\":\"" + walletId + "\"}")
            .post("/api/v1/wallet")
            .then().statusCode(200);

        delete("/api/v1/wallet/" + walletId)
            .then().statusCode(200);

        get("/api/v1/wallet/" + walletId)
            .then().statusCode(404);
    }
}
