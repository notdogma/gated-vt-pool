package com.esp.poller;

import com.esp.poller.executor.GatedVirtualThreadExecutor;
import com.esp.poller.tasks.LoggerPollerSim;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * This is a simulation of the poller service that polls for events from the logger for processing.
 * It contains the scheduled executor service, like the real poller service.
 * It has a gated executor service to control the number of concurrent tasks.
 * There is no queue of tasks like we have now. We avoid that overhead, leaving the tasks in the database until we have capacity to process them.
 * We use the gated executor to control the number of concurrent tasks. We can dramatically increase the number of concurrent tasks. Out memory will
 * stay low since we only pull what we can process at any given time.
 */
public class PollerServiceSim {
    private final ScheduledExecutorService pollerExecutor;
    private final GatedVirtualThreadExecutor gatedExecutor;

    public PollerServiceSim( int maxConcurrentTasks ) {
        pollerExecutor = Executors.newScheduledThreadPool( 1 );
        gatedExecutor = new GatedVirtualThreadExecutor( maxConcurrentTasks );
    }

    public void start() {
        pollerExecutor.scheduleWithFixedDelay( new LoggerPollerSim( gatedExecutor ), 1, 5, TimeUnit.SECONDS );
    }

    public void stop() {
        pollerExecutor.shutdown();
    }
}
