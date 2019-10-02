package org.gsoft.showcase.wallet;

import static io.restassured.RestAssured.expect;

import org.junit.Test;

public class ApplicationStartTest extends BaseApplicationTest {

    @Test
    public void health_check_returns_200() {
        expect().statusCode(200).when().get("/api/health-check");
    }
}
