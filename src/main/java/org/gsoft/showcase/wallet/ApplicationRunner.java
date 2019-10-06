package org.gsoft.showcase.wallet;

import java.net.InetSocketAddress;

public class ApplicationRunner {

    private static final int DEFAULT_PORT = 8080;

    public static void main(String[] args) {
        try {
            int port = getPort(args);

            Application application = new Application(new InetSocketAddress(port));
            application.start();

            System.out.println("Running wallet service at http://localhost:" + port + "/ ...");
            System.out.println("Press Ctrl+C to exit");
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static int getPort(String[] args) {
        if (args.length >= 1) {
            return Integer.parseInt(args[0]);
        } else {
            return DEFAULT_PORT;
        }
    }
}
