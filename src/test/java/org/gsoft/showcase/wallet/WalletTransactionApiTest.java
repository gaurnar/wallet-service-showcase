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

        UUID aWalletId = createWalletWithBalance(BigDecimal.valueOf(10));
        UUID bWalletId = createWalletWithBalance(BigDecimal.valueOf(0));

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

        UUID aWalletId = createWalletWithBalance(BigDecimal.valueOf(1));
        UUID bWalletId = createWalletWithBalance(BigDecimal.valueOf(0));

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

        UUID aWalletId = createWalletWithBalance(BigDecimal.valueOf(10));
        UUID bWalletId = createWalletWithBalance(BigDecimal.valueOf(0));

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
    public void should_return_404_on_unknown_from() {
        UUID transactionId = UUID.randomUUID();

        UUID aWalletId = UUID.randomUUID();
        UUID bWalletId = createWalletWithBalance(BigDecimal.valueOf(0));

        final String body = "{\"id\": \"" + transactionId + "\", "
            + "\"from\": \"" + aWalletId + "\", "
            + "\"to\": \"" + bWalletId + "\", "
            + "\"amount\": \"5\"}";

        given().body(body).post("/api/v1/transaction")
            .then().statusCode(404);

        BigDecimal bWalletBalance = getWalletBalance(bWalletId);

        assertEquals(BigDecimal.valueOf(0), bWalletBalance);
    }

    @Test
    public void should_return_404_on_unknown_to() {
        UUID transactionId = UUID.randomUUID();

        UUID aWalletId = createWalletWithBalance(BigDecimal.valueOf(10));
        UUID bWalletId = UUID.randomUUID();

        final String body = "{\"id\": \"" + transactionId + "\", "
            + "\"from\": \"" + aWalletId + "\", "
            + "\"to\": \"" + bWalletId + "\", "
            + "\"amount\": \"5\"}";

        given().body(body).post("/api/v1/transaction")
            .then().statusCode(404);

        BigDecimal aWalletBalance = getWalletBalance(aWalletId);

        assertEquals(BigDecimal.valueOf(10), aWalletBalance);
    }

    @Test
    public void should_return_404_on_unknown_from_and_to() {
        UUID transactionId = UUID.randomUUID();
        UUID aWalletId = UUID.randomUUID();
        UUID bWalletId = UUID.randomUUID();

        final String body = "{\"id\": \"" + transactionId + "\", "
            + "\"from\": \"" + aWalletId + "\", "
            + "\"to\": \"" + bWalletId + "\", "
            + "\"amount\": \"5\"}";

        given().body(body).post("/api/v1/transaction")
            .then().statusCode(404);
    }

    @Test
    public void should_return_400_on_same_transaction_id_different_amount() {
        UUID transactionId = UUID.randomUUID();

        UUID aWalletId = createWalletWithBalance(BigDecimal.valueOf(10));
        UUID bWalletId = createWalletWithBalance(BigDecimal.valueOf(0));

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

        BigDecimal aWalletBalance = getWalletBalance(aWalletId);
        BigDecimal bWalletBalance = getWalletBalance(bWalletId);

        assertEquals(BigDecimal.valueOf(5), aWalletBalance);
        assertEquals(BigDecimal.valueOf(5), bWalletBalance);
    }

    @Test
    public void should_return_400_on_same_transaction_id_different_from() {
        UUID transactionId = UUID.randomUUID();

        UUID aWalletId = createWalletWithBalance(BigDecimal.valueOf(10));
        UUID bWalletId = createWalletWithBalance(BigDecimal.valueOf(0));
        UUID cWalletId = createWalletWithBalance(BigDecimal.valueOf(10));

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

        BigDecimal aWalletBalance = getWalletBalance(aWalletId);
        BigDecimal bWalletBalance = getWalletBalance(bWalletId);
        BigDecimal cWalletBalance = getWalletBalance(cWalletId);

        assertEquals(BigDecimal.valueOf(5), aWalletBalance);
        assertEquals(BigDecimal.valueOf(5), bWalletBalance);
        assertEquals(BigDecimal.valueOf(10), cWalletBalance);
    }

    @Test
    public void should_return_400_on_same_transaction_id_different_to() {
        UUID transactionId = UUID.randomUUID();

        UUID aWalletId = createWalletWithBalance(BigDecimal.valueOf(10));
        UUID bWalletId = createWalletWithBalance(BigDecimal.valueOf(0));
        UUID cWalletId = createWalletWithBalance(BigDecimal.valueOf(10));

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

        BigDecimal aWalletBalance = getWalletBalance(aWalletId);
        BigDecimal bWalletBalance = getWalletBalance(bWalletId);
        BigDecimal cWalletBalance = getWalletBalance(cWalletId);

        assertEquals(BigDecimal.valueOf(5), aWalletBalance);
        assertEquals(BigDecimal.valueOf(5), bWalletBalance);
        assertEquals(BigDecimal.valueOf(10), cWalletBalance);
    }

    @Test
    public void should_return_400_on_same_transaction_id_all_different() {
        UUID transactionId = UUID.randomUUID();

        UUID aWalletId = createWalletWithBalance(BigDecimal.valueOf(10));
        UUID bWalletId = createWalletWithBalance(BigDecimal.valueOf(0));
        UUID cWalletId = createWalletWithBalance(BigDecimal.valueOf(10));
        UUID dWalletId = createWalletWithBalance(BigDecimal.valueOf(0));

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
                      + "\"to\": \"" + dWalletId + "\", "
                      + "\"amount\": \"5\"}")
            .post("/api/v1/transaction")
            .then().statusCode(400);

        BigDecimal aWalletBalance = getWalletBalance(aWalletId);
        BigDecimal bWalletBalance = getWalletBalance(bWalletId);
        BigDecimal cWalletBalance = getWalletBalance(cWalletId);
        BigDecimal dWalletBalance = getWalletBalance(dWalletId);

        assertEquals(BigDecimal.valueOf(5), aWalletBalance);
        assertEquals(BigDecimal.valueOf(5), bWalletBalance);
        assertEquals(BigDecimal.valueOf(10), cWalletBalance);
        assertEquals(BigDecimal.valueOf(0), dWalletBalance);
    }

    @Test
    public void should_not_make_binary_mantissa_error_on_repeated_addition() {
        UUID aWalletId = createWalletWithBalance(BigDecimal.valueOf(10));
        UUID bWalletId = createWalletWithBalance(BigDecimal.valueOf(0));

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
