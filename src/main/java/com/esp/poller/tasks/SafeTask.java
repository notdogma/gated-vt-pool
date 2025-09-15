package com.esp.poller.tasks;

public class SafeTask {
    public static Runnable safeRunnable( Runnable task ) {
        return () -> {
            try {
                task.run();
            } catch( Exception e ) {
                System.out.println( "Task execution failed: " + e );
                // Could add: circuit breaker, exponential backoff, etc.
            }
        };
    }
}
