package com.esp.poller.tasks;

import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

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

    public static <T> Callable<T> safeCallable( Callable<T> task ) {
        return () -> {
            try {
                return task.call();
            } catch( Exception e ) {
                System.out.println( "Task execution failed: " + e );
                // Could add: circuit breaker, exponential backoff, etc.
                return null;
            }
        };
    }

    public static <T> Consumer<T> safeConsumer( Consumer<T> task ) {
        return ( T t ) -> {
            try {
                task.accept( t );
            } catch( Exception e ) {
                System.out.println( "Task execution failed: " + e );
                // Could add: circuit breaker, exponential backoff, etc.
            }
        };
    }

    public static <T> Supplier<T> safeSupplier( Supplier<T> task ) {
        return () -> {
            try {
                return task.get();
            } catch( Exception e ) {
                System.out.println( "Task execution failed: " + e );
                // Could add: circuit breaker, exponential backoff, etc.
                return null;
            }
        };
    }

    public static <R, T> Function<R, T> safeFunction( Function<R, T> task ) {
        return ( R r ) -> {
            try {
                return task.apply( r );
            } catch( Exception e ) {
                System.out.println( "Task execution failed: " + e );
                // Could add: circuit breaker, exponential backoff, etc.
                return null;
            }
        };
    }
}
