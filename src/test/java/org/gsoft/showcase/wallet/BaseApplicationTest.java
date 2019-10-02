package org.gsoft.showcase.wallet;

import io.restassured.RestAssured;
import java.net.InetSocketAddress;
import org.junit.After;
import org.junit.Before;

public abstract class BaseApplicationTest {

    protected Application application;

    @Before
    public void setUp() throws Exception {
        application = new Application(new InetSocketAddress(0));
        application.start();

        RestAssured.port = application.getAddress().getPort();
    }

    @After
    public void tearDown() throws Exception {
        application.stop();
    }
}
