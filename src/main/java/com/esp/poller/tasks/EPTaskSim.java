package com.esp.poller.tasks;

import com.esp.poller.model.EventTaskContext;
import com.esp.poller.model.EventTaskEPContext;

import java.util.concurrent.Callable;

/**
 * Simulated EP task implemented as a Callable. For the purposes of this simulation, it takes an EventTaskDPContext and returns an EventTaskContext with the
 * result set.
 * The real implementation would be a call to the EP service.
 */
public record EPTaskSim(EventTaskEPContext context) implements ClientTask {

    @Override
    public EventTaskContext call() throws Exception {
        int randSleep = (int) (Math.random() * 1000);
        Thread.sleep( Math.min( Math.max( randSleep, 300 ), 600 ) );
        context.setResult( EventTaskContext.Result.SUCCESS );
//        System.out.println( "EP task completed: " + context );
        return context;
    }

    @Override
    public EventTaskContext getEventTaskContext() {
        return context;
    }
}
