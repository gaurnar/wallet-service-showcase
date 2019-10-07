package org.gsoft.showcase.wallet.util;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;

import java.math.BigDecimal;
import java.util.UUID;

public final class ApiTestUtil {

    public static UUID createWalletWithBalance(BigDecimal balance) {
        String uuidString =
            given().body("{\"initialBalance\": \"" + balance + "\"}")
                .post("/api/v1/wallet")
                .then().statusCode(200)
                .extract().path("id");
        return UUID.fromString(uuidString);
    }

    public static BigDecimal getWalletBalance(UUID id) {
        String balanceString = get("/api/v1/wallet/" + id)
            .then().statusCode(200)
            .extract().jsonPath().getString("balance");
        return new BigDecimal(balanceString);
    }
}
