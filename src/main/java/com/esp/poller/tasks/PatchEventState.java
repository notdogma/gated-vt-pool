package com.esp.poller.tasks;

import com.esp.poller.model.EventSim;

import java.util.function.Consumer;

public class PatchEventState implements Consumer<EventSim> {
    @Override
    public void accept( EventSim eventSim ) {
        try {
            // put code here
        } catch( Exception e ) {
            throw new RuntimeException( "Error applying function", e );
        }
    }
}
