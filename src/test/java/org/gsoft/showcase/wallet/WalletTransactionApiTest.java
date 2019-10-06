package org.gsoft.showcase.wallet;

import static io.restassured.RestAssured.given;
import static org.gsoft.showcase.wallet.util.ApiTestUtil.createWalletWithBalance;
import static org.gsoft.showcase.wallet.util.ApiTestUtil.getWalletBalance;
import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.UUID;
import org.junit.Test;

public class WalletTransactionApiTest extends BaseApplicationTest {

    @Test
    public void should_transfer_money_enough_funds() {
        UUID transactionId = UUID.randomUUID();
        UUID aWalletId = UUID.randomUUID();
        UUID bWalletId = UUID.randomUUID();

        createWalletWithBalance(aWalletId, BigDecimal.valueOf(10));
        createWalletWithBalance(bWalletId, BigDecimal.valueOf(0));

        given()
            .body("{\"id\": \"" + transactionId + "\", "
                      + "\"from\": \"" + aWalletId + "\", "
                      + "\"to\": \"" + bWalletId + "\", "
                      + "\"amount\": \"9.85\"}")
            .post("/api/v1/transaction")
            .then().statusCode(200);

        BigDecimal aWalletBalance = getWalletBalance(aWalletId);
        BigDecimal bWalletBalance = getWalletBalance(bWalletId);

        assertEquals(BigDecimal.valueOf(0.15), aWalletBalance);
        assertEquals(BigDecimal.valueOf(9.85), bWalletBalance);
    }

    @Test
    public void should_return_error_insufficient_funds() {
        UUID transactionId = UUID.randomUUID();
        UUID aWalletId = UUID.randomUUID();
        UUID bWalletId = UUID.randomUUID();

        createWalletWithBalance(aWalletId, BigDecimal.valueOf(1));
        createWalletWithBalance(bWalletId, BigDecimal.valueOf(0));

        given()
            .body("{\"id\": \"" + transactionId + "\", "
                      + "\"from\": \"" + aWalletId + "\", "
                      + "\"to\": \"" + bWalletId + "\", "
                      + "\"amount\": \"1.5\"}")
            .post("/api/v1/transaction")
            .then().statusCode(400);

        BigDecimal aWalletBalance = getWalletBalance(aWalletId);
        BigDecimal bWalletBalance = getWalletBalance(bWalletId);

        assertEquals(BigDecimal.valueOf(1), aWalletBalance);
        assertEquals(BigDecimal.valueOf(0), bWalletBalance);
    }

    @Test
    public void transaction_should_be_idempotent() {
        UUID transactionId = UUID.randomUUID();
        UUID aWalletId = UUID.randomUUID();
        UUID bWalletId = UUID.randomUUID();

        createWalletWithBalance(aWalletId, BigDecimal.valueOf(10));
        createWalletWithBalance(bWalletId, BigDecimal.valueOf(0));

        final String body = "{\"id\": \"" + transactionId + "\", "
            + "\"from\": \"" + aWalletId + "\", "
            + "\"to\": \"" + bWalletId + "\", "
            + "\"amount\": \"3\"}";

        given().body(body).post("/api/v1/transaction")
            .then().statusCode(200);

        given().body(body).post("/api/v1/transaction")
            .then().statusCode(200);

        BigDecimal aWalletBalance = getWalletBalance(aWalletId);
        BigDecimal bWalletBalance = getWalletBalance(bWalletId);

        assertEquals(BigDecimal.valueOf(7), aWalletBalance);
        assertEquals(BigDecimal.valueOf(3), bWalletBalance);
    }

