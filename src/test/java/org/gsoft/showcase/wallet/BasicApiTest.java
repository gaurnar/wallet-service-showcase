package org.gsoft.showcase.wallet;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;

import org.junit.Test;

public class BasicApiTest extends BaseApplicationTest {

    @Test
    public void should_return_400_on_invalid_json() {
        given().body("foobar")
            .post("/api/v1/wallet")
            .then().statusCode(400);
    }

    @Test
    public void should_return_400_on_mapping_failure() {
        given().body("{\"foo\":\"bar\"}")
            .post("/api/v1/wallet")
            .then().statusCode(400);
    }

    @Test
    public void should_return_404_on_unknown_url() {
        get("/api/v1/foo/bar")
            .then().statusCode(404);
    }
}
