package com.esp.poller.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Context for a EP task. This has the event that will be sent to EP for processing.
 * It also has a result field that will be set base on the result of the EP call.
 */
@ToString
public class EventTaskEPContext implements EventTaskContext {
    private final EventSim eventSim;
    @Getter
    @Setter
    private Result result;

    public EventTaskEPContext( EventSim eventSim ) {
        this.eventSim = eventSim;
    }

    @Override
    public EventSim eventSim() {
        return eventSim;
    }
}
