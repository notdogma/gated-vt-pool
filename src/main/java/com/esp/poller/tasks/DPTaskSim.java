package com.esp.poller.tasks;

import com.esp.poller.exception.NonRetryableException;
import com.esp.poller.exception.RetryableException;
import com.esp.poller.model.EventTaskContext;
import com.esp.poller.model.EventTaskDPContext;

import java.util.concurrent.Callable;

/**
 * Simulated DP task implemented as a Callable. For the purposes of this simulation, it takes an EventTaskDPContext and returns an EventTaskContext with the
 * result set.
 * The real implementation would be a call to the DP service.
 */
public record DPTaskSim(EventTaskDPContext context) implements ClientTask {

    @Override
    public EventTaskContext call() throws Exception {
        int randSleep = (int) (Math.random() * 1000);
        Thread.sleep( Math.min( Math.max( randSleep, 300 ), 600 ) );
        context.setResult( EventTaskContext.Result.SUCCESS );
//        System.out.println( "DP task completed: " + context );

        if( Math.random() < .02 ) {
            throw new NonRetryableException( "Simulated non-retryable failure!!!" );
        }
        if( Math.random() < .03 ) {
            throw new RetryableException( "Simulated retryable failure!!!" );
        }

        return context;
    }

    @Override
    public EventTaskContext getEventTaskContext() {
        return context;
    }
}
