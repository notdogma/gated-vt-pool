package com.esp.poller.future;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Function;

public class SafeCompletableFuture {

    public static <T> CompletableFuture<T> safeExceptionally(
            CompletableFuture<T> future,
            Function<Throwable, T> handler,
            T ultimateFallback ) {

        return future.exceptionally( throwable -> {
            try {
                return handler.apply( throwable );
            } catch( Exception e ) {
                System.out.println( "Exception handler failed: " + e );
                return ultimateFallback;
            }
        } );
    }

    public static <T, R> CompletableFuture<R> safeHandle(
            CompletableFuture<T> future,
            BiFunction<T, Throwable, R> handler,
            R ultimateFallback ) {

        return future.handle( ( result, throwable ) -> {
            try {
                return handler.apply( result, throwable );
            } catch( Exception e ) {
                System.out.println( "Handle method failed: " + e );
                return ultimateFallback;
            }
        } );
    }
}
