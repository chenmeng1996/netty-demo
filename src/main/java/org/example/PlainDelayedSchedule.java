package org.example;

import java.util.concurrent.*;

/**
 * jdk实现延时调度
 *
 * @author Chen Meng
 */
public class PlainDelayedSchedule {
    public static void main(String[] args) {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(10);

        ScheduledFuture<?> future = executor.schedule(new Runnable() {
            @Override
            public void run() {
                System.out.println("10 seconds later");
            }
        }, 5, TimeUnit.SECONDS);
        try {
            future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        executor.shutdown();
    }
}
