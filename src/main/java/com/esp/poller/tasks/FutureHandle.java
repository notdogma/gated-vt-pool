package com.esp.poller.tasks;

import com.esp.poller.exception.NonRetryableException;
import com.esp.poller.exception.RetryableException;
import com.esp.poller.model.EventTaskContext;
import com.esp.poller.model.EventTaskErrContext;

import java.util.function.BiFunction;

/**
 * This is a BiFunction that is used to handle the result of a CompletableFuture.
 * It takes an EventTaskContext and a Throwable as parameters and returns an EventTaskContext.
 * It is used to handle the result of a CompletableFuture in a functional way.
 */
public class FutureHandle implements BiFunction<EventTaskContext, Throwable, EventTaskContext> {
    @Override
    public EventTaskContext apply( EventTaskContext result, Throwable e ) {
        // for exceptions, result here will be null!!!
        // but e could also be null. one or the other will be non-null.
        try {
            if( e != null ) {
                System.err.println( "Task failed (e): " + e );
                if( e.getCause() instanceof RetryableException ) {
                    result = new EventTaskErrContext( EventTaskContext.Result.FAILURE_RETRYABLE );
                } else if( e.getCause() instanceof NonRetryableException ) {
                    result = new EventTaskErrContext( EventTaskContext.Result.FAILURE_NON_RETRYABLE );
                } else {
                    result = new EventTaskErrContext( EventTaskContext.Result.FAILURE_RETRYABLE );
                }
            } else
                result.setResult( EventTaskContext.Result.SUCCESS );
        } catch( Exception e2 ) {
            System.err.println( "Task " + "failed (e2): " + e2.getCause().getMessage() );
            result = new EventTaskErrContext( EventTaskContext.Result.FAILURE_RETRYABLE );
        }
        return result;
    }
}
