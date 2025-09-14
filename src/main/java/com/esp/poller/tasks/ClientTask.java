package com.esp.poller.tasks;

import com.esp.poller.model.EventTaskContext;

import java.util.concurrent.Callable;

public interface ClientTask extends Callable<EventTaskContext> {
    EventTaskContext getEventTaskContext();
}
