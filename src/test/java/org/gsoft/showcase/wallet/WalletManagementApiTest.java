package org.gsoft.showcase.wallet;

import static io.restassured.RestAssured.delete;
import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static org.gsoft.showcase.wallet.util.ApiTestUtil.getWalletBalance;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.text.IsEmptyString.emptyString;
import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.UUID;
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
            .body("balance", equalTo("0"));
    }

    @Test
    public void should_create_wallet_with_given_balance() {
        UUID walletId = UUID.randomUUID();

        given().body("{\"id\": \"" + walletId + "\", \"initialBalance\": \"10\"}")
            .post("/api/v1/wallet")
            .then().statusCode(200);

        get("/api/v1/wallet/" + walletId)
            .then().statusCode(200)
            .body("balance", equalTo("10"));
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
    public void wallet_creation_should_be_idempotent_same_initial_balance() {
        UUID walletId = UUID.randomUUID();

        String body = "{\"id\": \"" + walletId + "\", \"initialBalance\": \"10\"}";

        given().body(body)
            .post("/api/v1/wallet")
            .then().statusCode(200);

        given().body(body)
            .post("/api/v1/wallet")
            .then().statusCode(200);
    }

    @Test
    public void should_return_400_on_creation_existing_id_different_initial_balance() {
        UUID walletId = UUID.randomUUID();

        given().body("{\"id\": \"" + walletId + "\", \"initialBalance\": \"10\"}")
            .post("/api/v1/wallet")
            .then().statusCode(200);

        given().body("{\"id\": \"" + walletId + "\", \"initialBalance\": \"5\"}")
            .post("/api/v1/wallet")
            .then().statusCode(400);

        get("/api/v1/wallet/" + walletId)
            .then().statusCode(200)
            .body("balance", equalTo("10"));
    }

    @Test
    public void should_return_404_when_asked_for_balance_unknown_wallet() {
        get("/api/v1/wallet/" + UUID.randomUUID())
            .then().statusCode(404);
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

    @Test
    public void should_store_many_significant_digits_without_error() {
        UUID walletId = UUID.randomUUID();

        String numberString = "123674.23455732464576566532223534214";

        given().body("{\"id\": \"" + walletId + "\", \"initialBalance\": \"" + numberString + "\"}")
            .post("/api/v1/wallet")
            .then().statusCode(200);

        assertEquals(new BigDecimal(numberString), getWalletBalance(walletId));
    }
}
