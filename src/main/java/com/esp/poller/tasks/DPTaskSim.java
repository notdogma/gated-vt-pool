package com.esp.poller.tasks;

import com.esp.poller.model.EventTaskContext;
import com.esp.poller.model.EventTaskDPContext;

import java.util.concurrent.Callable;

/**
 * Simulated DP task implemented as a Callable. For the purposes of this simulation, it takes an EventTaskDPContext and returns an EventTaskContext with the
 * result set.
 * The real implementation would be a call to the DP service.
 */
public record DPTaskSim(EventTaskDPContext context) implements Callable<EventTaskContext> {

    @Override
    public EventTaskContext call() throws Exception {
        int randSleep = (int) (Math.random() * 1000);
        Thread.sleep( Math.min( Math.max( randSleep, 300 ), 600 ) );
        context.setResult( EventTaskContext.Result.SUCCESS );
        return context;
    }
}
