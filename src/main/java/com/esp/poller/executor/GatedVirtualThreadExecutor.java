package com.esp.poller.executor;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Gated virtual thread executor.
 * This uses a 'new virtual thread per task' executor service, instead of an old-fashioned thread pool. This is a newer concept in Java 21, given how light
 * virtual threads are to create and destroy.
 * It uses a Semaphore to gate the number of concurrent tasks. This is a simple way to control the number of concurrent tasks. Semaphores aren't new.
 * It also uses AtomicLong to track the number of active and queued tasks. This is a simple way to track the number of active and queued tasks.
 * It has two methods to submit work: supplyAsync and runAsync. These are similar to the ones in ExecutorService.
 *
 * @author esp
 */
public class GatedVirtualThreadExecutor {
    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
    private final Semaphore gate;
    private final AtomicLong activeCount = new AtomicLong( 0 );
    private final AtomicLong queuedCount = new AtomicLong( 0 );

    public GatedVirtualThreadExecutor( int maxConcurrent ) {
        this.gate = new Semaphore( maxConcurrent );
    }

    public <T> CompletableFuture<T> supplyAsync( Callable<T> task ) {
        queuedCount.incrementAndGet();
        AtomicBoolean wasDecremented = new AtomicBoolean( false );

        return CompletableFuture.supplyAsync( () -> {
            try {
//                System.out.println( "Queued count in: " + queuedCount.get() + ". Active count in: " + activeCount.get() );
                gate.acquire(); // Block if too many active
                queuedCount.decrementAndGet();
                wasDecremented.set( true );
                activeCount.incrementAndGet();

                try {
                    return task.call();
                } finally {
                    activeCount.decrementAndGet();
                    gate.release();
//                    System.out.println( "Queued count: " + queuedCount.get() + ". Active count: " + activeCount.get() );
                }
            } catch( InterruptedException e ) {
                Thread.currentThread().interrupt();
                if( !wasDecremented.get() )
                    queuedCount.decrementAndGet();
                throw new CompletionException( "Task was interrupted", e );
            } catch( Exception e ) {
                if( !wasDecremented.get() )
                    queuedCount.decrementAndGet();
                throw new CompletionException( "Task failed", e );
            }
        }, executor );
    }

    public CompletableFuture<Void> runAsync( Runnable task ) {
        queuedCount.incrementAndGet();
        AtomicBoolean wasDecremented = new AtomicBoolean( false );

        return CompletableFuture.runAsync( () -> {
            try {
                gate.acquire(); // Block if too many active
                queuedCount.decrementAndGet();
                wasDecremented.set( true );
                activeCount.incrementAndGet();

                try {
                    task.run();
                } finally {
                    activeCount.decrementAndGet();
                    gate.release();
                }
            } catch( InterruptedException e ) {
                Thread.currentThread().interrupt();
                if( !wasDecremented.get() )
                    queuedCount.decrementAndGet();
                throw new CompletionException( "Task was interrupted", e );
            } catch( Exception e ) {
                if( !wasDecremented.get() )
                    queuedCount.decrementAndGet();
                throw new CompletionException( "Task failed", e );
            }
        }, executor );
    }

    public long getActiveCount() {
        return activeCount.get();
    }

    public long getQueuedCount() {
        return queuedCount.get();
    }

    public int getAvailablePermits() {
        return gate.availablePermits();
    }

    public boolean hasCapacity() {
        return gate.availablePermits() > 0;
    }
}
