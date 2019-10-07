package org.gsoft.showcase.wallet;

import static io.restassured.RestAssured.delete;
import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static org.gsoft.showcase.wallet.util.ApiTestUtil.createWalletWithBalance;
import static org.gsoft.showcase.wallet.util.ApiTestUtil.getWalletBalance;
import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.UUID;
import org.junit.Test;

public class WalletManagementApiTest extends BaseApplicationTest {

    @Test
    public void should_create_wallet_without_specified_balance() {
        String uuidString =
            given().body("{}")
                .post("/api/v1/wallet")
                .then().statusCode(200)
                .extract().path("id");

        BigDecimal walletBalance = getWalletBalance(UUID.fromString(uuidString));

        assertEquals(BigDecimal.ZERO, walletBalance);
    }

    @Test
    public void should_create_wallet_with_given_balance() {
        UUID walletId = createWalletWithBalance(BigDecimal.valueOf(10));

        BigDecimal walletBalance = getWalletBalance(walletId);

        assertEquals(BigDecimal.valueOf(10), walletBalance);
    }

    @Test
    public void should_return_404_when_asked_for_balance_unknown_wallet() {
        get("/api/v1/wallet/" + UUID.randomUUID())
            .then().statusCode(404);
    }

    @Test
    public void should_remove_wallet() {
        UUID walletId = createWalletWithBalance(BigDecimal.ZERO);

        delete("/api/v1/wallet/" + walletId)
            .then().statusCode(200);

        get("/api/v1/wallet/" + walletId)
            .then().statusCode(404);
    }

    @Test
    public void should_store_many_significant_digits_without_error() {
        BigDecimal amount = new BigDecimal("123674.23455732464576566532223534214");

        UUID walletId = createWalletWithBalance(amount);

        assertEquals(amount, getWalletBalance(walletId));
    }
}