    @Test
    public void should_return_400_on_same_transaction_id_different_amount() {
        UUID transactionId = UUID.randomUUID();
        UUID aWalletId = UUID.randomUUID();
        UUID bWalletId = UUID.randomUUID();

        createWalletWithBalance(aWalletId, BigDecimal.valueOf(10));
        createWalletWithBalance(bWalletId, BigDecimal.valueOf(0));

        given()
            .body("{\"id\": \"" + transactionId + "\", "
                      + "\"from\": \"" + aWalletId + "\", "
                      + "\"to\": \"" + bWalletId + "\", "
                      + "\"amount\": \"5\"}")
            .post("/api/v1/transaction")
            .then().statusCode(200);

        given()
            .body("{\"id\": \"" + transactionId + "\", "
                      + "\"from\": \"" + aWalletId + "\", "
                      + "\"to\": \"" + bWalletId + "\", "
                      + "\"amount\": \"3\"}")
            .post("/api/v1/transaction")
            .then().statusCode(400);
    }

    @Test
    public void should_return_400_on_same_transaction_id_different_from() {
        UUID transactionId = UUID.randomUUID();
        UUID aWalletId = UUID.randomUUID();
        UUID bWalletId = UUID.randomUUID();
        UUID cWalletId = UUID.randomUUID();

        createWalletWithBalance(aWalletId, BigDecimal.valueOf(10));
        createWalletWithBalance(bWalletId, BigDecimal.valueOf(0));
        createWalletWithBalance(cWalletId, BigDecimal.valueOf(10));

        given()
            .body("{\"id\": \"" + transactionId + "\", "
                      + "\"from\": \"" + aWalletId + "\", "
                      + "\"to\": \"" + bWalletId + "\", "
                      + "\"amount\": \"5\"}")
            .post("/api/v1/transaction")
            .then().statusCode(200);

        given()
            .body("{\"id\": \"" + transactionId + "\", "
                      + "\"from\": \"" + cWalletId + "\", "
                      + "\"to\": \"" + bWalletId + "\", "
                      + "\"amount\": \"5\"}")
            .post("/api/v1/transaction")
            .then().statusCode(400);
    }

    @Test
    public void should_return_400_on_same_transaction_id_different_to() {
        UUID transactionId = UUID.randomUUID();
        UUID aWalletId = UUID.randomUUID();
        UUID bWalletId = UUID.randomUUID();
        UUID cWalletId = UUID.randomUUID();

        createWalletWithBalance(aWalletId, BigDecimal.valueOf(10));
        createWalletWithBalance(bWalletId, BigDecimal.valueOf(0));
        createWalletWithBalance(cWalletId, BigDecimal.valueOf(10));

        given()
            .body("{\"id\": \"" + transactionId + "\", "
                      + "\"from\": \"" + aWalletId + "\", "
                      + "\"to\": \"" + bWalletId + "\", "
                      + "\"amount\": \"5\"}")
            .post("/api/v1/transaction")
            .then().statusCode(200);

        given()
            .body("{\"id\": \"" + transactionId + "\", "
                      + "\"from\": \"" + aWalletId + "\", "
                      + "\"to\": \"" + cWalletId + "\", "
                      + "\"amount\": \"5\"}")
            .post("/api/v1/transaction")
            .then().statusCode(400);
    }

    @Test
    public void should_not_make_binary_mantissa_error() {
        UUID aWalletId = UUID.randomUUID();
        UUID bWalletId = UUID.randomUUID();

        createWalletWithBalance(aWalletId, BigDecimal.valueOf(10));
        createWalletWithBalance(bWalletId, BigDecimal.valueOf(0));

        for (int i = 0; i < 100; i++) {
            given()
                .body("{\"id\": \"" + UUID.randomUUID() + "\", "
                          + "\"from\": \"" + aWalletId + "\", "
                          + "\"to\": \"" + bWalletId + "\", "
                          + "\"amount\": \"0.1\"}")
                .post("/api/v1/transaction")
                .then().statusCode(200);
        }

        BigDecimal aWalletBalance = getWalletBalance(aWalletId);
        BigDecimal bWalletBalance = getWalletBalance(bWalletId);

        assertEquals(BigDecimal.valueOf(0), aWalletBalance);
        assertEquals(BigDecimal.valueOf(10), bWalletBalance);
    }
}
