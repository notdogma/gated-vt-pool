package com.esp.poller.tasks;

import com.esp.poller.executor.GatedVirtualThreadExecutor;
import com.esp.poller.future.SafeCompletableFuture;
import com.esp.poller.model.EventSim;
import com.esp.poller.model.EventTaskContext;
import com.esp.poller.model.EventTaskEPContext;
import com.esp.poller.model.EventTaskErrContext;
import com.esp.poller.ruleCache.RuleCacheSim;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * This is a simulation of the poller that polling for events from the logger for processing.
 * For this simulation, each event if 'polls' contains 3 assets.
 * <p>
 * Like the real poller, it checks the number of available permits and submits the number of tasks that can be processed based on a percentage of the
 * available permits, in order to account for the task multiplication.
 */
public class LoggerPollerSim implements Runnable {
    private final GatedVirtualThreadExecutor gatedExecutor;

    public LoggerPollerSim( GatedVirtualThreadExecutor gatedExecutor ) {
        this.gatedExecutor = gatedExecutor;
    }

    @Override
    public void run() {
        // See
        int allowedTasks = gatedExecutor.getAvailablePermits();

        System.out.println( "Allowed tasks: " + allowedTasks );

        // Here we're simulating getting events from the logger.
        // For the purposes of this simulation, we'll submit 10% of the available permits because each event has 3 assets and each asset has 3 rules.
        // The mapToObj does the multiplication by 3 assets, but constructing an EventSim object with three assets.
        // The flatMap takes the event with 3 assets and calls the rule cache sim to get the set of rules. The result is a stream of 9 EventTaskDPContext
        // objects.
        // The flatMap then 'randomly' decides to add an EventTaskEPContext object to the stream.
        // The result is a stream of 9 or 10 EventTaskContext objects in this simulation.
        // Next we group the tasks by event id. This lets us submit the tasks for a single event in a batch.
        Map<String, List<ClientTask>> tasks = IntStream.range( 0, allowedTasks / 10 )
                                                       // this is the event
                                                       .mapToObj( i -> new EventSim( "event" + i, new ArrayList<>( List.of( "asset1", "asset2", "asset3" ) ) ) )
                                                       // this creates tasks for each event
                                                       .flatMap( eventSim -> {
                                                           // this simulates getting rules from the rule cache
                                                           Stream<ClientTask> dpStream = new RuleCacheSim().getRules( eventSim ).map( DPTaskSim::new );

                                                           // this simulates that the event is a process complete event
                                                           if( Math.random() > .5 ) {
                                                               Stream<ClientTask> epStream = Stream.of( new EPTaskSim( new EventTaskEPContext( eventSim ) ) );

                                                               return Stream.concat( dpStream, epStream );
                                                           } else
                                                               return dpStream;
                                                       } ).collect( Collectors.groupingBy( t -> t.getEventTaskContext().eventSim().eventId() ) );

        // Now we loop of the entries in the map submit a Runnable to our GatedVirtualThreadExecutor.
        // This runnable takes the list of tasks for each event that was determined above and submits them to the executor via the map function.
        // The result of the tasks for each event are then collected into a List of CompletableFuture objects.
        // The handle function is used to set the result of the CompletableFuture to either SUCCESS or FAILURE.
        // The handle function is called whether the task completed successfully or not. So we need to check whether the exception is null or not.
        // This protects us from unhandled exceptions in the tasks.

        tasks.forEach( ( k, v ) -> {
            // for each event, list pair
            SafeCompletableFuture.safeHandle( gatedExecutor.runAsync( () -> {
                List<CompletableFuture<EventTaskContext>> futures = v.stream().map( callable -> gatedExecutor.supplyAsync( callable ).handle( ( result, e ) -> {
                    // for exceptions, result here will
                    // be null!!!
                    // but e could also be null. one or
                    // the other
                    // will be non-null.
                    try {
                        if( e != null ) {
                            System.err.println( "Task failed (e): " + e.getCause().getMessage() );
                            result = new EventTaskErrContext( EventTaskContext.Result.FAILURE_RETRYABLE );
                        } else
                            result.setResult( EventTaskContext.Result.SUCCESS );
                    } catch( Exception e2 ) {
                        System.err.println( "Task " + "failed (e2): " + e2.getCause().getMessage() );
                        result = new EventTaskErrContext( EventTaskContext.Result.FAILURE_RETRYABLE );
                    }
                    return result;
                } ) ).toList();

                // Now, since the code we have here is actually executed in the Runnable submitted, we can wrap the list of CompletableFuture objects we
                // created
                // just above in a CompletableFuture.allOf() call and join. THe join here will block until all the futures are done.
                // Since we protected the list of Callables above using the handle, we're still protected.
                // Once we get past the join we can determine the course of action based on the results. A clever way to do that is to stream the
                // list of
                // futures,
                // calling map to join each future and then collect the results into a map grouped by the result type. This will give us a Map with;
                // SUCCESS, FAILURE_RETRYABLE and FAILURE_NON_RETRYABLE keys.  Each key will have a List of EventTaskContext objects that were completed
                // with
                // that result type.
                // This makes it super easy to know whether all tasks were successful or if there were any failures.
                //
                System.out.println( k + " Submitted futures: " + futures.size() );
                CompletableFuture.allOf( futures.toArray( CompletableFuture[]::new ) ).join();
                System.out.println( k + " All submitted futures are done. Parent is now running." );
                Map<EventTaskContext.Result, List<EventTaskContext>> results = futures.stream()
                                                                                      .map( CompletableFuture::join )
                                                                                      .collect( Collectors.groupingBy( EventTaskContext::getResult
                                                                                              // Group by the Result
                                                                                      ) );
                System.out.println( k + " Collected results." );

                List<EventTaskContext> success = results.get( EventTaskContext.Result.SUCCESS );
                List<EventTaskContext> failureRetryable = results.get( EventTaskContext.Result.FAILURE_RETRYABLE );
                List<EventTaskContext> failureNonRetryable = results.get( EventTaskContext.Result.FAILURE_NON_RETRYABLE );
                System.out.println( k + " SUCCESS: " + (null != success ? success.size() : 0) );
                System.out.println( k + " FAILURE_RETRYABLE: " + (null != failureRetryable ? failureRetryable.size() : 0) );
                System.out.println( k + " FAILURE_NON_RETRYABLE: " + (null != failureNonRetryable ? failureNonRetryable.size() : 0) );

                // Now we can determine the course of action based on the results.
                // If all tasks were successful, we can call poller to update the status to success.
                // If all tasks failed, we can call poller to update the status to failed.
                // If some tasks failed, we can call poller to update the status to partial success.
                if( null != success && success.size() == futures.size() ) {
                    System.out.println( k + " All submitted futures are done." );
                } else if( null != failureRetryable && failureRetryable.size() == futures.size() ) {
                    System.out.println( k + " All submitted futures are failed, retryable." );
                } else if( null != failureNonRetryable && failureNonRetryable.size() == futures.size() ) {
                    System.out.println( k + " All submitted futures are failed, non-retryable." );
                } else {
                    System.out.println( k + " Some submitted futures are failed as retryable and non-retryable." );
                }

                //todo: call logger to update the event status.
            } ), ( r, ex ) -> r, null );
        } );
    }
}
