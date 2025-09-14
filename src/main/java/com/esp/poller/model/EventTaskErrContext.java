package com.esp.poller.model;

import lombok.Getter;
import lombok.Setter;

public class EventTaskErrContext implements EventTaskContext {
    @Getter
    @Setter
    private Result result;

    public EventTaskErrContext( Result result ) {
        this.result = result;
    }

    @Override
    public EventSim eventSim() {
        return null;
    }
}
