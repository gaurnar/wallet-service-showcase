package org.gsoft.showcase.wallet.util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class ParallelActionRunner {
    private final CountDownLatch startLatch;
    private final int numberOfThreads;
    private final Runnable action;

    public ParallelActionRunner(int numberOfThreads, Runnable action) {
        startLatch = new CountDownLatch(1);
        this.numberOfThreads = numberOfThreads;

        this.action = () -> {
            try {
                startLatch.await();
            } catch (InterruptedException ignored) {
            }

            action.run();
        };
    }

    public void run() {
        List<Thread> threads = new ArrayList<>(numberOfThreads);

        for (int i = 0; i < numberOfThreads; i++) {
            Thread thread = new Thread(action);
            threads.add(thread);
            thread.start();
        }

        startLatch.countDown();

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException ignored) {
            }
        }
    }
}
