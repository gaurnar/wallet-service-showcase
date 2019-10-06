package org.gsoft.showcase.wallet.util;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;

import java.math.BigDecimal;
import java.util.UUID;

public final class ApiTestUtil {

    public static void createWalletWithBalance(UUID id, BigDecimal balance) {
        given().body("{\"id\": \"" + id + "\", \"initialBalance\": \"" + balance + "\"}")
            .post("/api/v1/wallet")
            .then().statusCode(200);
    }

    public static BigDecimal getWalletBalance(UUID id) {
        String balanceString = get("/api/v1/wallet/" + id)
            .then().statusCode(200)
            .extract().jsonPath().getString("balance");
        return new BigDecimal(balanceString);
    }
}
